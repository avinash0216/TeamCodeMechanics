/**
 * TypeScript types for the banking API.
 *
 * These types mirror the JSON the BFF returns. The BFF passes the resource
 * server's shapes straight through, so Account here matches bankapi's
 * AccountDto (id, customerId, accountType, balance) and User matches the
 * BFF's UserInfoDto returned by /api/me.
 */

export type AccountType = 'SAVINGS' | 'CHECKING';
export type AccountStatus = 'ACTIVE' | 'INACTIVE';

export type Account = {
  accountNumber: string;
  customerId: string;
  accountType: AccountType;
  balance: number;
  status: AccountStatus;
};

export type TransferRequest = {
  fromAccountNumber: string;
  toAccountNumber: string;
  amount: number;
};

export type DepositRequest = {
  toAccountNumber: string;
  amount: number;
};

export type PaymentRequest = {
  fromAccountNumber: string;
  toCompany: string;
  amount: number;
};

export type WithdrawalRequest = {
  fromAccountNumber: string;
  amount: number;
};

export type TransferResponse = {
  transactionId: string;
  status: 'COMPLETE' | 'FAILED';
};

// Matches the UserInfoDto returned by the BFF's /api/me endpoint.
export type User = {
  subject: string;
  preferredUsername: string;
  fullName: string;
  roles: string[];
};