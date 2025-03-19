package com.example.dental.service.user;

import java.math.BigDecimal;

import com.example.dental.common.Response.Result;
import com.example.dental.model.User;

public interface UserService {

    Result register(User user);

    Result login(User user);

    Result logout(String token);

    Result getUserInfo();

    Result updateUserInfo(User user);

    Result recharge(BigDecimal amount);

}
