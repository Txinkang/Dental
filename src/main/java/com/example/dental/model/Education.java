package com.example.dental.model;

import jakarta.persistence.*;

import java.sql.Timestamp;
import java.util.Objects;

@Entity
public class Education {
    @Id
    @Column(name = "education_id")
    private String educationId;
    @Basic
    @Column(name = "education_title")
    private String educationTitle;
    @Basic
    @Column(name = "education_content")
    private String educationContent;
    @Basic
    @Column(name = "create_time")
    private Timestamp createTime;
    @Basic
    @Column(name = "update_time")
    private Timestamp updateTime;

    public String getEducationId() {
        return educationId;
    }

    public void setEducationId(String educationId) {
        this.educationId = educationId;
    }

    public String getEducationTitle() {
        return educationTitle;
    }

    public void setEducationTitle(String educationTitle) {
        this.educationTitle = educationTitle;
    }

    public String getEducationContent() {
        return educationContent;
    }

    public void setEducationContent(String educationContent) {
        this.educationContent = educationContent;
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
        Education education = (Education) o;
        return Objects.equals(educationId, education.educationId) && Objects.equals(educationTitle, education.educationTitle) && Objects.equals(educationContent, education.educationContent) && Objects.equals(createTime, education.createTime) && Objects.equals(updateTime, education.updateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(educationId, educationTitle, educationContent, createTime, updateTime);
    }
}
