package com.imooc.miaosha.vo;

import com.imooc.miaosha.validators.isMobile;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

public class LoginVo {

    @NotNull
    @isMobile
    private String mobile;

    @NotNull
    @Length(min = 32)
    private String password;//最后加盐的密码
    private String inputPass;//输入的密码

    public String getInputPass() {
        return inputPass;
    }

    public void setInputPass(String inputPass) {
        this.inputPass = inputPass;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "mobile:"+this.mobile+"pssword:"+this.password+"inputPass:"+this.inputPass;
    }
}
