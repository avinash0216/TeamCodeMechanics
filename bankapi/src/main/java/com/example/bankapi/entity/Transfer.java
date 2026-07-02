package com.example.bankapi.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;
import java.time.LocalDateTime;

@Entity
@Table(name = "transfers")
public class Transfer {

    @Id
    @UuidGenerator
    @Column(name = "transfer_id", length = 36)
    private String transferId;

    @Column(name = "debit_txn_id", nullable = false, length = 36)
    private String debitTxnId;

    @Column(name = "credit_txn_id", nullable = false, length = 36)
    private String creditTxnId;

    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    // Constructors
    public Transfer() {}

    public Transfer(String transferId, String debitTxnId, String creditTxnId) {
        this.transferId = transferId;
        this.debitTxnId = debitTxnId;
        this.creditTxnId = creditTxnId;
    }

    // Getters & Setters
    public String getTransferId() {
        return transferId;
    }

    public void setTransferId(String transferId) {
        this.transferId = transferId;
    }

    public String getDebitTxnId() {
        return debitTxnId;
    }

    public void setDebitTxnId(String debitTxnId) {
        this.debitTxnId = debitTxnId;
    }

    public String getCreditTxnId() {
        return creditTxnId;
    }

    public void setCreditTxnId(String creditTxnId) {
        this.creditTxnId = creditTxnId;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
}
