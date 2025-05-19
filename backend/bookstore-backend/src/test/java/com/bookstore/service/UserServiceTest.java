package com.bookstore.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bookstore.entity.User;
import com.bookstore.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 用户界面与登录注册模块测试类(丁)
 * 包含黑盒测试(边界值分析法)和白盒测试(语句覆盖、条件覆盖、路径测试)
 */
@SpringBootTest
@Transactional
@DisplayName("用户界面与登录注册模块测试")
public class UserServiceTest {

    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private UserService userService;
    
    // 测试数据
    private User normalUser;
    private User disabledUser;
    
    @BeforeEach
    public void setUp() {
        // 清理测试数据
        cleanup();
        
        // 初始化正常用户
        normalUser = new User();
        normalUser.setUsername("testuser");
        normalUser.setPassword("encodedPassword"); // 模拟加密后的密码
        normalUser.setStatus(1); // 正常状态
        userMapper.insert(normalUser);
        
        // 查询数据库获取ID
        LambdaQueryWrapper<User> normalQuery = new LambdaQueryWrapper<>();
        normalQuery.eq(User::getUsername, "testuser");
        normalUser = userMapper.selectOne(normalQuery);
        
        // 初始化被禁用用户
        disabledUser = new User();
        disabledUser.setUsername("disableduser");
        disabledUser.setPassword("encodedPassword"); // 模拟加密后的密码
        disabledUser.setStatus(0); // 禁用状态
        userMapper.insert(disabledUser);
        
        // 查询数据库获取ID
        LambdaQueryWrapper<User> disabledQuery = new LambdaQueryWrapper<>();
        disabledQuery.eq(User::getUsername, "disableduser");
        disabledUser = userMapper.selectOne(disabledQuery);
    }
    
    /**
     * 清理测试数据
     */
    private void cleanup() {
        LambdaQueryWrapper<User> cleanup = new LambdaQueryWrapper<>();
        cleanup.eq(User::getUsername, "testuser")
            .or().eq(User::getUsername, "disableduser")
            .or().eq(User::getUsername, "user1")
            .or().eq(User::getUsername, "user123456789012345")
            .or().eq(User::getUsername, "newuser");
        userMapper.delete(cleanup);
    }
    
    /**
     * 测试用例: TC-U01 用户名长度边界(最小)
     * 边界值分析: 测试最小长度(5字符)的用户名
     */
    @DisplayName("TC-U01: 用户名最小长度测试")
    @Test
    public void testMinimumUsernameLength() {
        // 准备数据 - 最小长度用户名
        String minUsername = "user1"; // 5字符
        String password = "password";
        
        // 执行测试
        User user = new User();
        user.setUsername(minUsername);
        user.setPassword(password);
        
        boolean result = userService.register(user);
        
        // 验证结果
        assertTrue(result, "注册应成功");
        
        // 验证数据库中存在该用户
        LambdaQueryWrapper<User> query = new LambdaQueryWrapper<>();
        query.eq(User::getUsername, minUsername);
        User savedUser = userMapper.selectOne(query);
        assertNotNull(savedUser, "用户应保存到数据库");
        assertEquals(minUsername, savedUser.getUsername(), "用户名应正确保存");
    }
    
    /**
     * 测试用例: TC-U02 用户名长度边界(最大)
     * 边界值分析: 测试最大长度(20字符)的用户名
     */
    @DisplayName("TC-U02: 用户名最大长度测试")
    @Test
    public void testMaximumUsernameLength() {
        // 准备数据 - 最大长度用户名
        String maxUsername = "user123456789012345"; // 20字符
        String password = "password";
        
        // 执行测试
        User user = new User();
        user.setUsername(maxUsername);
        user.setPassword(password);
        
        boolean result = userService.register(user);
        
        // 验证结果
        assertTrue(result, "注册应成功");
        
        // 验证数据库中存在该用户
        LambdaQueryWrapper<User> query = new LambdaQueryWrapper<>();
        query.eq(User::getUsername, maxUsername);
        User savedUser = userMapper.selectOne(query);
        assertNotNull(savedUser, "用户应保存到数据库");
        assertEquals(maxUsername, savedUser.getUsername(), "用户名应正确保存");
    }
    
