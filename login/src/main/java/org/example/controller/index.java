package org.example.controller;

import org.example.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class index {

    @Autowired
    private AuthController authController;

    // 公开接口，无需权限验证
    @RequestMapping("/auth/code")
    public Object getCode(){
        return authController.getCode();
    }
}
