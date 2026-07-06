import { useState } from 'react';
import { postTransfer } from '../api/client';
import type { Account, TransferResponse } from '../api/types';
import { toast } from 'react-toastify';

type TransferFormProps = {
  accounts: Account[] | { data: Account[] } | null | undefined;
  onTransferComplete: () => void;
};

export function TransferForm({ accounts, onTransferComplete }: TransferFormProps) {
  const [fromAccount, setFromAccount] = useState<string>('');
  const [toAccount, setToAccount] = useState<string>('');
  const [amount, setAmount] = useState<string>('');
  const [submitting, setSubmitting] = useState<boolean>(false);
  const [message, setMessage] = useState<string | null>(null);
  const [messageType, setMessageType] = useState<'success' | 'error' | null>(null);
  const [normalizedResult, setNormalizedResult] = useState<Partial<TransferResponse> | null>(null);

  const accountOptions = Array.isArray(accounts)
    ? accounts
    : Array.isArray(accounts?.data)
      ? accounts.data
      : [];

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setMessage(null);
    setMessageType(null);

    const amountNumber = parseFloat(amount);
    if (!fromAccount || !toAccount || isNaN(amountNumber) || amountNumber <= 0) {
      setMessage('Please fill in all fields with valid values.');
      setMessageType('error');
      return;
    }

    if (fromAccount === toAccount) {
      setMessage('From and to accounts must be different.');
      setMessageType('error');
      return;
    }

    setSubmitting(true);
    try {
      const result = await postTransfer({
        fromAccountNumber: fromAccount,
        toAccountNumber: toAccount,
        amount: amountNumber,
      });

      const normalized = result && typeof result === 'object' && 'data' in result
        ? (result as { data?: Partial<TransferResponse> }).data ?? result
        : result;
      
      setNormalizedResult(normalized);

      if (normalized?.status === 'FAILED') {
        toast.error('Transfer failed. Check the accounts and that the source has sufficient funds.');
      } else {
        const transferId = normalized?.transactionId || normalized?.transferId;
        toast.success(`Transfer complete. Transaction ID: ${transferId}`);
        setFromAccount('');
        setToAccount('');
        setAmount('');
        onTransferComplete();
      }
    } catch (e) {
      toast.error(e instanceof Error ? e.message : 'Transfer failed.');
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <section className="transfer-form">
      <h2>Transfer Funds</h2>
      <form onSubmit={handleSubmit}>
        <div className="form-row">
          <label htmlFor="from-account">From Account</label>
          <select
            id="from-account"
            value={fromAccount}
            onChange={(e) => setFromAccount(e.target.value)}
          >
            <option value="">-- Select --</option>
            {accountOptions.map((a) => (
              <option key={a.accountNumber} value={a.accountNumber}>
                {a.accountNumber} ({a.accountType}, ${a.balance.toFixed(2)})
              </option>
            ))}
          </select>
        </div>
        <div className="form-row">
          <label htmlFor="to-account">To Account</label>
          <select
            id="to-account"
            value={toAccount}
            onChange={(e) => setToAccount(e.target.value)}
          >
            <option value="">-- Select --</option>
            {accountOptions.map((a) => (
              <option key={a.accountNumber} value={a.accountNumber}>
                {a.accountNumber} ({a.accountType}, ${a.balance.toFixed(2)})
              </option>
            ))}
          </select>
        </div>
        <div className="form-row">
          <label htmlFor="amount">Amount</label>
          <input
            id="amount"
            type="number"
            step="0.01"
            min="0.01"
            value={amount}
            onChange={(e) => setAmount(e.target.value)}
          />
        </div>
        <button type="submit" disabled={submitting}>
          {submitting ? 'Processing...' : 'Submit Transfer'}
        </button>
        {/* Toast messages now handled by react-toastify via toast() calls */}
      </form>
    </section>
  );
}