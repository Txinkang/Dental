package com.example.dental.model;

import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.Objects;

@Entity
public class Admin {
    @Id
    @Column(name = "admin_id")
    private String adminId;
    @Basic
    @Column(name = "admin_account")
    private String adminAccount;
    @Basic
    @Column(name = "admin_password")
    private String adminPassword;
    @Basic
    @Column(name = "created_at")
    private Timestamp createdAt;
    @Basic
    @Column(name = "updated_at")
    private Timestamp updatedAt;

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getAdminAccount() {
        return adminAccount;
    }

    public void setAdminAccount(String adminAccount) {
        this.adminAccount = adminAccount;
    }

    public String getAdminPassword() {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Admin admin = (Admin) o;
        return Objects.equals(adminId, admin.adminId) && Objects.equals(adminAccount, admin.adminAccount) && Objects.equals(adminPassword, admin.adminPassword) && Objects.equals(createdAt, admin.createdAt) && Objects.equals(updatedAt, admin.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(adminId, adminAccount, adminPassword, createdAt, updatedAt);
    }
}
