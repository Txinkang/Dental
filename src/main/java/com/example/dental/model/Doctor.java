package com.example.dental.model;

import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.Objects;

@Entity
public class Doctor {
    @Id
    @Column(name = "doctor_id")
    private String doctorId;
    @Basic
    @Column(name = "doctor_name")
    private String doctorName;
    @Basic
    @Column(name = "doctor_avatar")
    private String doctorAvatar;
    @Basic
    @Column(name = "introduction")
    private String introduction;
    @Basic
    @Column(name = "working_years")
    private Integer workingYears;
    @Basic
    @Column(name = "create_time")
    private Timestamp createTime;
    @Basic
    @Column(name = "update_time")
    private Timestamp updateTime;

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDoctorAvatar() {
        return doctorAvatar;
    }

    public void setDoctorAvatar(String doctorAvatar) {
        this.doctorAvatar = doctorAvatar;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public Integer getWorkingYears() {
        return workingYears;
    }

    public void setWorkingYears(Integer workingYears) {
        this.workingYears = workingYears;
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
        Doctor doctor = (Doctor) o;
        return Objects.equals(doctorId, doctor.doctorId) && Objects.equals(doctorName, doctor.doctorName) && Objects.equals(doctorAvatar, doctor.doctorAvatar) && Objects.equals(introduction, doctor.introduction) && Objects.equals(workingYears, doctor.workingYears) && Objects.equals(createTime, doctor.createTime) && Objects.equals(updateTime, doctor.updateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(doctorId, doctorName, doctorAvatar, introduction, workingYears, createTime, updateTime);
    }
}
