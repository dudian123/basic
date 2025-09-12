package org.example.controller;

import org.example.utils.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试控制器
 */
@RestController
@RequestMapping("/test")
public class TestController {

    /**
     * 测试简单字符串返回
     */
    @GetMapping("/string")
    public R<String> testString() {
        return R.ok("测试字符串");
    }

    /**
     * 测试Map返回
     */
    @GetMapping("/map")
    public R<Map<String, Object>> testMap() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", "测试");
        data.put("value", 123);
        return R.ok(data);
    }

    /**
     * 测试空数据返回
     */
    @GetMapping("/null")
    public R<Object> testNull() {
        return R.ok(null);
    }
}