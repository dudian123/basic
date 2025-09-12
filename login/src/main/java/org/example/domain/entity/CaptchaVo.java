package org.example.domain.entity;

import lombok.Data;

@Data
public class CaptchaVo {

    /**
     * 是否开启验证码
     */
    private Boolean captchaEnabled = false;

    private String uuid;

    /**
     * 验证码图片
     */
    private String img;

    // Getter and Setter methods
    public Boolean getCaptchaEnabled() {
        return captchaEnabled;
    }

    public void setCaptchaEnabled(Boolean captchaEnabled) {
        this.captchaEnabled = captchaEnabled;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

}