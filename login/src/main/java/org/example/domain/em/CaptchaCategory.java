package org.example.domain.em;

import cn.hutool.captcha.AbstractCaptcha;
import cn.hutool.captcha.CircleCaptcha;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.captcha.ShearCaptcha;
import lombok.Getter;

@Getter
public enum CaptchaCategory {

    /**
     * 线段干扰
     */
    LINE(LineCaptcha.class),

    /**
     * 圆圈干扰
     */
    CIRCLE(CircleCaptcha.class),

    /**
     * 扭曲干扰
     */
    SHEAR(ShearCaptcha.class);

    private final Class<? extends AbstractCaptcha> clazz;

    // Constructor
    CaptchaCategory(Class<? extends AbstractCaptcha> clazz) {
        this.clazz = clazz;
    }

    // Getter method
    public Class<? extends AbstractCaptcha> getClazz() {
        return clazz;
    }
}