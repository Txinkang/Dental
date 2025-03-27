package com.example.dental.controller.user;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.dental.common.Response.Result;
import com.example.dental.model.Appointment;
import com.example.dental.model.Consultation;
import com.example.dental.model.User;
import com.example.dental.service.user.UserService;
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    //=========================================用户管理=========================================
    @PostMapping(value = "/register")
    public Result register(@RequestBody User user){
        return userService.register(user);
    }

    @PostMapping(value = "/login")
    public Result login(@RequestBody User user){
        return userService.login(user);
    }

    @GetMapping("/logout")
    public Result logout(@RequestHeader("Authorization") String token){
        return userService.logout(token);
    }

    @GetMapping("/getUserInfo")
    public Result getUserInfo(){
        return userService.getUserInfo();
    }

    @PatchMapping("/updateUserInfo")
    public Result updateUserInfo(@RequestBody @Validated User user){
        return userService.updateUserInfo(user);
    }

    //=========================================项目管理=========================================
    @GetMapping("/getItem")
    public Result getItem(){
        return userService.getItem();
    }

    @PostMapping("/appointentItem")
    public Result appointentItem(@RequestBody Appointment appointment){
        return userService.appointentItem(appointment);
    }

    @GetMapping("/getAppointment")
    public Result getAppointment(){
        return userService.getAppointment();
    }

    @GetMapping("/cancelAppointment")  
    public Result cancelAppointment(
        @RequestParam("appointmentId") String appointmentId
    ){
        return userService.cancelAppointment(appointmentId);
    }

    //=========================================科普管理=========================================
    @GetMapping("/getEducation")
    public Result getEducation(){
        return userService.getEducation();
    }

    @GetMapping("/getDoctorInfo")
    public Result getDoctorInfo(){
        return userService.getDoctorInfo();
    }

    //=========================================咨询管理=========================================
    @PostMapping("/consult")
    public Result consult(@RequestBody Consultation consultation){
        return userService.consult(consultation);
    }

    @GetMapping("/getConsultaion")
    public Result getConsultaion(){
        return userService.getConsultaion();
    }

}
