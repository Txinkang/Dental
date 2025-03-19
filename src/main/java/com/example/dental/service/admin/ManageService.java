package com.example.dental.service.admin;

import org.springframework.web.multipart.MultipartFile;

import com.example.dental.common.Response.Result;
import com.example.dental.model.Doctor;
import com.example.dental.model.Item;

public interface ManageService {
    Result getItem(String itemName, String pageNum, String pageSize);

    Result addItem(Item item);

    Result addDoctor(String doctorName, MultipartFile doctorAvatar, String introduction, String workingYears);
}
