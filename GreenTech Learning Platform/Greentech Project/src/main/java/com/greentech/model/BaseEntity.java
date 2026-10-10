package com.greentech.model;

import java.sql.Timestamp;

/**
 * Base abstract domain entity providing common primary key and audit timestamp attributes.
 * Demonstrates Abstraction and Inheritance across all domain models.
 */
public abstract class BaseEntity {
    protected int id;
    protected Timestamp createdAt;

    public BaseEntity() {}

    public BaseEntity(int id, Timestamp createdAt) {
        this.id = id;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
