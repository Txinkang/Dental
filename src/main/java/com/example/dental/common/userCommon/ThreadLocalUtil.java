package com.example.dental.common.userCommon;

import com.example.dental.repository.UserRepository;
import com.example.dental.utils.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * ThreadLocal 工具类
 */
@SuppressWarnings("all")
@Component
public class ThreadLocalUtil {
    private static UserRepository userRepository;
    
    private static final LogUtil logUtil = LogUtil.getLogger(ThreadLocalUtil.class);
    private static final ThreadLocal<Object> THREAD_LOCAL = new ThreadLocal<>();

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        ThreadLocalUtil.userRepository = userRepository;
    }

    //提供get方法，拿取线程存储的值
    public static <T> T get(){
        try {
            return (T) THREAD_LOCAL.get();
        }catch (Exception e){
            logUtil.error("ThreadLocalUtil get value error : " + e);
            return null;
        }
    }

    //getUserId，拿取线程存储的UserId
    public static String getUserId() {
        try {
            Map<String, Object> userMap = get();
            if (userMap == null || !userMap.containsKey("id")) {
                logUtil.error("ThreadLocalUtil getUserId error: userMap is null or id not found");
                return null;
            }
            String userId = (String) userMap.get("id");
            if (userId == null || userId.isEmpty()) {
                logUtil.error("ThreadLocalUtil getUserId error: userId is null or empty");
                return null;
            }
            return userId;
        } catch (Exception e) {
            logUtil.error("ThreadLocalUtil getUserId error: " + e);
            return null;
        }
    }

    // 获取用户名
    public static String getUserName() {
        try {
            Map<String, Object> userMap = get();
            if (userMap == null || !userMap.containsKey("name")) {
                logUtil.error("ThreadLocalUtil getUserName error: userMap is null or name not found");
                return null;
            }
            String userName = (String) userMap.get("name");
            if (userName == null || userName.isEmpty()) {
                logUtil.error("ThreadLocalUtil getUserName error: userName is null or empty");
                return null;
            }
            return userName;
        } catch (Exception e) {
            logUtil.error("ThreadLocalUtil getUserName error: " + e);
            return null;
        }
    }

    //提供set方法，向线程内写入值
    public static void set(Object value){
        try {
            THREAD_LOCAL.set(value);
        }catch (Exception e){
            logUtil.error("ThreadLocalUtil set value error : " + e);
        }
    }

    //使用完毕，移除线程，防止内存泄漏
    public static void remove(){
        try {
            THREAD_LOCAL.remove();
        }catch (Exception e){
            logUtil.error("ThreadLocalUtil remove value error : " + e);
        }
    }

    


}
