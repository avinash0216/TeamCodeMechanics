import type { ITransactionProps } from "../App";

type TransactionsProps = {
  transactions: ITransactionProps[];
};

export default function Transactions({ transactions }: TransactionsProps) {
  return (
    <section className="account-list">
      <table>
        <thead>
          <tr>
            <th>Txn ID</th>
            <th>Type</th>
            <th>Amount</th>
            <th>Account</th>
            <th>Status</th>
            <th>Customer</th>
            <th>Description</th>
            <th>Date</th>
          </tr>
        </thead>
        <tbody>
          {transactions.map((txn) => (
            <tr key={txn.txnId}>
              <td>{txn.txnId}</td>
              <td>{txn.transactionType}</td>
              <td>{txn.Amount.toFixed(2)}</td>
              <td>{txn.accountId}</td>
              <td>{txn.status}</td>
              <td>{txn.customerId}</td>
              <td>{txn.description}</td>
              <td>{txn.date}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </section>
  );
}