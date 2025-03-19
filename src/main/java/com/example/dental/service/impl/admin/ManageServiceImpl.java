package com.example.dental.service.impl.admin;

import com.example.dental.model.Doctor;
import com.example.dental.model.Item;

import java.sql.Timestamp;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.example.dental.common.Response.Result;
import com.example.dental.common.Response.ResultCode;
import com.example.dental.repository.DoctorRepository;
import com.example.dental.repository.ItemRepository;
import com.example.dental.service.admin.ManageService;
import com.example.dental.utils.FileUtil;
import com.example.dental.utils.LogUtil;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ManageServiceImpl implements ManageService {

    private static final LogUtil logUtil = LogUtil.getLogger(ManageServiceImpl.class);

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Value("${uploadFilePath.doctorPicturesPath}")
    private String doctorPicturesPath;

    //==================================医生管理==================================
    @Override
    public Result addDoctor(String doctorName, MultipartFile doctorAvatar, String introduction, String workingYears) {
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
            if(workingYears == null || workingYears.isEmpty()){
                return new Result(ResultCode.R_Error, "医生工作年限不能为空");
            }
            Doctor doctor = new Doctor();
            doctor.setDoctorId(UUID.randomUUID().toString());
            doctor.setDoctorName(doctorName);
            String doctorAvatarUrl = FileUtil.saveFile(doctorAvatar, doctorPicturesPath);
            doctor.setDoctorAvatar(doctorAvatarUrl);
            doctor.setIntroduction(introduction);
            doctor.setWorkingYears(workingYears);
            doctor.setCreateTime(new Timestamp(System.currentTimeMillis()));
            doctor.setUpdateTime(new Timestamp(System.currentTimeMillis()));
            doctorRepository.save(doctor);
            return new Result(ResultCode.R_Ok, "添加医生成功");
        }catch(Exception e){
            logUtil.error("添加医生失败", e);
            return new Result(ResultCode.R_Error, "添加医生失败");
        }
    }



    //==================================项目管理==================================
    @Override
    public Result getItem(String itemName, String pageNum, String pageSize) {
        return null;
    }

    @Override
    public Result addItem(Item item) {
        return null;
    }

    
}
