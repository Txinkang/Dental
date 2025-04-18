package com.example.dental.service.impl.user;

import com.example.dental.common.Redis.RedisService;
import com.example.dental.common.Response.Result;
import com.example.dental.common.Response.ResultCode;
import com.example.dental.common.userCommon.ThreadLocalUtil;
import com.example.dental.constant.AppointmentConstantdata;
import com.example.dental.constant.MagicMathConstData;
import com.example.dental.constant.RedisConstData;
import com.example.dental.model.Appointment;
import com.example.dental.model.Consultation;
import com.example.dental.model.Doctor;
import com.example.dental.model.Education;
import com.example.dental.model.Item;
import com.example.dental.model.User;
import com.example.dental.repository.AppointmentRepository;
import com.example.dental.repository.ConsultationRepository;
import com.example.dental.repository.DoctorRepository;
import com.example.dental.repository.EducationRepository;
import com.example.dental.repository.ItemRepository;
import com.example.dental.repository.UserRepository;
import com.example.dental.service.user.UserService;
import com.example.dental.utils.JwtUtil;
import com.example.dental.utils.LogUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class UserServiceImpl implements UserService {
    
    private static final LogUtil logUtil = LogUtil.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RedisService redisService;

    @Autowired
    private ItemRepository itemRepository;
    
    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private EducationRepository educationRepository;

    @Autowired
    private ConsultationRepository consultationRepository;

    //=========================================用户管理=========================================
    @Override
    public Result register(User user) {
        try {
            //验证参数
            if (user == null || Strings.isEmpty(user.getUserAccount()) || Strings.isEmpty(user.getUserPassword())) {
                return new Result(ResultCode.R_ParamError);
            }
            //查询用户是否存在
            if (userRepository.findByUserAccount(user.getUserAccount()) != null) {
                return new Result(ResultCode.R_UserAccountIsExist);
            }
            if (userRepository.findByUserEmail(user.getUserEmail()) != null) {
                return new Result(ResultCode.R_UserEmailIsExist);
            }
            if (userRepository.findByUserName(user.getUserName()) != null) {
                return new Result(ResultCode.R_UserNameIsExist);
            }
            //设置用户ID
            user.setUserId(UUID.randomUUID().toString());
            user.setUserBreach(0);
            //设置创建时间和更新时间
            Timestamp now = new Timestamp(System.currentTimeMillis());
            user.setCreatedAt(now);
            user.setUpdatedAt(now);
            //写入数据库

            userRepository.save(user);
            return new Result(ResultCode.R_Ok);
        } catch (Exception e) {
            logUtil.error("注册用户失败", e);
            return new Result(ResultCode.R_UpdateDbFailed);
        }

    }

    @Override
    public Result login(User user) {
        try {
            //验证参数
            if (user == null || Strings.isEmpty(user.getUserAccount()) || Strings.isEmpty(user.getUserPassword())) {
                return new Result(ResultCode.R_ParamError);
            }
            //查询用户是否存在
            User queryUser = userRepository.findByUserAccount(user.getUserAccount());
            if (queryUser == null) {
                return new Result(ResultCode.R_UserNotFound);
            }
            //查询密码是否正确
            if (!user.getUserPassword().equals(queryUser.getUserPassword())) {
                return new Result(ResultCode.R_PasswordError);
            }
            //存入信息生成token
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("id", queryUser.getUserId());
            String token = JwtUtil.genToken(userMap);
            boolean redisSet = redisService.set(RedisConstData.USER_LOGIN_TOKEN + queryUser.getUserId(), token, MagicMathConstData.REDIS_VERIFY_TOKEN_TIMEOUT, TimeUnit.HOURS);
            if (!redisSet) {
                return new Result(ResultCode.R_Fail);
            }
            return new Result(ResultCode.R_Ok, token);
        } catch (Exception e) {
            logUtil.error("登录失败", e);
            return new Result(ResultCode.R_UpdateDbFailed);
        }
    }

    @Override
    public Result logout(String token) {
        try {
            Map<String, Object> userMap = JwtUtil.parseToken(token);
            //为null大概就是过期了，直接回ok就行
            if (userMap == null) {
                return new Result(ResultCode.R_Ok);
            }
            boolean redisDelete = redisService.delete(RedisConstData.USER_LOGIN_TOKEN + userMap.get("id"));
            return new Result(redisDelete ? ResultCode.R_Ok : ResultCode.R_UpdateDbFailed);
        } catch (Exception e) {
            logUtil.error("登出失败", e);
            return new Result(ResultCode.R_UpdateDbFailed);
        }
    }

    @Override
    public Result getUserInfo() {
        try {
            String userId = ThreadLocalUtil.getUserId();
            if (userId == null) {
                return new Result(ResultCode.R_Fail);
            }
            User queryUser = userRepository.findByUserId(userId);
            if (queryUser == null) {
                return new Result(ResultCode.R_UserNotFound);
            }
            return new Result(ResultCode.R_Ok, queryUser);
        } catch (Exception e) {
            logUtil.error("获取用户信息失败", e);
            return new Result(ResultCode.R_UpdateDbFailed);
        }
    }

    @Override
    public Result updateUserInfo(User user) {
        try {
            //获取userId
            String userId = ThreadLocalUtil.getUserId();
            if (userId == null) {
                return new Result(ResultCode.R_Fail);
            }
            //验证所要修改信息是否已存在
            User queryUser = userRepository.findByUserId(userId);
            if(queryUser == null){
                return new Result(ResultCode.R_UserNotFound);
            }
            if (!Strings.isEmpty(user.getUserAccount())) {
                User queryUserAccount = userRepository.findByUserAccount(user.getUserAccount());
                if (queryUserAccount != null && !queryUserAccount.getUserId().equals(userId)) {
                    return new Result(ResultCode.R_UserAccountIsExist);
                }
                queryUser.setUserAccount(user.getUserAccount());
            }
            if (!Strings.isEmpty(user.getUserEmail())) {
                User queryUserEmail = userRepository.findByUserEmail(user.getUserEmail());
                if (queryUserEmail != null && !queryUserEmail.getUserId().equals(userId)) {
                    return new Result(ResultCode.R_UserEmailIsExist);
                }
                queryUser.setUserEmail(user.getUserEmail());
            }
            if (!Strings.isEmpty(user.getUserName())) {
                User queryUserName = userRepository.findByUserName(user.getUserName());
                if (queryUserName != null && !queryUserName.getUserId().equals(userId)) {
                    return new Result(ResultCode.R_UserNameIsExist);
                }
                queryUser.setUserName(user.getUserName());
            }
            if (!Strings.isEmpty(user.getUserPassword())) {
                queryUser.setUserPassword(user.getUserPassword());
            }
            //验证通过开始修改
            queryUser.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            userRepository.saveAndFlush(queryUser);  // 使用 saveAndFlush 确保立即更新
            boolean redisDelete = redisService.delete(RedisConstData.USER_LOGIN_TOKEN + queryUser.getUserId());
            return new Result(redisDelete ? ResultCode.R_Ok : ResultCode.R_UpdateDbFailed);
        } catch (Exception e) {
            logUtil.error("更新用户信息失败", e);
            return new Result(ResultCode.R_UpdateDbFailed);
        }
    }

    //=========================================项目管理=========================================
    @Override
    public Result getItem() {
        try {
            List<Item> itemList = itemRepository.findAll();
            List<Map<String, Object>> itemWithDoctors = new ArrayList<>();
            
            for (Item o : itemList) {
                Map<String, Object> itemMap = new HashMap<>();
                itemMap.put("itemId", o.getItemId());
                itemMap.put("itemName", o.getItemName());
                itemMap.put("createTime", o.getCreateTime());
                itemMap.put("updateTime", o.getUpdateTime());
                String doctorIdsJson = (String) o.getDoctorId();
                List<Map<String, Object>> doctorList = new ArrayList<>();
                if (!Strings.isEmpty(doctorIdsJson)) {
                    String[] doctorIdsArray = new ObjectMapper().readValue(doctorIdsJson, String[].class);
                    for(String doctorId : doctorIdsArray){
                        Doctor d = doctorRepository.findByDoctorId(doctorId);
                        if (d != null) {
                            Map<String, Object> doctorMap = new HashMap<>();
                            doctorMap.put("doctorId", d.getDoctorId());
                            doctorMap.put("doctorName", d.getDoctorName());
                            doctorList.add(doctorMap);
                        } else {
                            Map<String, Object> doctorMap = new HashMap<>();
                            doctorMap.put("doctorId", "");
                            doctorMap.put("doctorName", "");
                            doctorList.add(doctorMap);
                        }
                    }
                }
                itemMap.put("doctor", doctorList);
                // 获取当前时间
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime today1745 = now.withHour(17).withMinute(45).withSecond(0).withNano(0);

                // 确定是使用今天还是明天的日期
                LocalDateTime targetDate;
                if (now.isAfter(today1745)) {
                    // 如果当前时间已经超过了今天的17:45，则使用明天的日期
                    targetDate = now.plusDays(1);
                } else {
                    // 否则使用今天的日期
                    targetDate = now;
                }

                // 设置目标日期的起始和结束时间
                LocalDateTime start = targetDate.withHour(8).withMinute(0).withSecond(0).withNano(0);
                LocalDateTime end = targetDate.withHour(17).withMinute(45).withSecond(0).withNano(0);

                // 生成8:00到17:45之间每15分钟的时间戳
                List<Integer> appointmentTime = new ArrayList<>();
                while (!start.isAfter(end)) {
                    // 转换为秒级时间戳
                    long timestampInSeconds = start.atZone(ZoneId.systemDefault()).toEpochSecond();
                    
                    // 检查Redis中是否存在预约
                    String appointment = redisService.get(String.valueOf(timestampInSeconds));
                    
                    // 如果该时间段没有预约，将时间戳添加到列表中
                    if (appointment == null) {
                        appointmentTime.add((int)timestampInSeconds);
                    }
                    
                    // 增加15分钟
                    start = start.plusMinutes(15);
                }
                itemMap.put("appointmentTime", appointmentTime);
                itemWithDoctors.add(itemMap);
            }
            return new Result(ResultCode.R_Ok, itemWithDoctors);
        } catch (Exception e) {
            logUtil.error("获取项目失败", e);
            return new Result(ResultCode.R_UpdateDbFailed);
        }
    }

    @Override
    public Result appointentItem(Appointment appointment) {
        try {
            User queryUser = userRepository.findByUserId(ThreadLocalUtil.getUserId());
            if(queryUser == null){
                return new Result(ResultCode.R_UserNotFound);
            }
            if(queryUser.getUserBreach() >= 3){
                return new Result(ResultCode.R_Error, "违约次数过多，无法预约");
            }
            // 获取预约时间
            Timestamp appointmentTime = appointment.getAppointmentTime();
            if (appointmentTime == null) {
                return new Result(ResultCode.R_ParamError);
            }
            
            // 将时间戳转换为秒级时间戳作为Redis的key
            long timestampInSeconds = appointmentTime.getTime() / 1000;
            String redisKey = String.valueOf(timestampInSeconds);
            
            // 检查该时间段是否已被预约
            String existingAppointment = redisService.get(redisKey);
            if (existingAppointment != null) {
                return new Result(ResultCode.R_Error, "该时间段已被预约");
            }
            
            // 创建新预约
            Appointment newAppointment = new Appointment();
            newAppointment.setAppointmentId(UUID.randomUUID().toString());
            newAppointment.setUserId(ThreadLocalUtil.getUserId());
            newAppointment.setItemId(appointment.getItemId());
            newAppointment.setDoctorId(appointment.getDoctorId());
            newAppointment.setAppointmentTime(appointmentTime);
            newAppointment.setResult("");
            newAppointment.setStatus(AppointmentConstantdata.APPOINTMENT_STATUS_NOUPLOAD);
            newAppointment.setCreateTime(new Timestamp(System.currentTimeMillis()));
            newAppointment.setUpdateTime(new Timestamp(System.currentTimeMillis()));
            
            // 保存预约到数据库
            appointmentRepository.save(newAppointment);
            
            // 将预约时间存入Redis
            redisService.set(redisKey, newAppointment.getAppointmentId());
            
            return new Result(ResultCode.R_Ok);
        } catch (Exception e) {
            logUtil.error("预约项目失败", e);
            return new Result(ResultCode.R_UpdateDbFailed);
        }
    }

    @Override
    public Result getAppointment() {
        try {
            String userId = ThreadLocalUtil.getUserId();
            if (userId == null) {
                return new Result(ResultCode.R_Fail);
            }
            List<Appointment> appointmentList = appointmentRepository.findByUserIdOrderByAppointmentTimeDesc(userId);
            List<Map<String, Object>> appointmentWithItems = new ArrayList<>();
            for (Appointment o : appointmentList) {
                Map<String, Object> appointmentMap = new HashMap<>();
                appointmentMap.put("appointmentId", o.getAppointmentId());
                appointmentMap.put("appointmentTime", o.getAppointmentTime());
                appointmentMap.put("status", o.getStatus());
                appointmentMap.put("result", o.getResult());
                appointmentMap.put("createTime", o.getCreateTime());
                appointmentMap.put("updateTime", o.getUpdateTime());
                Doctor queryDoctor = doctorRepository.findByDoctorId(o.getDoctorId());
                if(queryDoctor != null){
                    appointmentMap.put("doctor_name", queryDoctor.getDoctorName());
                }else{
                    appointmentMap.put("doctor_name", "已删除");
                }
                User queryUser = userRepository.findByUserId(o.getUserId());
                if(queryUser != null){
                    appointmentMap.put("user_name", queryUser.getUserName());
                }else{
                    appointmentMap.put("user_name", "已删除");
                }
                Item queryItem = itemRepository.findByItemId(o.getItemId());
                if(queryItem != null){
                    appointmentMap.put("item_name", queryItem.getItemName());
                }else{
                    appointmentMap.put("item_name", "已删除");
                }
                //每条数据插入到数组的最开头
                appointmentWithItems.add(appointmentMap);
            }
            return new Result(ResultCode.R_Ok, appointmentWithItems);
        } catch (Exception e) {
            logUtil.error("获取预约项目失败", e);
            return new Result(ResultCode.R_UpdateDbFailed);
        }
    }

    @Override
    public Result cancelAppointment(String appointmentId) {
        try {
            Appointment appointment = appointmentRepository.findByAppointmentId(appointmentId);
            if(appointment == null){
                return new Result(ResultCode.R_Error);
            }
            //判断距离预约开始时间如果小于15分钟，则不允许取消
            if(appointment.getAppointmentTime().getTime() - System.currentTimeMillis() < 15 * 60 * 1000){
                return new Result(ResultCode.R_Error);
            }
            //取消预约
            appointmentRepository.delete(appointment);
            return new Result(ResultCode.R_Ok);
        } catch (Exception e) {
            logUtil.error("取消预约项目失败", e);
            return new Result(ResultCode.R_UpdateDbFailed);
        }
    }
    //=========================================科普管理=========================================
    @Override
    public Result getEducation() {
        try {
            List<Education> educationList = educationRepository.findAll();
            return new Result(ResultCode.R_Ok, educationList);
        } catch (Exception e) {
            logUtil.error("获取科普失败", e);
            return new Result(ResultCode.R_UpdateDbFailed);
        }
    }

    @Override
    public Result getDoctorInfo() {
        try {
            List<Doctor> doctorList = doctorRepository.findAll();
            return new Result(ResultCode.R_Ok, doctorList);
        } catch (Exception e) {
            logUtil.error("获取医生信息失败", e);
            return new Result(ResultCode.R_UpdateDbFailed);
        }
    }


    //=========================================咨询管理=========================================
    @Override
    public Result consult(Consultation consultation) {
        try {
            consultation.setConsultationId(UUID.randomUUID().toString());
            consultation.setUserId(ThreadLocalUtil.getUserId());
            consultation.setAnswer("");
            consultation.setCreateTime(new Timestamp(System.currentTimeMillis()));
            consultation.setUpdateTime(new Timestamp(System.currentTimeMillis()));
            consultationRepository.save(consultation);
            return new Result(ResultCode.R_Ok);
        } catch (Exception e) {
            logUtil.error("咨询失败", e);
            return new Result(ResultCode.R_UpdateDbFailed);
        }
    }

    @Override
    public Result getConsultaion() {
        try {
            String userId = ThreadLocalUtil.getUserId();
            if (userId == null) {
                return new Result(ResultCode.R_Fail);
            }
            List<Consultation> consultationList = consultationRepository.findByUserId(userId);
            return new Result(ResultCode.R_Ok, consultationList);
        } catch (Exception e) {
            logUtil.error("获取咨询失败", e);
            return new Result(ResultCode.R_UpdateDbFailed);
        }
    }
}
