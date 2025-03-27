package com.example.dental.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.dental.common.Response.Result;
import com.example.dental.model.Appointment;
import com.example.dental.model.Consultation;
import com.example.dental.model.Doctor;
import com.example.dental.model.Education;
import com.example.dental.model.Item;
import com.example.dental.service.admin.ManageService;

@RestController
@RequestMapping("/admin")
public class ManageController {
    @Autowired
    private ManageService manageService;


    //==================================医生管理==================================
    @PostMapping("/addDoctor")
    public Result addDoctor(
        @RequestParam(value = "doctor_name", required = true) String doctorName,
        @RequestParam(value = "doctor_avatar", required = true) MultipartFile doctorAvatar,
        @RequestParam(value = "introduction", required = true) String introduction,
        @RequestParam(value = "working_years", required = true) Integer workingYears
    ){
        return manageService.addDoctor(doctorName, doctorAvatar, introduction, workingYears);
    }

    @GetMapping("/getDoctor")
    public Result getDoctor(
        @RequestParam(value = "doctor_name", required = false) String doctorName,
        @RequestParam(value = "page_num", required = true) Integer pageNum,
        @RequestParam(value = "page_size", required = true) Integer pageSize
    ){
        return manageService.getDoctor(doctorName, pageNum, pageSize);
    }

    @PostMapping("/updateDoctor")
    public Result updateDoctor(
        @RequestParam(value = "doctor_id", required = true) String doctorId,
        @RequestParam(value = "doctor_name", required = false) String doctorName,
        @RequestParam(value = "doctor_avatar", required = false) MultipartFile doctorAvatar,
        @RequestParam(value = "introduction", required = false) String introduction,
        @RequestParam(value = "working_years", required = false) Integer workingYears
    ){
        return manageService.updateDoctor(doctorId, doctorName, doctorAvatar, introduction, workingYears);
    }

    @DeleteMapping("/deleteDoctor")
    public Result deleteDoctor(
        @RequestParam(value = "doctor_id", required = true) String doctorId
    ){
        return manageService.deleteDoctor(doctorId);
    }

    @GetMapping("/getAllDoctor")
    public Result getAllDoctor(){
        return manageService.getAllDoctor();
    }


    //==================================项目管理==================================
    @GetMapping("/getItem")
    public Result getItem(
        @RequestParam(value = "itemName", required = false) String itemName,
        @RequestParam(value = "page_num", required = true) Integer pageNum,
        @RequestParam(value = "page_size", required = true) Integer pageSize
    ){
        return manageService.getItem(itemName, pageNum, pageSize);
    }

    @PostMapping("/addItem")
    public Result addItem(
        @RequestParam(value = "item_name", required = true) String itemName,
        @RequestParam(value = "doctor_id", required = true) String doctorId
    ){
        return manageService.addItem(itemName, doctorId);
    }

    @PatchMapping("/updateItem")
    public Result updateItem(
        @RequestParam(value = "item_id", required = true) String itemId,
        @RequestParam(value = "item_name", required = false) String itemName,
        @RequestParam(value = "doctor_id", required = false) String doctorId
    ){
        return manageService.updateItem(itemId, itemName, doctorId);
    }

    @DeleteMapping("/deleteItem")
    public Result deleteItem(
        @RequestParam(value = "item_id", required = true) String itemId
    ){
        return manageService.deleteItem(itemId);
    }

    @DeleteMapping("/deleteItemDoctor")
    public Result deleteItemDoctor(
        @RequestParam(value = "item_id", required = true) String itemId,
        @RequestParam(value = "doctor_id", required = true) String doctorId
    ){
        return manageService.deleteItemDoctor(itemId, doctorId);
    }

    //==================================科普管理==================================
    @PostMapping("/addEducation")
    public Result addEducation(@RequestBody Education education){
        return manageService.addEducation(education.getEducationTitle(), education.getEducationContent());
    }

    @GetMapping("/getEducation")
    public Result getEducation(
        @RequestParam(value = "education_title", required = false) String educationTitle,
        @RequestParam(value = "page_num", required = true) Integer pageNum,
        @RequestParam(value = "page_size", required = true) Integer pageSize
    ){
        return manageService.getEducation(educationTitle, pageNum, pageSize);
    }

    @PatchMapping("/updateEducation")
    public Result updateEducation(@RequestBody Education education){
        return manageService.updateEducation(education.getEducationId(), education.getEducationTitle(), education.getEducationContent());
    }

    @DeleteMapping("/deleteEducation")
    public Result deleteEducation(@RequestParam(value = "education_id", required = true) String educationId){
        return manageService.deleteEducation(educationId);
    }

    //==================================预约管理==================================
    @GetMapping("/getAppointment")
    public Result getAppointment(
        @RequestParam(value = "item_name", required = false) String itemName,
        @RequestParam(value = "doctor_name", required = false) String doctorName,
        @RequestParam(value = "user_name", required = false) String userName,
        @RequestParam(value = "page_num", required = true) Integer pageNum,
        @RequestParam(value = "page_size", required = true) Integer pageSize
    ){
        return manageService.getAppointment(itemName, doctorName, userName, pageNum, pageSize);
    }

    @PatchMapping("/uploadResult")
    public Result uploadResult(@RequestBody Appointment appointment){
        return manageService.uploadResult(appointment);
    }

    //==================================咨询管理==================================
    @GetMapping("/getConsultation")
    public Result getConsultation(){
        return manageService.getConsultation();
    }

    @PostMapping("/reply")
    public Result reply(@RequestBody Consultation consultation){
        return manageService.reply(consultation);
    }
}