    /**
     * 测试用例: TC-U03 用户名长度边界(不足)
     * 边界值分析: 测试不足长度(4字符)的用户名
     */
    @DisplayName("TC-U03: 用户名长度不足测试")
    @Test
    public void testInsufficientUsernameLength() {
        // 准备数据 - 不足长度用户名
        String shortUsername = "usr"; // 3字符
        String password = "password";
        
        // 执行测试
        User user = new User();
        user.setUsername(shortUsername);
        user.setPassword(password);
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.register(user);
        });
        
        // 验证结果
        assertTrue(exception.getMessage().contains("用户名长度必须在5-20个字符之间"), 
                "应提示用户名长度不足");
        
        // 验证数据库中不存在该用户
        LambdaQueryWrapper<User> query = new LambdaQueryWrapper<>();
        query.eq(User::getUsername, shortUsername);
        User savedUser = userMapper.selectOne(query);
        assertNull(savedUser, "不应保存到数据库");
    }
    
    /**
     * 测试用例: TC-U04 用户名长度边界(超出)
     * 边界值分析: 测试超出长度(21字符)的用户名
     */
    @DisplayName("TC-U04: 用户名长度超出测试")
    @Test
    public void testExceedUsernameLength() {
        // 准备数据 - 超长用户名
        String longUsername = "u12345678901234567890"; // 21字符
        String password = "password";
        
        // 执行测试
        User user = new User();
        user.setUsername(longUsername);
        user.setPassword(password);
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.register(user);
        });
        
        // 验证结果
        assertTrue(exception.getMessage().contains("用户名长度必须在5-20个字符之间"), 
                "应提示用户名长度超出");
        
        // 验证数据库中不存在该用户
        LambdaQueryWrapper<User> query = new LambdaQueryWrapper<>();
        query.eq(User::getUsername, longUsername);
        User savedUser = userMapper.selectOne(query);
        assertNull(savedUser, "不应保存到数据库");
    }
    
    /**
     * 测试用例: TC-U05 登录状态测试
     * 功能测试: 测试用户状态检查功能
     */
    @DisplayName("TC-U05: 用户状态检查测试")
    @Test
    public void testUserStatus() {
        // 验证结果
        assertNotNull(normalUser, "应有有效用户");
        assertEquals(1, normalUser.getStatus(), "用户状态应为正常");
        
        assertNotNull(disabledUser, "应有禁用用户");
        assertEquals(0, disabledUser.getStatus(), "用户状态应为禁用");
    }
    
    /**
     * 测试用例: TC-WU01 用户注册语句覆盖
     * 白盒测试: 语句覆盖 - UserServiceImpl.register()
     */
    @DisplayName("TC-WU01: 用户注册语句覆盖测试")
    @Test
    public void testRegisterStatementCoverage() {
        // 准备测试数据
        String username = "newuser";
        String password = "password";
        
        // 执行测试
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        
        boolean result = userService.register(user);
        
        // 验证结果
        assertTrue(result, "注册应成功");
        
        // 验证数据库中的用户
        LambdaQueryWrapper<User> query = new LambdaQueryWrapper<>();
        query.eq(User::getUsername, username);
        User savedUser = userMapper.selectOne(query);
        assertNotNull(savedUser, "用户应保存到数据库");
        assertEquals(username, savedUser.getUsername(), "用户名应正确");
        assertEquals(password, savedUser.getPassword(), "密码应相同"); // 注意：真实应用中应该加密
        assertEquals(1, savedUser.getStatus(), "状态应为正常");
    }
    
    /**
     * 测试用例: TC-WU02 用户登录语句覆盖
     * 白盒测试: 语句覆盖 - UserServiceImpl.login()
     */
    @DisplayName("TC-WU02: 用户登录语句覆盖测试")
    @Test
    public void testLoginStatementCoverage() {
        // 准备测试数据
        String username = normalUser.getUsername();
        String password = normalUser.getPassword();
        
        // 执行测试
        User loggedInUser = userService.login(username, password);
        
        // 验证结果
        assertNotNull(loggedInUser, "登录应成功");
        assertEquals(normalUser.getId(), loggedInUser.getId(), "应返回正确的用户");
    }
    
    /**
     * 测试用例: TC-CU01 用户名已存在判断
     * 白盒测试: 条件覆盖 - 用户名已存在分支
     */
    @DisplayName("TC-CU01: 用户名已存在条件测试")
    @Test
    public void testUsernameExistsConditionTrue() {
        // 已存在的用户名
        String existingUsername = normalUser.getUsername();
        
        // 执行测试
        User user = new User();
        user.setUsername(existingUsername);
        user.setPassword("password");
        
        boolean result = userService.register(user);
        
        // 验证结果
        assertFalse(result, "已存在用户名的注册应失败");
    }
    
    /**
     * 测试用例: TC-CU01 用户名不存在判断
     * 白盒测试: 条件覆盖 - 用户名不存在分支
     */
    @DisplayName("TC-CU01: 用户名不存在条件测试")
    @Test
    public void testUsernameExistsConditionFalse() {
        // 不存在的用户名
        String nonExistingUsername = "nonexisting";
        
        // 确保用户名不存在
        LambdaQueryWrapper<User> query = new LambdaQueryWrapper<>();
        query.eq(User::getUsername, nonExistingUsername);
        User existingUser = userMapper.selectOne(query);
        assertNull(existingUser, "测试前用户不应存在");
        
        // 执行测试
        User user = new User();
        user.setUsername(nonExistingUsername);
        user.setPassword("password");
        
        boolean result = userService.register(user);
        
        // 验证结果
        assertTrue(result, "不存在用户名的注册应成功");
        
        // 验证用户已创建
        User newUser = userMapper.selectOne(query);
        assertNotNull(newUser, "用户应成功创建");
    }
    
    /**
     * 测试用例: TC-CU02 密码匹配判断
     * 白盒测试: 条件覆盖 - 密码匹配分支
     */
    @DisplayName("TC-CU02: 密码匹配条件测试")
    @Test
    public void testPasswordMatchesConditionTrue() {
        // 使用正确的用户名和密码
        String username = normalUser.getUsername();
        String correctPassword = normalUser.getPassword();
        
        // 执行测试
        User loginUser = userService.login(username, correctPassword);
        
        // 验证结果
        assertNotNull(loginUser, "正确密码应登录成功");
        assertEquals(normalUser.getId(), loginUser.getId(), "返回用户ID应匹配");
    }
    
    /**
     * 测试用例: TC-CU02 密码不匹配判断
     * 白盒测试: 条件覆盖 - 密码不匹配分支
     */
    @DisplayName("TC-CU02: 密码不匹配条件测试")
    @Test
    public void testPasswordMatchesConditionFalse() {
        // 使用正确的用户名和错误的密码
        String username = normalUser.getUsername();
        String wrongPassword = "wrongpassword";
        
        // 执行测试
        User loginUser = userService.login(username, wrongPassword);
        
        // 验证结果
        assertNull(loginUser, "错误密码应登录失败");
    }
    
    /**
     * 测试用例: TC-CU03 用户正常状态判断
     * 白盒测试: 条件覆盖 - 用户状态正常分支
     */
    @DisplayName("TC-CU03: 用户正常状态条件测试")
    @Test
    public void testUserStatusNormalCondition() {
        // 使用正常状态用户
        String username = normalUser.getUsername();
        String password = normalUser.getPassword();
        
        // 执行测试
        User loginUser = userService.login(username, password);
        
        // 验证结果
        assertNotNull(loginUser, "正常状态用户应登录成功");
        assertEquals(normalUser.getId(), loginUser.getId(), "返回用户ID应匹配");
    }
    
    /**
     * 测试用例: TC-CU03 用户禁用状态判断
     * 白盒测试: 条件覆盖 - 用户状态禁用分支
     */
    @DisplayName("TC-CU03: 用户禁用状态条件测试")
    @Test
    public void testUserStatusDisabledCondition() {
        // 使用禁用状态用户
        String username = disabledUser.getUsername();
        String password = disabledUser.getPassword();
        
        // 执行测试
        User loginUser = userService.login(username, password);
        
        // 验证结果
        assertNull(loginUser, "禁用状态用户应登录失败");
    }
    
    /**
     * 测试用例: TC-PU01 登录成功路径
     * 白盒测试: 路径测试 - 登录成功路径
     */
    @DisplayName("TC-PU01: 登录成功路径测试")
    @Test
    public void testLoginSuccessPath() {
        // 准备数据 - 正常用户、正确密码、正常状态
        String username = normalUser.getUsername();
        String password = normalUser.getPassword();
        
        // 执行测试
        User loginUser = userService.login(username, password);
        
        // 验证结果
        assertNotNull(loginUser, "成功路径应返回用户对象");
        assertEquals(normalUser.getId(), loginUser.getId(), "应返回正确的用户ID");
        assertEquals(username, loginUser.getUsername(), "应返回正确的用户名");
        assertEquals(1, loginUser.getStatus(), "应返回正确的状态");
    }
    
    /**
     * 测试用例: TC-PU02 登录失败路径(用户不存在)
     * 白盒测试: 路径测试 - 登录失败路径(用户不存在)
     */
    @DisplayName("TC-PU02: 用户不存在登录失败路径测试")
    @Test
    public void testLoginFailurePathUserNotExist() {
        // 准备数据 - 不存在的用户
        String nonExistingUsername = "nonexistinguser";
        String password = "anypassword";
        
        // 执行测试
        User loginUser = userService.login(nonExistingUsername, password);
        
        // 验证结果
        assertNull(loginUser, "不存在用户应登录失败");
    }
    
    /**
     * 测试用例: TC-PU03 登录失败路径(密码错误)
     * 白盒测试: 路径测试 - 登录失败路径(密码错误)
     */
    @DisplayName("TC-PU03: 密码错误登录失败路径测试")
    @Test
    public void testLoginFailurePathWrongPassword() {
        // 准备数据 - 正确用户名、错误密码
        String username = normalUser.getUsername();
        String wrongPassword = "wrongpassword";
        
        // 执行测试
        User loginUser = userService.login(username, wrongPassword);
        
        // 验证结果
        assertNull(loginUser, "密码错误应登录失败");
    }
} 