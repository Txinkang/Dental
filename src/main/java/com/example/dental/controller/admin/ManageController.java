package com.example.dental.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.dental.common.Response.Result;
import com.example.dental.model.Doctor;
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
        @RequestParam(value = "working_years", required = true) String workingYears
    ){
        return manageService.addDoctor(doctorName, doctorAvatar, introduction, workingYears);
    }



    //==================================项目管理==================================
    @GetMapping("/getItem")
    public Result getItem(
        @RequestParam(value = "itemName", required = false) String itemName,
        @RequestParam(value = "page_num", required = false) String pageNum,
        @RequestParam(value = "page_size", required = false) String pageSize
    ){
        return manageService.getItem(itemName, pageNum, pageSize);
    }

    @PostMapping("/addItem")
    public Result addItem(@RequestBody Item item){
        return manageService.addItem(item);
    }
}
