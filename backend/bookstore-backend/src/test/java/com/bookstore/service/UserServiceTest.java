package com.bookstore.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bookstore.entity.User;
import com.bookstore.mapper.UserMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ==================== 用户界面与登录注册模块测试（刘思磊） ====================
 * 本类先覆盖白盒测试用例，后覆盖黑盒测试用例。
 * 每个测试方法前有详细注释，DisplayName与文档编号和用例名称一致。
 */
@SpringBootTest
@Transactional
@DisplayName("用户界面与登录注册模块测试")
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    private String existUsername = "existUser";
    private String existPassword = "existPass123";
    private Long existUserId;

    @BeforeEach
    public void setUp() {
        // 创建已存在用户
        User user = new User();
        user.setUsername(existUsername);
        user.setPassword(existPassword);
        user.setStatus(1);
        userMapper.insert(user);
        existUserId = user.getId();
    }

    @AfterEach
    public void tearDown() {
        userMapper.delete(null); // 清空所有用户
    }

    // ==================== 白盒测试（语句/判定/条件/路径/循环） ====================

    /** TCU-SC01: 用户名已存在 */
    @DisplayName("TCU-SC01: 用户名已存在")
    @Test
    public void testRegister_TCU_SC01_usernameExists() {
        User user = new User();
        user.setUsername(existUsername);
        user.setPassword("anyPass");
        boolean result = userService.register(user);
        assertFalse(result, "用户名已存在时注册应失败");
    }

    /** TCU-SC02: 状态为null */
    @DisplayName("TCU-SC02: 状态为null")
    @Test
    public void testRegister_TCU_SC02_statusNull() {
        User user = new User();
        user.setUsername("newUser1");
        user.setPassword("pass1234");
        user.setStatus(null);
        boolean result = userService.register(user);
        assertTrue(result);
        User dbUser = userMapper.selectById(user.getId());
        assertNotNull(dbUser);
        assertEquals(1, dbUser.getStatus());
    }

    /** TCU-SC03: 状态非null */
    @DisplayName("TCU-SC03: 状态非null")
    @Test
    public void testRegister_TCU_SC03_statusNotNull() {
        User user = new User();
        user.setUsername("newUser2");
        user.setPassword("pass1234");
        user.setStatus(2);
        boolean result = userService.register(user);
        assertTrue(result);
        User dbUser = userMapper.selectById(user.getId());
        assertNotNull(dbUser);
        assertEquals(2, dbUser.getStatus());
    }

    // 判定覆盖
    /** TCU-DC01: checkUsernameExists(user.getUsername()) 判定为true */
    @DisplayName("TCU-DC01: checkUsernameExists(user.getUsername()) 判定为true")
    @Test
    public void testRegister_TCU_DC01_usernameExists() {
        User user = new User();
        user.setUsername(existUsername);
        user.setPassword("anyPass");
        boolean result = userService.register(user);
        assertFalse(result);
    }

    /** TCU-DC02: checkUsernameExists(user.getUsername()) 判定为false */
    @DisplayName("TCU-DC02: checkUsernameExists(user.getUsername()) 判定为false")
    @Test
    public void testRegister_TCU_DC02_usernameNotExists() {
        User user = new User();
        user.setUsername("uniqueUser");
        user.setPassword("pass1234");
        boolean result = userService.register(user);
        assertTrue(result);
    }

    /** TCU-DC03: user.getStatus()==null 判定为true */
    @DisplayName("TCU-DC03: user.getStatus()==null 判定为true")
    @Test
    public void testRegister_TCU_DC03_statusNull() {
        User user = new User();
        user.setUsername("uniqueUser2");
        user.setPassword("pass1234");
        user.setStatus(null);
        boolean result = userService.register(user);
        assertTrue(result);
        User dbUser = userMapper.selectById(user.getId());
        assertEquals(1, dbUser.getStatus());
    }

    /** TCU-DC04: user.getStatus()==null 判定为false */
    @DisplayName("TCU-DC04: user.getStatus()==null 判定为false")
    @Test
    public void testRegister_TCU_DC04_statusNotNull() {
        User user = new User();
        user.setUsername("uniqueUser3");
        user.setPassword("pass1234");
        user.setStatus(2);
        boolean result = userService.register(user);
        assertTrue(result);
        User dbUser = userMapper.selectById(user.getId());
        assertEquals(2, dbUser.getStatus());
    }

    // 条件覆盖
    /** TCU-CC01: checkUsernameExists(user.getUsername()) 条件为true */
    @DisplayName("TCU-CC01: checkUsernameExists(user.getUsername()) 条件为true")
    @Test
    public void testRegister_TCU_CC01_usernameExists() {
        User user = new User();
        user.setUsername(existUsername);
        user.setPassword("anyPass");
        boolean result = userService.register(user);
        assertFalse(result);
    }

    /** TCU-CC02: checkUsernameExists(user.getUsername()) 条件为false */
    @DisplayName("TCU-CC02: checkUsernameExists(user.getUsername()) 条件为false")
    @Test
    public void testRegister_TCU_CC02_usernameNotExists() {
        User user = new User();
        user.setUsername("uniqueUser4");
        user.setPassword("pass1234");
        boolean result = userService.register(user);
        assertTrue(result);
    }

    /** TCU-CC03: user.getStatus()==null 条件为true */
    @DisplayName("TCU-CC03: user.getStatus()==null 条件为true")
    @Test
    public void testRegister_TCU_CC03_statusNull() {
        User user = new User();
        user.setUsername("uniqueUser5");
        user.setPassword("pass1234");
        user.setStatus(null);
        boolean result = userService.register(user);
        assertTrue(result);
        User dbUser = userMapper.selectById(user.getId());
        assertEquals(1, dbUser.getStatus());
    }

    /** TCU-CC04: user.getStatus()==null 条件为false */
    @DisplayName("TCU-CC04: user.getStatus()==null 条件为false")
    @Test
    public void testRegister_TCU_CC04_statusNotNull() {
        User user = new User();
        user.setUsername("uniqueUser6");
        user.setPassword("pass1234");
        user.setStatus(2);
        boolean result = userService.register(user);
        assertTrue(result);
        User dbUser = userMapper.selectById(user.getId());
        assertEquals(2, dbUser.getStatus());
    }

    // 判定/条件组合覆盖
    /** TCU-MCC01: checkUsernameExists()=true */
    @DisplayName("TCU-MCC01: checkUsernameExists()=true")
    @Test
    public void testRegister_TCU_MCC01_usernameExists() {
        User user = new User();
        user.setUsername(existUsername);
        user.setPassword("anyPass");
        boolean result = userService.register(user);
        assertFalse(result);
    }

    /** TCU-MCC02: checkUsernameExists()=false, status=null */
    @DisplayName("TCU-MCC02: checkUsernameExists()=false, status=null")
    @Test
    public void testRegister_TCU_MCC02_statusNull() {
        User user = new User();
        user.setUsername("uniqueUser7");
        user.setPassword("pass1234");
        user.setStatus(null);
        boolean result = userService.register(user);
        assertTrue(result);
        User dbUser = userMapper.selectById(user.getId());
        assertEquals(1, dbUser.getStatus());
    }

    /** TCU-MCC03: checkUsernameExists()=false, status非null */
    @DisplayName("TCU-MCC03: checkUsernameExists()=false, status非null")
    @Test
    public void testRegister_TCU_MCC03_statusNotNull() {
        User user = new User();
        user.setUsername("uniqueUser8");
        user.setPassword("pass1234");
        user.setStatus(2);
        boolean result = userService.register(user);
        assertTrue(result);
        User dbUser = userMapper.selectById(user.getId());
        assertEquals(2, dbUser.getStatus());
    }

    // ==================== 路径覆盖
    /** TCU-PC01: 用户名已存在路径 */
    @DisplayName("TCU-PC01: 用户名已存在路径")
    @Test
    public void testRegister_TCU_PC01_usernameExistsPath() {
        User user = new User();
        user.setUsername(existUsername);
        user.setPassword("anyPass");
        boolean result = userService.register(user);
        assertFalse(result);
    }

    /** TCU-PC02: 新用户，status为null路径 */
    @DisplayName("TCU-PC02: 新用户，status为null路径")
    @Test
    public void testRegister_TCU_PC02_statusNullPath() {
        User user = new User();
        user.setUsername("uniqueUser9");
        user.setPassword("pass1234");
        user.setStatus(null);
        boolean result = userService.register(user);
        assertTrue(result);
        User dbUser = userMapper.selectById(user.getId());
        assertEquals(1, dbUser.getStatus());
    }

    /** TCU-PC03: 新用户，status已设置路径 */
    @DisplayName("TCU-PC03: 新用户，status已设置路径")
    @Test
    public void testRegister_TCU_PC03_statusSetPath() {
        User user = new User();
        user.setUsername("uniqueUser10");
        user.setPassword("pass1234");
        user.setStatus(2);
        boolean result = userService.register(user);
        assertTrue(result);
        User dbUser = userMapper.selectById(user.getId());
        assertEquals(2, dbUser.getStatus());
    }

    // ==================== 黑盒测试（等价类划分/边界值分析） ====================

    /** TC-U01: 注册成功 */
    @DisplayName("TC-U01: 注册成功")
    @Test
    public void testRegister_TC_U01_success() {
        User user = new User();
        user.setUsername("user01");
        user.setPassword("passwd123");
        boolean result = userService.register(user);
        assertTrue(result);
        assertNotNull(user.getId());
    }

    /** TC-U02: 用户名过短 */
    @DisplayName("TC-U02: 用户名过短")
    @Test
    public void testRegister_TC_U02_usernameTooShort() {
        User user = new User();
        user.setUsername("usr");
        user.setPassword("passwd123");
        boolean result = userService.register(user);
        assertFalse(result);
    }

    /** TC-U03: 用户名过长 */
    @DisplayName("TC-U03: 用户名过长")
    @Test
    public void testRegister_TC_U03_usernameTooLong() {
        User user = new User();
        user.setUsername("user01234567890123456789"); // 21字符
        user.setPassword("passwd123");
        boolean result = userService.register(user);
        assertFalse(result);
    }

    /** TC-U04: 用户名已存在 */
    @DisplayName("TC-U04: 用户名已存在")
    @Test
    public void testRegister_TC_U04_usernameExists() {
        User user = new User();
        user.setUsername(existUsername);
        user.setPassword("passwd123");
        boolean result = userService.register(user);
        assertFalse(result);
    }

    /** TC-U05: 密码过短 */
    @DisplayName("TC-U05: 密码过短")
    @Test
    public void testRegister_TC_U05_passwordTooShort() {
        User user = new User();
        user.setUsername("user02");
        user.setPassword("123");
        boolean result = userService.register(user);
        assertFalse(result);
    }

    /** TC-U06: 密码过长 */
    @DisplayName("TC-U06: 密码过长")
    @Test
    public void testRegister_TC_U06_passwordTooLong() {
        User user = new User();
        user.setUsername("user03");
        user.setPassword("123456789012345678901"); // 21字符
        boolean result = userService.register(user);
        assertFalse(result);
    }

    /** TC-U07: 登录成功 */
    @DisplayName("TC-U07: 登录成功")
    @Test
    public void testLogin_TC_U07_success() {
        User user = new User();
        user.setUsername("loginUser");
        user.setPassword("passwd123");
        userService.register(user);
        User loginUser = userService.login("loginUser", "passwd123");
        assertNotNull(loginUser);
        assertEquals("loginUser", loginUser.getUsername());
    }

    /** TC-U08: 登录用户名不存在 */
    @DisplayName("TC-U08: 登录用户名不存在")
    @Test
    public void testLogin_TC_U08_usernameNotExist() {
        User loginUser = userService.login("nouser", "passwd123");
        assertNull(loginUser);
    }

    /** TC-U09: 登录密码错误 */
    @DisplayName("TC-U09: 登录密码错误")
    @Test
    public void testLogin_TC_U09_passwordWrong() {
        User user = new User();
        user.setUsername("loginUser2");
        user.setPassword("passwd123");
        userService.register(user);
        User loginUser = userService.login("loginUser2", "wrongpass");
        assertNull(loginUser);
    }
} 