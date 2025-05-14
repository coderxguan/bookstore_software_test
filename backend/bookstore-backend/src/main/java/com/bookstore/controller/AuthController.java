package com.bookstore.controller;

import com.bookstore.entity.User;
import com.bookstore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> loginForm) {
        String username = loginForm.get("username");
        String password = loginForm.get("password");
        
        Map<String, Object> result = new HashMap<>();
        
        User user = userService.login(username, password);
        if (user != null) {
            result.put("code", 200);
            result.put("message", "登录成功");
            
            Map<String, Object> data = new HashMap<>();
            // 生成令牌，实际项目中应使用JWT等技术
            String token = "mock-token-" + UUID.randomUUID().toString().replaceAll("-", "");
            data.put("token", token);
            
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("id", user.getId());
            userMap.put("username", user.getUsername());
            
            data.put("user", userMap);
            
            result.put("data", data);
        } else {
            result.put("code", 400);
            result.put("message", "用户名或密码错误");
        }
        
        return result;
    }
    
    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody User user) {
        Map<String, Object> result = new HashMap<>();
        
        // 检查用户名是否已存在
        if (userService.checkUsernameExists(user.getUsername())) {
            result.put("code", 400);
            result.put("message", "用户名已存在");
            return result;
        }
        
        // 设置默认状态为启用
        user.setStatus(1);
        
        // 注册用户
        boolean success = userService.register(user);
        if (success) {
            result.put("code", 200);
            result.put("message", "注册成功");
            
            // 隐藏密码
            user.setPassword(null);
            result.put("data", user);
        } else {
            result.put("code", 500);
            result.put("message", "注册失败");
        }
        
        return result;
    }
    
    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public Map<String, Object> logout() {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "登出成功");
        return result;
    }
} 