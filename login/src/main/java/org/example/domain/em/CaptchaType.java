package org.example.domain.em;

import cn.hutool.captcha.generator.CodeGenerator;
import cn.hutool.captcha.generator.RandomGenerator;
import lombok.Getter;
import org.example.utils.UnsignedMathGenerator;

@Getter
public enum CaptchaType {

    /**
     * 数字
     */
    MATH(UnsignedMathGenerator.class),

    /**
     * 字符
     */
    CHAR(RandomGenerator.class);

    private final Class<? extends CodeGenerator> clazz;

    // Constructor
    CaptchaType(Class<? extends CodeGenerator> clazz) {
        this.clazz = clazz;
    }

    public Class<? extends CodeGenerator> getClazz() {
        return clazz;
    }
}