import { useState, useEffect, useCallback } from 'react';
import { Header } from './components/Header';
import { AccountList } from './components/AccountList';
import { TransferForm } from './components/TransferForm';
import { getAccounts } from './api/client';
import type { Account } from './api/types';
import './App.css';
import MatButton from './components/common/MatButton';

import SendIcon from '@mui/icons-material/Send';
import PaymentIcon from '@mui/icons-material/Payment';
import AccountBalanceIcon from '@mui/icons-material/AccountBalance';
import RemoveCircleIcon from '@mui/icons-material/RemoveCircle';
import Payment from './features/payment';
import Deposit from './features/deposit';
import Withdrawal from './features/withdrawal';
import { AccountsContext, TitleContext } from './components/common/TitleContext';
import { useAuth } from './auth/AuthContext';
import { SignInScreen } from './components/SignInScreen';

export function App() {
  const [accounts, setAccounts] = useState<Account[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const { user, loading: authLoading } = useAuth();

  const loadAccounts = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await getAccounts();
      setAccounts(data);
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Unknown error');
    } finally {
      setLoading(false);
    }
  }, []);

  // const transferTitle = 'Transfer';
  // const paymentTitle = 'Payment';
  // const depositTitle = 'Deposit';
  // const withdrawalTitle = 'Withdrawal';

  const transactionOptions: ITransactionOptions[] = [
    { id: 1, title: 'Transfer', icon: <SendIcon /> },
    { id: 2, title: 'Payment', icon: <PaymentIcon /> },
    { id: 3, title: 'Deposit', icon: <AccountBalanceIcon /> },
    { id: 4, title: 'Withdrawal', icon: <RemoveCircleIcon /> },
  ];

  const [show, setShow] = useState<string | null>(null);
  const [theTitle, setTheTitle] = useState<string | null>(null);

  // useEffect(() => {
  //   loadAccounts();
  // }, [loadAccounts]);

  // Load accounts once a user becomes available (the auth gate).
  useEffect(() => {
    if (user) {
      loadAccounts();
    }
  }, [user, loadAccounts]);

  function handleTransactionClick(title: string) {
    if (title !== null) {
      setTheTitle(title);
      setShow(title);
    }
 else {
      setShow(null);
    }
  }

  return (
    <TitleContext.Provider value={theTitle}>
      <AccountsContext.Provider value={accounts}>
        <div className="app">
          <Header />
          <main>
            {authLoading && <p className="status-message">Checking sign-in state...</p>}
            {!authLoading && !user && <SignInScreen />}
            {!authLoading && user && (
              <>
                <AccountList
                  accounts={accounts}
                  loading={loading}
                  error={error}
                />
                <div className="button-row">
                  {transactionOptions.map((title) => (
                    <div className="button-item" key={title.id}>
                      <MatButton
                        title={title.title}
                        icon={title.icon}
                        handleButtonClick={() => {
                          handleTransactionClick(title.title);
                        }}
                        fullWidth
                      />
                    </div>
                  ))}
                </div>
                {show === 'Transfer' && (
          <TransferForm
            accounts={accounts}
            onTransferComplete={loadAccounts}
          />
        )}
        {show === 'Payment' && (
          <div className="payment-form">
            {/* Payment form component will go here */}
            <Payment 
                title={theTitle}
                accounts={accounts} />
          </div>
        )}
        {show === 'Deposit' && (
          <div className="payment-form">
            {/* Payment form component will go here */}
            <Deposit />
          </div>
        )}
        {show === 'Withdrawal' && (
          <div className="payment-form">
            {/* Payment form component will go here */}
            <Withdrawal />
          </div>
        )}
                {/* <div className="button-row">
                  {transactionOptions.map((title) => (
                    <div className="button-item" key={title.id}>
                      <MatButton
                        title={title.title}
                        icon={title.icon}
                        handleButtonClick={() => {
                          handleTransactionClick(title.title);
                        }}
                        fullWidth
                      />
                    </div>
                  ))}
                </div> */}
              </>
            )}
          </main>
        </div>
      </AccountsContext.Provider>
    </TitleContext.Provider>
  );
}

export interface ITransactionOptions {
  id: number;
  title: string;
  icon?: React.ReactNode;
}