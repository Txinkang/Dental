package com.example.dental.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    public void sendAppointmentReminder(String to, String userName, String itemName, String doctorName, String appointmentTime) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("2912528586@qq.com");
        message.setTo(to);
        message.setSubject("牙科预约提醒");
        message.setText("尊敬的 " + userName + "：\n\n"
                + "提醒您，您预约的项目 " + itemName + " 将在30分钟后开始。\n"
                + "预约时间：" + appointmentTime + "\n"
                + "主治医生：" + doctorName + "\n"
                + "请您准时到达就诊。\n\n"
                + "祝您健康！");
        
        mailSender.send(message);
    }
} 