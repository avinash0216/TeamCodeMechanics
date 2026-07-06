import type { Account } from '../api/types';

type AccountListProps = {
  accounts: Account[] | { data: Account[] } | null | undefined;
  loading: boolean;
  error: string | null;
};

export function AccountList({ accounts, loading, error }: AccountListProps) {
  const accountItems = Array.isArray(accounts)
    ? accounts
    : Array.isArray(accounts?.data)
      ? accounts.data
      : [];

  if (loading) {
    return <p className="status-message">Loading accounts...</p>;
  }

  if (error) {
    return <p className="error-message">Error loading accounts: {error}</p>;
  }

  if (accountItems.length === 0) {
    return <p className="status-message">No accounts found.</p>;
  }

  return (
    <section className="account-list">
      <h2>Your Accounts</h2>
      <table>
        <thead>
          <tr>
            <th>Account</th>
            <th>Type</th>
            <th>Balance</th>
          </tr>
        </thead>
        <tbody>
          {accountItems.map((account) => (
            <tr key={account.accountNumber}>
              <td>{account.accountNumber}</td>
              <td>{account.accountType}</td>
              <td>${account.balance.toFixed(2)}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </section>
  );
}