package com.example.bankapi.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "transactions_idempotency")
public class IdempotencyKey {

    @Id
    @Column(name = "idempotency_key", length = 255)
    private String idempotencyKey;

    public IdempotencyKey() {}

    public IdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }
}
