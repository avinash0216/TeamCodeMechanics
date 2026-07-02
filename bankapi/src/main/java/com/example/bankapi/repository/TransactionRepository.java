package com.example.bankapi.repository;

import com.example.bankapi.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for Transaction entity lookups.
 * Provides methods to fetch transaction history for an account, optionally filtered by date or type.
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {

    /**
     * Find all transactions for a given account, ordered by date descending (newest first).
     * Primary method for retrieving transaction history.
     *
     * @param accountId the account ID
     * @return list of transactions for the account, newest first
     */
    List<Transaction> findByAccountIdOrderByTxnDateDesc(Long accountId);

    /**
     * Find transactions for a given account within a date range, ordered by date descending.
     * Used for filtered transaction history queries by date.
     *
     * @param accountId the account ID
     * @param startDate the start of the date range (inclusive)
     * @param endDate the end of the date range (inclusive)
     * @return list of transactions within the range, newest first
     */
    @Query("SELECT t FROM Transaction t WHERE t.accountId = :accountId " +
           "AND t.txnDate >= :startDate AND t.txnDate <= :endDate " +
           "ORDER BY t.txnDate DESC")
    List<Transaction> findByAccountIdAndDateRange(@Param("accountId") Long accountId,
                                                   @Param("startDate") LocalDateTime startDate,
                                                   @Param("endDate") LocalDateTime endDate);

    /**
     * Find transactions for a given account by transaction type, ordered by date descending.
     * Used to filter history by transaction type (DEPOSIT, WITHDRAWAL, TRANSFER_IN, etc.).
     *
     * @param accountId the account ID
     * @param txnType the transaction type to filter by
     * @return list of transactions of the specified type, newest first
     */
    List<Transaction> findByAccountIdAndTxnTypeOrderByTxnDateDesc(Long accountId, String txnType);

    /**
     * Find transactions for a given account by status, ordered by date descending.
     * Used to filter history by completion status (COMPLETED, FAILED).
     *
     * @param accountId the account ID
     * @param status the transaction status to filter by
     * @return list of transactions with the specified status, newest first
     */
    List<Transaction> findByAccountIdAndStatusOrderByTxnDateDesc(Long accountId, String status);

    /**
     * Find transactions for a given account within a date range and by transaction type.
     * Combined filter for detailed transaction history queries.
     *
     * @param accountId the account ID
     * @param startDate the start of the date range (inclusive)
     * @param endDate the end of the date range (inclusive)
     * @param txnType the transaction type to filter by
     * @return list of transactions matching all criteria, newest first
     */
    @Query("SELECT t FROM Transaction t WHERE t.accountId = :accountId " +
           "AND t.txnDate >= :startDate AND t.txnDate <= :endDate " +
           "AND t.txnType = :txnType " +
           "ORDER BY t.txnDate DESC")
    List<Transaction> findTransactionHistory(@Param("accountId") Long accountId,
                                             @Param("startDate") LocalDateTime startDate,
                                             @Param("endDate") LocalDateTime endDate,
                                             @Param("txnType") String txnType);

    /**
     * Count completed transactions for an account (useful for analytics/reporting).
     *
     * @param accountId the account ID
     * @return count of completed transactions
     */
    long countByAccountIdAndStatus(Long accountId, String status);
}
