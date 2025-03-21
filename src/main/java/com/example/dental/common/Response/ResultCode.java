package com.example.dental.common.Response;

public enum ResultCode {
    //成功
    R_Ok(200, "操作成功"),
    //失败
    R_Fail(400, "操作失败"),
    //异常
    R_Error(500, "操作异常"),

    R_TimeOut(600,"超时"),
    //-----------------------系统内部错误(不应该出现但防止出现) 501-600---------------------
    R_WhyNull(501, "不可能NULL的数据为NULL了"),
    R_UpdateDbFailed(502, "修改数据库失败"),



    //-----------------------用户相关错误 401-500---------------------
    R_ParamError(401,"参数异常"),
    R_UserAccountIsExist(402,"用户账号已存在"),
    R_UserNotFound(405,"该用户不存在"),
    R_PasswordError(406,"密码错误"),
    R_OldPasswordError(407,"旧密码错误"),
    R_NewPasswordNotSame(408,"新密码不一致"),
    R_RoleAlreadyUpgrade(409,"权限早已升级"),
    R_CodeError(410,"验证码错误"),
    R_NoAuth(411,"该用户没有权限"),
    R_FileExists(412,"文件已存在"),
    R_FileNotFound(413,"文件不存在"),
    R_FileNameExists(414,"文件名已存在"),
    R_BalanceNotEnough(415,"余额不足"),
    R_UserNameIsExist(416,"用户名已存在"),
    R_UserEmailIsExist(417,"邮箱已存在"),
    //-----------------------系统相关错误 401-500---------------------
    R_SaveFileError(503,"文件保存异常"),
    R_DeleteFileError(504,"文件删除异常"), 
    ;

    private int code;
    private String msg;
    ResultCode(int code,String msg){
        this.code=code;
        this.msg=msg;
    }
    public int getCode(){
        return code;
    }
    public String getMsg(){
        return msg;
    }
}
