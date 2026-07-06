import { FormEvent, useContext, useState } from "react";
import { AccountsContext, TitleContext } from "../common/TitleContext";
import { IPaymentFacilities } from "../../features/payment";
import { Account, GenericResponse } from "../../api/types";
import { postDeposit, postPayment, postWithdrawal } from "../../api/client";

export default function GeneralForm( { paymentComps, selectedValue, onChange, btnTitle, labelDescription, onActionComplete }: { paymentComps?: IPaymentFacilities[]; selectedValue?: number; onChange?: (value: IPaymentFacilities | null) => void; btnTitle?: string; labelDescription?: string; onActionComplete: () => void; }) {
    const title = useContext(TitleContext);
    const accounts = useContext(AccountsContext);
    
    // Check if accounts has a data property and convert it, otherwise use as array
    const accountList: Account[] = Array.isArray(accounts)
      ? accounts
      : accounts && typeof accounts === 'object' && 'data' in accounts && Array.isArray((accounts as Record<string, unknown>).data)
        ? (accounts as Record<string, unknown>).data as Account[]
        : [];
    
    const [fromAccount, setFromAccount] = useState<string>('');
    const [, setToAccount] = useState<string>('');
    const [amount, setAmount] = useState<string>('');
    const [submitting, setSubmitting] = useState<boolean>(false);
    const [message, setMessage] = useState<string | null>(null);
    const[messageType, setMessageType] = useState<'success' | 'error' | null>(null);
  
  
  
  function handlePaymentChange(event: React.ChangeEvent<HTMLSelectElement>) {
    const selectedId = parseInt(event.target.value);
    const selectedComp = paymentComps?.find((comp) => comp.id === selectedId) || null;
    onChange?.(selectedComp);
  }

  async function handleSubmit(event: FormEvent<HTMLFormElement>): Promise<void> {
    event.preventDefault();
    const selectedAccount = accountList.find(a => a.accountNumber === fromAccount);
    const amountNumber = parseFloat(amount);
  
    // Validation
    if (!fromAccount || !amountNumber || amountNumber <= 0) {
     console.error('Invalid input');
     return;
   }
    // Use the values
    //console.log({ btnTitle, selectedAccount, amount: amountNumber });
    // Call your API or update state here
    try {
          let result: GenericResponse | undefined;
          const selectedCompanyName = btnTitle === 'Submit Payment'
            ? paymentComps?.find((comp) => comp.id === selectedValue)?.name || ''
            : '';
          
          if(btnTitle === 'Deposit') {
            result = await postDeposit({
              accountNumber: selectedAccount?.accountNumber || '',
              amount: amountNumber,
            });
          }
          else if(btnTitle === 'Withdraw') {
              result = await postWithdrawal({
                accountNumber: selectedAccount?.accountNumber || '',
                amount: amountNumber, // Placeholder for withdrawal
              });
          }
          else if(btnTitle === 'Submit Payment') {
            result = await postPayment({
              accountNumber: selectedAccount?.accountNumber || '',
              payee: selectedCompanyName,
              amount: amountNumber,
            });
          }

          

          const normalizedResult = result && typeof result === 'object' && 'data' in result
                  ? (result as { data?: Partial<GenericResponse> }).data ?? result
                  : result;

          if (result?.status === 'FAILED') {
            setMessage(`${btnTitle} failed. Check the account and/or the amount.`);
            setMessageType('error');
          } else {
            const transactionIdId = normalizedResult?.transactionId
            setMessage(`${btnTitle} complete. Transaction ID: ${transactionIdId}`);
            setMessageType('success');
            setFromAccount('');
            setToAccount('');
            setAmount('');
            onActionComplete();
          }
        } catch (e) {
          setMessage(e instanceof Error ? e.message : `${btnTitle} failed.`);
          setMessageType('error');
        } finally {
          setSubmitting(false);
        }
  }

   return (
//     <div className="general-form">
//       <h2>General Form</h2>
//       <p>This is a simple general form component.</p>
//     </div>
//   );
<section className="transfer-form">
      <h2>{ title }</h2>
      {/* TODO 2.4: Wire the onSubmit prop on the form below to the handler */}
      <form onSubmit={handleSubmit}>
        <div className="form-row">
          <label htmlFor="fromAccount">{labelDescription}</label>
                        <select
                      id="fromAccount"
                      value={fromAccount}
                      onChange={(e) => setFromAccount(e.target.value)}
                      required
                    >
            <option value="">-- Select an account --</option>
            {accountList.map((a: Account) => (
              <option key={a.accountNumber} value={a.accountNumber}>
                {a.accountNumber} ({a.accountType}, ${a.balance.toFixed(2)})
              </option>
            ))}
          </select>
              
          {/* <select id="fromAccount">
            <option value="">-- Select an account --</option>
          </select> */}
        </div>

        <div className="form-row">
          { title === 'Payment' && (
            <>
              <label htmlFor="To">To</label>
                        <select
                      id="fromAccount"
                      value={selectedValue}
                      onChange={handlePaymentChange}
                      required
                    >
            <option value="">-- Select a company --</option>
            {paymentComps?.map((comp) => (
              <option key={comp.id} value={comp.id}>
                {comp.name}
              </option>
            ))}
          </select>
            </>
          )}
        </div>

        <div className="form-row">
          <label htmlFor="amount">Amount</label>
              <input
                  id="amount"
                  type="number"
                  step="0.01"
                  min="0.01"
                  placeholder="0.00"
                  value={amount}
                  onChange={(e) => setAmount(e.target.value)}
                  required
            />
        </div>

        {/* TODO 2.8: When submitting is true, the button should be disabled and show "Submitting..."
                     Otherwise the button should be enabled and show "Transfer". */}
                     {/* <button type="submit" disabled={submitting}>
                      {submitting ? 'Submitting...' : 'Transfer'}
                     </button> */}
        {/* <button type="submit">Transfer</button> */}
        <button type="submit" disabled={submitting}>
  {submitting ? 'Submitting...' : btnTitle || 'Payment'}
</button>

        {/* TODO 2.9: If message is not null, render a <p> with the message.
                     Use the className "form-message success" or "form-message error"
                     based on messageType. */}
                     {/* {message && (
  <p className={`form-message ${messageType}`}>
    {message}
  </p>
  
    )
    } */}
    {message && (
          <p className={messageType === 'success' ? 'success-message' : 'error-message'}>
            {message}
          </p>
        )}
      </form>
    </section>
  );
}
