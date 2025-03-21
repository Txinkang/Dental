package com.example.dental.service;

import com.example.dental.model.Appointment;
import com.example.dental.model.Doctor;
import com.example.dental.model.Item;
import com.example.dental.model.User;
import com.example.dental.repository.AppointmentRepository;
import com.example.dental.repository.DoctorRepository;
import com.example.dental.repository.ItemRepository;
import com.example.dental.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AppointmentReminderService {

    @Autowired
    private AppointmentRepository appointmentRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private DoctorRepository doctorRepository;
    
    @Autowired
    private ItemRepository itemRepository;
    
    @Autowired
    private EmailService emailService;

    // 每分钟检查一次需要提醒的预约
    @Scheduled(fixedRate = 60000)
    public void checkAppointments() {
        // 获取当前时间30分钟后的时间点
        Timestamp thirtyMinsLater = Timestamp.valueOf(LocalDateTime.now().plusMinutes(1));
        // 获取当前时间31分钟后的时间点
        Timestamp thirtyOneMinsLater = Timestamp.valueOf(LocalDateTime.now().plusMinutes(2));
        
        // 查找需要提醒的预约
        List<Appointment> appointments = appointmentRepository.findByAppointmentTimeBetween(
            thirtyMinsLater, thirtyOneMinsLater);
            
        for (Appointment appointment : appointments) {
            User user = userRepository.findByUserId(appointment.getUserId());
            Doctor doctor = doctorRepository.findByDoctorId(appointment.getDoctorId());
            Item item = itemRepository.findByItemId(appointment.getItemId());
            
            // 发送邮件提醒
            if (user != null && user.getUserEmail() != null) {
                emailService.sendAppointmentReminder(
                    user.getUserEmail(),
                    user.getUserName(),
                    item.getItemName(),
                    doctor.getDoctorName(),
                    appointment.getAppointmentTime().toString()
                );
            }
        }
    }
} 