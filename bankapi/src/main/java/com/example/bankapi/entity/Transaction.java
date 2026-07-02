package com.example.bankapi.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @UuidGenerator
    @Column(name = "txn_id", length = 36)
    private String txnId;

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @Column(name = "txn_type", nullable = false, length = 12)
    private String txnType;  // DEPOSIT, WITHDRAWAL, TRANSFER_IN, TRANSFER_OUT, PAYMENT

    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "status", nullable = false, length = 10)
    private String status;  // COMPLETED or FAILED

    @Column(name = "txn_date", nullable = false, updatable = false)
    private LocalDateTime txnDate;

    @Column(name = "description", length = 255)
    private String description;

    // Constructors
    public Transaction() {}

    public Transaction(String txnId, Long accountId, String txnType, BigDecimal amount, String status) {
        this.txnId = txnId;
        this.accountId = accountId;
        this.txnType = txnType;
        this.amount = amount;
        this.status = status;
    }

    // Getters & Setters
    public String getTxnId() {
        return txnId;
    }

    public void setTxnId(String txnId) {
        this.txnId = txnId;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getTxnType() {
        return txnType;
    }

    public void setTxnType(String txnType) {
        this.txnType = txnType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getTxnDate() {
        return txnDate;
    }

    public void setTxnDate(LocalDateTime txnDate) {
        this.txnDate = txnDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
