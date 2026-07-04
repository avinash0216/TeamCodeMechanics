import { FormEvent, useContext, useState } from "react";
import { AccountsContext, TitleContext } from "../common/TitleContext";
import { formatCurrency } from "../../utils/format";
import { IPaymentFacilities } from "../../features/payment";
import MatButton from "./MatButton";

export default function GeneralForm( { paymentComps, selectedValue, onChange, btnTitle, labelDescription }: { paymentComps?: IPaymentFacilities[]; selectedValue?: number; onChange?: (value: IPaymentFacilities | null) => void; btnTitle?: string; labelDescription?: string; }) {
    const title = useContext(TitleContext);
    const accounts = useContext(AccountsContext);
    const activeAccounts = accounts?.filter((a) => a.status === 'ACTIVE');
    const [fromAccount, setFromAccount] = useState<string>('');
    const [amount, setAmount] = useState<string>('');
    const [submitting, setSubmitting] = useState<boolean>(false);
  
  
  
  function handleSubmit(event: FormEvent<HTMLFormElement>): void {
    throw new Error("Function not implemented.");
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
            {accounts?.map((a) => (
              <option key={a.id} value={a.id}>
                {a.id} ({a.accountType}, ${a.balance.toFixed(2)})
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
                      onChange={(e) => {
                        const selectedId = parseInt(e.target.value);
                        const selectedComp = paymentComps?.find((comp) => comp.id === selectedId) || null;
                        onChange?.(selectedComp);
                      }}
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
      </form>
    </section>
  );
}
