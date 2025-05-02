package com.example.dental.service.impl.admin;

import com.example.dental.model.Appointment;
import com.example.dental.model.Consultation;
import com.example.dental.model.Doctor;
import com.example.dental.model.Education;
import com.example.dental.model.Item;
import com.example.dental.model.User;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.example.dental.common.Response.PageResponse;
import com.example.dental.common.Response.Result;
import com.example.dental.common.Response.ResultCode;
import com.example.dental.constant.AppointmentConstantdata;
import com.example.dental.repository.AppointmentRepository;
import com.example.dental.repository.ConsultationRepository;
import com.example.dental.repository.DoctorRepository;
import com.example.dental.repository.EducationRepository;
import com.example.dental.repository.ItemRepository;
import com.example.dental.repository.UserRepository;
import com.example.dental.service.admin.ManageService;
import com.example.dental.utils.FileUtil;
import com.example.dental.utils.LogUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ManageServiceImpl implements ManageService {

    private static final LogUtil logUtil = LogUtil.getLogger(ManageServiceImpl.class);

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private EducationRepository educationRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConsultationRepository consultationRepository;

    @Value("${uploadFilePath.doctorPicturesPath}")
    private String doctorPicturesPath;

    //==================================医生管理==================================
    @Override
    public Result addDoctor(String doctorName, MultipartFile doctorAvatar, String doctorSchedule, String introduction, Integer workingYears) {
        try{
            if(doctorName == null || doctorName.isEmpty()){
                return new Result(ResultCode.R_Error, "医生名称不能为空");
            }
            if(doctorAvatar == null || doctorAvatar.isEmpty()){
                return new Result(ResultCode.R_Error, "医生头像不能为空");
            }
            if(introduction == null || introduction.isEmpty()){
                return new Result(ResultCode.R_Error, "医生简介不能为空");
            }
            if(doctorSchedule == null || doctorSchedule.isEmpty()){
                return new Result(ResultCode.R_Error, "医生排班不能为空");
            }
            if(workingYears == null){
                return new Result(ResultCode.R_Error, "医生工作年限不能为空");
            }
            Doctor doctor = new Doctor();
            doctor.setDoctorId(UUID.randomUUID().toString());
            doctor.setDoctorName(doctorName);
            String doctorAvatarUrl = FileUtil.saveFile(doctorAvatar, doctorPicturesPath);
            doctor.setDoctorAvatar(doctorAvatarUrl);
            doctor.setIntroduction(introduction);
            doctor.setWorkingYears(workingYears);
            doctor.setDoctorSchedule(doctorSchedule);
            doctor.setCreateTime(new Timestamp(System.currentTimeMillis()));
            doctor.setUpdateTime(new Timestamp(System.currentTimeMillis()));
            doctorRepository.save(doctor);
            return new Result(ResultCode.R_Ok, "添加医生成功");
        }catch(Exception e){
            logUtil.error("添加医生失败", e);
            return new Result(ResultCode.R_Error, "添加医生失败");
        }
    }

    @Override
    public Result getDoctor(String doctorName, Integer pageNum, Integer pageSize) {
        try{
            PageResponse<Doctor> pageResponse = new PageResponse<>();
            Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
            if(doctorName == null || doctorName.isEmpty()){
                Page<Doctor> doctors = doctorRepository.findAll(pageable);
                pageResponse.setTotal_item(doctors.getTotalElements());
                pageResponse.setData(doctors.getContent());
                return new Result(ResultCode.R_Ok, pageResponse);
            }else{
                Page<Doctor> doctors = doctorRepository.findByDoctorName(doctorName, pageable);
                pageResponse.setTotal_item(doctors.getTotalElements());
                pageResponse.setData(doctors.getContent());
                return new Result(ResultCode.R_Ok, pageResponse);
            }
        }catch(Exception e){
            logUtil.error("获取医生失败", e);
            return new Result(ResultCode.R_Error, "获取医生失败");
        }
    }

    @Override
    public Result updateDoctor(String doctorId, String doctorName, String doctorSchedule, MultipartFile doctorAvatar, String introduction, Integer workingYears) {
        try{
            Doctor doctor = doctorRepository.findByDoctorId(doctorId);
            if(doctor == null){
                return new Result(ResultCode.R_Error, "医生不存在");
            }
            if(doctorName != null && !doctorName.isEmpty()){
                doctor.setDoctorName(doctorName);
            }
            if(doctorAvatar != null && !doctorAvatar.isEmpty()){
                String doctorAvatarUrl = FileUtil.saveFile(doctorAvatar, doctorPicturesPath);
                if(doctor.getDoctorAvatar() != null){
                    FileUtil.deleteFile(doctor.getDoctorAvatar(), doctorPicturesPath);
                }
                doctor.setDoctorAvatar(doctorAvatarUrl);
            }
            if(introduction != null && !introduction.isEmpty()){
                doctor.setIntroduction(introduction);
            }
            if(workingYears != null){
                doctor.setWorkingYears(workingYears);
            }
            if(doctorSchedule != null && !doctorSchedule.isEmpty()){
                doctor.setDoctorSchedule(doctorSchedule);
            }
            doctor.setUpdateTime(new Timestamp(System.currentTimeMillis()));
            doctorRepository.save(doctor);
            return new Result(ResultCode.R_Ok, "更新医生成功");
        }catch(Exception e){
            logUtil.error("更新医生失败", e);
            return new Result(ResultCode.R_Error, "更新医生失败");
        }
    }
    
    @Override
    public Result deleteDoctor(String doctorId) {
        try{
            Doctor doctor = doctorRepository.findByDoctorId(doctorId);
            if(doctor == null){
                return new Result(ResultCode.R_Error, "医生不存在");
            }
            if(doctor.getDoctorAvatar() != null){
                FileUtil.deleteFile(doctor.getDoctorAvatar(), doctorPicturesPath);
            }
            doctorRepository.delete(doctor);
            //把item中的doctorjson数组里的对应doctor也删掉
            List<Item> items = itemRepository.findAll();
            for(Item item : items) {
                String doctorIdsJson = (String) item.getDoctorId();
                if(doctorIdsJson != null && !doctorIdsJson.isEmpty()) {
                    try {
                        String[] doctorIds = new ObjectMapper().readValue(doctorIdsJson, String[].class);
                        List<String> doctorIdList = new ArrayList<>(Arrays.asList(doctorIds));
                        doctorIdList.remove(doctorId);
                        if(doctorIdList.isEmpty()) {
                            item.setDoctorId(null);
                        } else {
                            item.setDoctorId(new ObjectMapper().writeValueAsString(doctorIdList.toArray(new String[0])));
                        }
                        itemRepository.save(item);
                    } catch(Exception ex) {
                        logUtil.error("从项目中移除医生失败", ex);
                    }
                }
            }
            return new Result(ResultCode.R_Ok, "删除医生成功");
        }catch(Exception e){
            logUtil.error("删除医生失败", e);
            return new Result(ResultCode.R_Error, "删除医生失败");
        }
    }

    @Override
    public Result getAllDoctor() {
        try{
            List<Doctor> doctors = doctorRepository.findAll();
            return new Result(ResultCode.R_Ok, doctors);
        }catch(Exception e){
            logUtil.error("获取所有医生失败", e);
            return new Result(ResultCode.R_Error, "获取所有医生失败");
        }
    }

    //==================================项目管理==================================
    @Override
    public Result getItem(String itemName, Integer pageNum, Integer pageSize) {
        try{
            PageResponse<Map<String, Object>> pageResponse = new PageResponse<>();
            Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
            Page<Item> page;
            if(itemName == null || itemName.isEmpty()){
                page = itemRepository.findAll(pageable);
            }else{
                page = itemRepository.findByItemName(itemName, pageable);
            }
            List<Item> items = page.getContent();
            List<Map<String, Object>> itemWithDoctors = new ArrayList<>();
            
            for (Item o : items) {
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
                itemWithDoctors.add(itemMap);
            }
            pageResponse.setTotal_item(page.getTotalElements());
            pageResponse.setData(itemWithDoctors);
            return new Result(ResultCode.R_Ok, pageResponse);
        }catch(Exception e){
            logUtil.error("获取项目失败", e);
            return new Result(ResultCode.R_Error, "获取项目失败");
        }
    }

    @Override
    public Result addItem(String itemName, String doctorId) {
        try{
            Item item = new Item();
            item.setItemId(UUID.randomUUID().toString());
            item.setItemName(itemName);
            String[] doctorIds = doctorId.split("\\|");
            String doctorIdsJson = new ObjectMapper().writeValueAsString(doctorIds);
            item.setDoctorId(doctorIdsJson);
            item.setCreateTime(new Timestamp(System.currentTimeMillis()));
            item.setUpdateTime(new Timestamp(System.currentTimeMillis()));
            itemRepository.save(item);
            return new Result(ResultCode.R_Ok, "添加项目成功");
        }catch(Exception e){
            logUtil.error("添加项目失败", e);
            return new Result(ResultCode.R_Error, "添加项目失败");
        }
    }

    @Override
    public Result updateItem(String itemId, String itemName, String doctorId) {
        try{
            Item item = itemRepository.findByItemId(itemId);
            if(item == null){
                return new Result(ResultCode.R_Error, "项目不存在");
            }
            if(itemName != null && !itemName.isEmpty()){
                Item itemByName = itemRepository.findByItemName(itemName);
                if(itemByName != null && !itemByName.getItemId().equals(itemId)){
                    return new Result(ResultCode.R_Error, "项目名称已存在");
                }
                item.setItemName(itemName);
            }
            if(doctorId != null && !doctorId.isEmpty()){
                String doctorIdsJson = (String) item.getDoctorId();
                String[] doctorIdsArray = new ObjectMapper().readValue(doctorIdsJson, String[].class);
                String[] doctorIds = doctorId.split("\\|");
                for(String id : doctorIds){
                    if(!Arrays.asList(doctorIdsArray).contains(id)){
                        doctorIdsArray = Arrays.copyOf(doctorIdsArray, doctorIdsArray.length + 1);
                        doctorIdsArray[doctorIdsArray.length - 1] = id;
                    }
                }
                String newDoctorIdsJson = new ObjectMapper().writeValueAsString(doctorIdsArray);
                item.setDoctorId(newDoctorIdsJson);
            }
            item.setUpdateTime(new Timestamp(System.currentTimeMillis()));
            itemRepository.save(item);
            return new Result(ResultCode.R_Ok, "更新项目成功");
        }catch(Exception e){
            logUtil.error("更新项目失败", e);
            return new Result(ResultCode.R_Error, "更新项目失败");
        }
    }

    @Override
    public Result deleteItem(String itemId) {
        try{
            Item item = itemRepository.findByItemId(itemId);
            if(item == null){
                return new Result(ResultCode.R_Error, "项目不存在");
            }
            itemRepository.delete(item);
            return new Result(ResultCode.R_Ok, "删除项目成功");
        }catch(Exception e){
            logUtil.error("删除项目失败", e);
            return new Result(ResultCode.R_Error, "删除项目失败");
        }
    }

    @Override
    public Result deleteItemDoctor(String itemId, String doctorId) {
        try{
            Item item = itemRepository.findByItemId(itemId);
            if(item == null){
                return new Result(ResultCode.R_Error, "项目不存在");
            }
            String doctorIdsJson = (String) item.getDoctorId();
            String[] doctorIdsArray = new ObjectMapper().readValue(doctorIdsJson, String[].class);
            
            // Create a new ArrayList from the array to make it modifiable
            List<String> doctorIdsList = new ArrayList<>(Arrays.asList(doctorIdsArray));
            doctorIdsList.remove(doctorId);
            
            // Convert back to array and then to JSON
            String newDoctorIdsJson = new ObjectMapper().writeValueAsString(doctorIdsList.toArray(new String[0]));
            item.setDoctorId(newDoctorIdsJson);
            item.setUpdateTime(new Timestamp(System.currentTimeMillis()));
            itemRepository.save(item);
            return new Result(ResultCode.R_Ok, "删除项目医生成功");
        }catch(Exception e){
            logUtil.error("删除项目医生失败", e);
            return new Result(ResultCode.R_Error, "删除项目医生失败");
        }
    }

    //==================================科普管理==================================
    @Override
    public Result addEducation(String educationTitle, String educationContent) {
        try{
            if(educationTitle == null || educationTitle.isEmpty()){
                return new Result(ResultCode.R_Error, "科普标题不能为空");
            }
            if(educationContent == null || educationContent.isEmpty()){
                return new Result(ResultCode.R_Error, "科普内容不能为空");
            }
            Education education = new Education();
            education.setEducationId(UUID.randomUUID().toString());
            education.setEducationTitle(educationTitle);
            education.setEducationContent(educationContent);
            education.setCreateTime(new Timestamp(System.currentTimeMillis()));
            education.setUpdateTime(new Timestamp(System.currentTimeMillis()));
            educationRepository.save(education);
            return new Result(ResultCode.R_Ok, "添加科普成功");
        }catch(Exception e){
            logUtil.error("添加科普失败", e);
            return new Result(ResultCode.R_Error, "添加科普失败");
        }
    }

    @Override
    public Result getEducation(String educationTitle, Integer pageNum, Integer pageSize) {
        try{
            PageResponse<Education> pageResponse = new PageResponse<>();
            Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
            Page<Education> page;
            if(educationTitle == null || educationTitle.isEmpty()){
                page = educationRepository.findAll(pageable);
            }else{
                page = educationRepository.findByEducationTitle(educationTitle, pageable);
            }
            pageResponse.setTotal_item(page.getTotalElements());
            pageResponse.setData(page.getContent());
            return new Result(ResultCode.R_Ok, pageResponse);
        }catch(Exception e){
            logUtil.error("获取科普失败", e);
            return new Result(ResultCode.R_Error, "获取科普失败");
        }
    }

    @Override
    public Result updateEducation(String educationId, String educationTitle, String educationContent) {
        try{
            Education education = educationRepository.findByEducationId(educationId);
            if(education == null){
                return new Result(ResultCode.R_Error, "科普不存在");
            }
            if(educationTitle != null && !educationTitle.isEmpty()){
                education.setEducationTitle(educationTitle);
            }
            if(educationContent != null && !educationContent.isEmpty()){
                education.setEducationContent(educationContent);
            }
            education.setUpdateTime(new Timestamp(System.currentTimeMillis()));
            educationRepository.save(education);
            return new Result(ResultCode.R_Ok, "更新科普成功");
        }catch(Exception e){
            logUtil.error("更新科普失败", e);
            return new Result(ResultCode.R_Error, "更新科普失败");
        }
    }

    @Override
    public Result deleteEducation(String educationId) {
        try{
            Education education = educationRepository.findByEducationId(educationId);
            if(education == null){
                return new Result(ResultCode.R_Error, "科普不存在");
            }
            educationRepository.delete(education);
            return new Result(ResultCode.R_Ok, "删除科普成功");
        }catch(Exception e){
            logUtil.error("删除科普失败", e);
            return new Result(ResultCode.R_Error, "删除科普失败");
        }
    }
    

    //==================================预约管理==================================
    @Override
    public Result getAppointment(String itemName, String doctorName, String userName, Integer pageNum, Integer pageSize) {
        try{
            PageResponse<Map<String, Object>> pageResponse = new PageResponse<>();
            Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
            Page<Appointment> page;
            Item item = null;
            if(itemName != null && !itemName.isEmpty()){
                item = itemRepository.findByItemName(itemName);
                if(item == null){
                    return new Result(ResultCode.R_Error, "项目不存在");
                }
            }
            Doctor doctor = null;
            if(doctorName != null && !doctorName.isEmpty()){
                doctor = doctorRepository.findByDoctorName(doctorName);
                if(doctor == null){
                    return new Result(ResultCode.R_Error, "医生不存在");
                }
            }
            User user = null;
            if(userName != null && !userName.isEmpty()){
                user = userRepository.findByUserName(userName);
                if(user == null){
                    return new Result(ResultCode.R_Error, "用户不存在");
                }
            }
            if(item != null && doctor != null && user != null){
                page = appointmentRepository.findByItemIdAndDoctorIdAndUserIdOrderByAppointmentTimeDesc(item.getItemId(), doctor.getDoctorId(), user.getUserId(), pageable);
            }else if(item != null && doctor != null){
                page = appointmentRepository.findByItemIdAndDoctorIdOrderByAppointmentTimeDesc(item.getItemId(), doctor.getDoctorId(), pageable);
            }else if(item != null && user != null){
                page = appointmentRepository.findByItemIdAndUserIdOrderByAppointmentTimeDesc(item.getItemId(), user.getUserId(), pageable);
            }else if(doctor != null && user != null){
                page = appointmentRepository.findByDoctorIdAndUserIdOrderByAppointmentTimeDesc(doctor.getDoctorId(), user.getUserId(), pageable);
            }else if(item != null){
                page = appointmentRepository.findByItemIdOrderByAppointmentTimeDesc(item.getItemId(), pageable);
            }else if(doctor != null){
                page = appointmentRepository.findByDoctorIdOrderByAppointmentTimeDesc(doctor.getDoctorId(), pageable);
            }else if(user != null){
                page = appointmentRepository.findByUserIdOrderByAppointmentTimeDesc(user.getUserId(), pageable);
            }else{
                page = appointmentRepository.findAllByOrderByAppointmentTimeDesc(pageable);
            }
            List<Appointment> appointments = page.getContent();
            List<Map<String, Object>> appointmentWithItems = new ArrayList<>();
            for (Appointment o : appointments) {
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
                appointmentWithItems.add(appointmentMap);
            }
            pageResponse.setTotal_item(page.getTotalElements());
            pageResponse.setData(appointmentWithItems);
            return new Result(ResultCode.R_Ok, pageResponse);
        }catch(Exception e){
            logUtil.error("获取预约失败", e);
            return new Result(ResultCode.R_Error, "获取预约失败");
        }
    }

    @Override
    public Result uploadResult(Appointment appointment) {
        try{
            Appointment queryAppointment = appointmentRepository.findByAppointmentId(appointment.getAppointmentId());
            if(queryAppointment == null){
                return new Result(ResultCode.R_Error, "预约不存在");
            }
            queryAppointment.setResult(appointment.getResult());
            queryAppointment.setStatus(AppointmentConstantdata.APPOINTMENT_STATUS_UPLOAD);
            queryAppointment.setUpdateTime(new Timestamp(System.currentTimeMillis()));
            appointmentRepository.save(queryAppointment);
            return new Result(ResultCode.R_Ok, "上传结果成功");
        }catch(Exception e){
            logUtil.error("上传结果失败", e);
            return new Result(ResultCode.R_Error, "上传结果失败");
        }
    }

    @Override
    public Result breachAppointment(Appointment appointment) {
        try{
            Appointment queryAppointment = appointmentRepository.findByAppointmentId(appointment.getAppointmentId());
            if(queryAppointment == null){
                return new Result(ResultCode.R_Error, "预约不存在");
            }
            queryAppointment.setStatus(AppointmentConstantdata.APPOINTMENT_STATUS_BREACH);
            queryAppointment.setUpdateTime(new Timestamp(System.currentTimeMillis()));
            appointmentRepository.save(queryAppointment);
            User queryUser = userRepository.findByUserId(queryAppointment.getUserId());
            if(queryUser != null){
                queryUser.setUserBreach(queryUser.getUserBreach() + 1);
                userRepository.save(queryUser);
            }
            return new Result(ResultCode.R_Ok);
        }catch(Exception e){
            logUtil.error("设置违约失败", e);
            return new Result(ResultCode.R_Error, "设置违约失败");
        }
    }
    //==================================咨询管理==================================
    @Override
    public Result getConsultation() {
        try{
            List<Consultation> consultations = consultationRepository.findAll();
            return new Result(ResultCode.R_Ok, consultations);
        }catch(Exception e){
            logUtil.error("获取咨询失败", e);
            return new Result(ResultCode.R_Error, "获取咨询失败");
        }
    }

    @Override
    public Result reply(Consultation consultation) {
        try{
            Consultation queryConsultation = consultationRepository.findByConsultationId(consultation.getConsultationId());
            if(queryConsultation == null){
                return new Result(ResultCode.R_Error, "咨询不存在");
            }
            queryConsultation.setAnswer(consultation.getAnswer());
            queryConsultation.setUpdateTime(new Timestamp(System.currentTimeMillis()));
            consultationRepository.save(queryConsultation);
            return new Result(ResultCode.R_Ok, "回复咨询成功");
        }catch(Exception e){
            logUtil.error("回复咨询失败", e);
            return new Result(ResultCode.R_Error, "回复咨询失败");
        }
    }
}
