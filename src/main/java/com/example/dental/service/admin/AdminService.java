package com.example.dental.service.admin;

import com.example.dental.common.Response.Result;
import com.example.dental.model.Admin;

public interface AdminService {

    Result login(Admin admin);

    Result logout(String token);

    Result getAdminInfo();
    
}
