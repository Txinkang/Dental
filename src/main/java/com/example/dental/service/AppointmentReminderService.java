package com.example.dental.service;

import com.example.dental.common.Redis.RedisService;
import com.example.dental.model.Appointment;
import com.example.dental.model.Doctor;
import com.example.dental.model.Item;
import com.example.dental.model.User;
import com.example.dental.repository.AppointmentRepository;
import com.example.dental.repository.DoctorRepository;
import com.example.dental.repository.ItemRepository;
import com.example.dental.repository.UserRepository;
import com.example.dental.utils.LogUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
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

    @Autowired
    private RedisService redisService;

    private static final LogUtil logUtil = LogUtil.getLogger(AppointmentReminderService.class);

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

    // 每天17:45清除Redis预约时间槽
    @Scheduled(cron = "0 45 17 * * ?")
    public void clearRedisAppointments() {
        try {
            logUtil.info("清除Redis预约时间槽");
            // 获取今天的日期
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime start = now.withHour(8).withMinute(0).withSecond(0).withNano(0);
            LocalDateTime end = now.withHour(17).withMinute(45).withSecond(0).withNano(0);

            // 删除8:00到17:45之间每15分钟的时间戳
            while (!start.isAfter(end)) {
                // 转换为秒级时间戳
                long timestampInSeconds = start.atZone(ZoneId.systemDefault()).toEpochSecond();
                
                // 删除Redis中的预约时间槽
                redisService.delete(String.valueOf(timestampInSeconds));
                
                // 增加15分钟
                start = start.plusMinutes(15);
            }
        } catch (Exception e) {
            logUtil.error("清除Redis预约时间槽失败", e);
        }
    }
} 