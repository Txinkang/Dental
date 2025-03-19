package com.example.dental.model;

import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.Objects;

@Entity
public class Item {
    @Id
    @Column(name = "item_id")
    private String itemId;
    @Basic
    @Column(name = "item_name")
    private String itemName;
    @Basic
    @Column(name = "doctor_id", columnDefinition = "json")
    private Object doctorId;
    @Basic
    @Column(name = "create_time")
    private Timestamp createTime;
    @Basic
    @Column(name = "update_time")
    private Timestamp updateTime;

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Object getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Object doctorId) {
        this.doctorId = doctorId;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(itemId, item.itemId) && Objects.equals(itemName, item.itemName) && Objects.equals(doctorId, item.doctorId) && Objects.equals(createTime, item.createTime) && Objects.equals(updateTime, item.updateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemId, itemName, doctorId, createTime, updateTime);
    }
}
