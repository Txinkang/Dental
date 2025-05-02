package com.example.dental.service.admin;

import org.springframework.web.multipart.MultipartFile;

import com.example.dental.common.Response.Result;
import com.example.dental.model.Appointment;
import com.example.dental.model.Consultation;
import com.example.dental.model.Doctor;
import com.example.dental.model.Item;

public interface ManageService {
    Result getItem(String itemName, Integer pageNum, Integer pageSize);

    Result addDoctor(String doctorName, MultipartFile doctorAvatar, String introduction, String introduction2, Integer workingYears);

    Result getDoctor(String doctorName, Integer pageNum, Integer pageSize);

    Result updateDoctor(String doctorId, String doctorName, String doctorSchedule, MultipartFile doctorAvatar, String introduction, Integer workingYears);

    Result deleteDoctor(String doctorId);

    Result getAllDoctor();

    Result addItem(String itemName, String doctorId);

    Result updateItem(String itemId, String itemName, String doctorId);

    Result deleteItem(String itemId);

    Result deleteItemDoctor(String itemId, String doctorId);

    Result addEducation(String educationTitle, String educationContent);

    Result getEducation(String educationTitle, Integer pageNum, Integer pageSize);

    Result updateEducation(String educationId, String educationTitle, String educationContent);

    Result deleteEducation(String educationId);

    Result getAppointment(String itemName, String doctorName, String userName, Integer pageNum, Integer pageSize);

    Result uploadResult(Appointment appointment);

    Result getConsultation();

    Result reply(Consultation consultation);

    Result breachAppointment(Appointment appointment);
}
