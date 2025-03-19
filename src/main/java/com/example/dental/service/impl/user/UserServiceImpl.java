package com.example.dental.service.impl.user;

import com.example.dental.common.Redis.RedisService;
import com.example.dental.common.Response.Result;
import com.example.dental.common.Response.ResultCode;
import com.example.dental.common.userCommon.ThreadLocalUtil;
import com.example.dental.constant.MagicMathConstData;
import com.example.dental.constant.RedisConstData;
import com.example.dental.model.User;
import com.example.dental.repository.UserRepository;
import com.example.dental.service.user.UserService;
import com.example.dental.utils.JwtUtil;
import com.example.dental.utils.LogUtil;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
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
            //设置用户ID和初始余额
            user.setUserId(UUID.randomUUID().toString());
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

    @Override
    public Result recharge(BigDecimal amount) {
        try {
            BigDecimal decimalAmount = new BigDecimal(String.valueOf(amount));
            if (decimalAmount.compareTo(BigDecimal.ZERO) <= 0) {
                return new Result(ResultCode.R_ParamError);
            }
            String userId = ThreadLocalUtil.getUserId();
            if (userId == null) {
                return new Result(ResultCode.R_Error);
            }
            User queryUser = userRepository.findByUserId(userId);
            if (queryUser == null) {
                return new Result(ResultCode.R_UserNotFound);
            }
            userRepository.saveAndFlush(queryUser);
            return new Result(ResultCode.R_Ok);
        } catch (Exception e) {
            logUtil.error("充值失败", e);
            return new Result(ResultCode.R_UpdateDbFailed);
        }
    }
}
