package com.example.dental.model;

import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.Objects;

@Entity
public class Appointment {
    @Id
    @Column(name = "appointment_id")
    private String appointmentId;
    @Basic
    @Column(name = "user_id")
    private String userId;
    @Basic
    @Column(name = "doctor_id")
    private String doctorId;
    @Basic
    @Column(name = "item_id")
    private String itemId;
    @Basic
    @Column(name = "appointment_time")
    private Timestamp appointmentTime;
    @Basic
    @Column(name = "result")
    private String result;
    @Basic
    @Column(name = "status")
    private int status;
    @Basic
    @Column(name = "create_time")
    private Timestamp createTime;
    @Basic
    @Column(name = "update_time")
    private Timestamp updateTime;

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public Timestamp getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(Timestamp appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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
        Appointment that = (Appointment) o;
        return status == that.status && Objects.equals(appointmentId, that.appointmentId) && Objects.equals(userId, that.userId) && Objects.equals(doctorId, that.doctorId) && Objects.equals(itemId, that.itemId) && Objects.equals(appointmentTime, that.appointmentTime) && Objects.equals(result, that.result) && Objects.equals(createTime, that.createTime) && Objects.equals(updateTime, that.updateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(appointmentId, userId, doctorId, itemId, appointmentTime, result, status, createTime, updateTime);
    }
}
