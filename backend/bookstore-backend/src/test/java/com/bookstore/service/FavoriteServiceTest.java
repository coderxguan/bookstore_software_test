package com.bookstore.service;

import com.bookstore.entity.Book;
import com.bookstore.entity.Favorite;
import com.bookstore.entity.User;
import com.bookstore.mapper.BookMapper;
import com.bookstore.mapper.FavoriteMapper;
import com.bookstore.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 收藏管理模块测试类(丙)
 * 包含黑盒测试(状态转换测试)和白盒测试(语句覆盖、条件覆盖、路径测试)
 */
@SpringBootTest
@Transactional
@DisplayName("收藏管理模块测试")
public class FavoriteServiceTest {

    @Autowired
    private FavoriteService favoriteService;
    
    @Autowired
    private BookService bookService;
    
    @Autowired
    private FavoriteMapper favoriteMapper;
    
    @Autowired
    private BookMapper bookMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    // 测试数据
    private Long userId;
    private Long bookId;
    private Long nonExistingBookId = 999L;
    private Book testBook;
    
    @BeforeEach
    public void setUp() {
        // 创建测试用户
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("password");
        userMapper.insert(user);
        userId = user.getId();
        
        // 创建测试图书
        testBook = new Book();
        testBook.setName("测试图书");
        testBook.setAuthor("测试作者");
        testBook.setCategory("测试分类");
        testBook.setPrice(new BigDecimal("50.00"));
        testBook.setDescription("测试描述");
        testBook.setFavoriteCount(10); // 初始收藏数为10
        bookMapper.insert(testBook);
        bookId = testBook.getId();
    }
    
    /**
     * 测试用例: TC-F01 未收藏→已收藏
     * 状态转换测试: 从未收藏状态转换到已收藏状态
     */
    @DisplayName("未收藏到已收藏状态转换测试")
    @Test
    public void testAddFavoriteWhenNotFavorited() {
        // 步骤1: 确认未收藏状态
        assertFalse(favoriteService.isFavorite(userId, bookId), "开始测试前应为未收藏状态");
        
        // 步骤2: 执行添加收藏操作
        boolean result = favoriteService.addFavorite(userId, bookId);
        
        // 步骤3: 验证结果
        assertTrue(result, "添加收藏应成功");
        assertTrue(favoriteService.isFavorite(userId, bookId), "添加后应为已收藏状态");
        
        // 验证收藏计数增加
        Book book = bookService.getById(bookId);
        assertEquals(11, book.getFavoriteCount(), "收藏计数应该增加1");
    }
    
    /**
     * 测试用例: TC-F02 已收藏→未收藏
     * 状态转换测试: 从已收藏状态转换到未收藏状态
     */
    @DisplayName("已收藏到未收藏状态转换测试")
    @Test
    public void testRemoveFavoriteWhenFavorited() {
        // 步骤1: 准备已收藏状态
        favoriteService.addFavorite(userId, bookId);
        assertTrue(favoriteService.isFavorite(userId, bookId), "准备阶段应为已收藏状态");
        
        // 步骤2: 执行取消收藏操作
        boolean result = favoriteService.removeFavorite(userId, bookId);
        
        // 步骤3: 验证结果
        assertTrue(result, "取消收藏应成功");
        assertFalse(favoriteService.isFavorite(userId, bookId), "取消后应为未收藏状态");
        
        // 验证收藏计数减少
        Book book = bookService.getById(bookId);
        assertEquals(10, book.getFavoriteCount(), "收藏计数应该恢复原值");
    }
    
    /**
     * 测试用例: TC-F03 已收藏→已收藏(无效转换)
     * 状态转换测试: 在已收藏状态下再次添加收藏(无效转换)
     */
    @DisplayName("已收藏到已收藏无效转换测试")
    @Test
    public void testAddFavoriteWhenAlreadyFavorited() {
        // 步骤1: 准备已收藏状态
        favoriteService.addFavorite(userId, bookId);
        assertTrue(favoriteService.isFavorite(userId, bookId), "准备阶段应为已收藏状态");
        
        // 步骤2: 执行添加收藏操作
        boolean result = favoriteService.addFavorite(userId, bookId);
        
        // 步骤3: 验证结果
        assertFalse(result, "已收藏状态下再次添加应失败");
        
        // 验证收藏计数不变
        Book book = bookService.getById(bookId);
        assertEquals(11, book.getFavoriteCount(), "收藏计数不应变化");
    }
    
    /**
     * 测试用例: TC-F04 添加收藏后计数增加
     * 收藏计数功能测试: 验证添加收藏后图书收藏计数增加
     */
    @DisplayName("添加收藏后计数增加测试")
    @Test
    public void testFavoriteCountIncrementAfterAdd() {
        // 记录原始收藏计数
        int originalCount = testBook.getFavoriteCount();
        
        // 执行添加收藏操作
        boolean result = favoriteService.addFavorite(userId, bookId);
        
        // 验证结果
        assertTrue(result, "添加收藏应成功");
        
        // 验证收藏计数增加
        Book updatedBook = bookService.getById(bookId);
        assertEquals(originalCount + 1, updatedBook.getFavoriteCount(), "收藏计数应该增加1");
    }
    
    /**
     * 测试用例: TC-F05 取消收藏后计数减少
     * 收藏计数功能测试: 验证取消收藏后图书收藏计数减少
     */
    @DisplayName("取消收藏后计数减少测试")
    @Test
    public void testFavoriteCountDecrementAfterRemove() {
        // 准备已收藏状态
        favoriteService.addFavorite(userId, bookId);
        
        // 记录收藏后的计数
        Book bookAfterAdd = bookService.getById(bookId);
        int countAfterAdd = bookAfterAdd.getFavoriteCount();
        
        // 执行取消收藏操作
        boolean result = favoriteService.removeFavorite(userId, bookId);
        
        // 验证结果
        assertTrue(result, "取消收藏应成功");
        
        // 验证收藏计数减少
        Book bookAfterRemove = bookService.getById(bookId);
        assertEquals(countAfterAdd - 1, bookAfterRemove.getFavoriteCount(), "收藏计数应该减少1");
    }
    
    /**
     * 测试用例: TC-F06 多用户收藏同一图书
     * 收藏计数功能测试: 验证多个用户收藏同一图书时收藏计数累加
     */
    @DisplayName("多用户收藏同一图书测试")
    @Test
    public void testMultipleUsersFavorite() {
        // 创建第二个测试用户
        User user2 = new User();
        user2.setUsername("testUser2");
        user2.setPassword("password");
        userMapper.insert(user2);
        Long userId2 = user2.getId();
        
        // 记录原始收藏计数
        int originalCount = testBook.getFavoriteCount();
        
        // 用户1收藏
        boolean result1 = favoriteService.addFavorite(userId, bookId);
        
        // 用户2收藏
        boolean result2 = favoriteService.addFavorite(userId2, bookId);
        
        // 验证结果
        assertTrue(result1, "用户1添加收藏应成功");
        assertTrue(result2, "用户2添加收藏应成功");
        
        // 验证收藏计数增加两次
        Book updatedBook = bookService.getById(bookId);
        assertEquals(originalCount + 2, updatedBook.getFavoriteCount(), "收藏计数应该增加2");
    }
    
    /**
     * 测试用例: TC-WF01 添加收藏语句覆盖
     * 白盒测试: 语句覆盖 - FavoriteServiceImpl.addFavorite()
     */
    @DisplayName("添加收藏语句覆盖测试")
    @Test
    public void testAddFavoriteStatementCoverage() {
        // 执行添加收藏操作
        boolean result = favoriteService.addFavorite(userId, bookId);
        
        // 验证结果
        assertTrue(result, "添加收藏应成功");
        
        // 验证收藏记录已添加
        assertTrue(favoriteService.isFavorite(userId, bookId), "收藏记录应存在于数据库中");
        
        // 验证收藏计数已增加
        Book updatedBook = bookService.getById(bookId);
        assertEquals(11, updatedBook.getFavoriteCount(), "收藏计数应该增加1");
    }
    
    /**
     * 测试用例: TC-WF02 取消收藏语句覆盖
     * 白盒测试: 语句覆盖 - FavoriteServiceImpl.removeFavorite()
     */
    @DisplayName("取消收藏语句覆盖测试")
    @Test
    public void testRemoveFavoriteStatementCoverage() {
        // 准备已收藏状态
        favoriteService.addFavorite(userId, bookId);
        
        // 执行取消收藏操作
        boolean result = favoriteService.removeFavorite(userId, bookId);
        
        // 验证结果
        assertTrue(result, "取消收藏应成功");
        
        // 验证收藏记录已删除
        assertFalse(favoriteService.isFavorite(userId, bookId), "收藏记录应从数据库中删除");
        
        // 验证收藏计数已减少
        Book updatedBook = bookService.getById(bookId);
        assertEquals(10, updatedBook.getFavoriteCount(), "收藏计数应该减少1");
    }
    
    /**
     * 测试用例: TC-WF04 获取收藏列表语句覆盖
     * 白盒测试: 语句覆盖 - FavoriteServiceImpl.getAllUserFavorites()
     */
    @DisplayName("获取收藏列表语句覆盖测试")
    @Test
    public void testGetAllUserFavoritesStatementCoverage() {
        // 准备已收藏状态
        favoriteService.addFavorite(userId, bookId);
        
        // 执行获取收藏列表操作
        List<Book> result = favoriteService.getAllUserFavorites(userId);
        
        // 验证结果
        assertNotNull(result, "收藏列表不应为null");
        assertEquals(1, result.size(), "收藏列表应有1条记录");
        assertEquals(bookId, result.get(0).getId(), "收藏的图书ID应匹配");
    }
    
    /**
     * 测试用例: TC-CF01 收藏状态判断(已收藏)
     * 白盒测试: 条件覆盖 - favoriteExists = true 的分支
     */
    @DisplayName("收藏状态已收藏条件测试")
    @Test
    public void testFavoriteExistsConditionTrue() {
        // 准备已收藏状态
        favoriteService.addFavorite(userId, bookId);
        
        // 执行添加收藏操作
        boolean result = favoriteService.addFavorite(userId, bookId);
        
        // 验证结果
        assertFalse(result, "已收藏状态下再次添加应失败");
    }
    
    /**
     * 测试用例: TC-CF02 收藏状态判断(未收藏)
     * 白盒测试: 条件覆盖 - favoriteExists = false 的分支
     */
    @DisplayName("收藏状态未收藏条件测试")
    @Test
    public void testFavoriteExistsConditionFalse() {
        // 执行添加收藏操作
        boolean result = favoriteService.addFavorite(userId, bookId);
        
        // 验证结果
        assertTrue(result, "未收藏状态下添加应成功");
    }
    
    /**
     * 测试用例: TC-CF03 图书存在判断
     * 白盒测试: 条件覆盖 - 测试存在和不存在的图书ID
     */
    @DisplayName("图书存在条件测试")
    @Test
    public void testBookExistsCondition() {
        // 测试存在的图书
        boolean result1 = favoriteService.addFavorite(userId, bookId);
        assertTrue(result1, "存在的图书ID应收藏成功");
        
        // 测试不存在的图书
        boolean result2 = favoriteService.addFavorite(userId, nonExistingBookId);
        assertFalse(result2, "不存在的图书ID应收藏失败");
    }
    
    /**
     * 测试用例: TC-PF01 添加收藏成功路径
     * 白盒测试: 路径测试 - 测试添加收藏的成功路径
     */
    @DisplayName("添加收藏成功路径测试")
    @Test
    public void testAddFavoriteSuccessPath() {
        // 执行添加收藏操作
        boolean result = favoriteService.addFavorite(userId, bookId);
        
        // 验证结果
        assertTrue(result, "添加收藏应成功");
        assertTrue(favoriteService.isFavorite(userId, bookId), "应成功添加收藏记录");
        
        // 验证收藏计数
        Book updatedBook = bookService.getById(bookId);
        assertEquals(11, updatedBook.getFavoriteCount(), "收藏计数应增加");
    }
    
    /**
     * 测试用例: TC-PF02 取消收藏成功路径
     * 白盒测试: 路径测试 - 测试取消收藏的成功路径
     */
    @DisplayName("取消收藏成功路径测试")
    @Test
    public void testRemoveFavoriteSuccessPath() {
        // 准备已收藏状态
        favoriteService.addFavorite(userId, bookId);
        
        // 执行取消收藏操作
        boolean result = favoriteService.removeFavorite(userId, bookId);
        
        // 验证结果
        assertTrue(result, "取消收藏应成功");
        assertFalse(favoriteService.isFavorite(userId, bookId), "收藏记录应被删除");
        
        // 验证收藏计数
        Book updatedBook = bookService.getById(bookId);
        assertEquals(10, updatedBook.getFavoriteCount(), "收藏计数应减少");
    }
    
    /**
     * 测试用例: 获取空收藏列表
     * 测试：当用户没有收藏任何图书时的处理
     */
    @DisplayName("空收藏列表测试")
    @Test
    public void testGetAllUserFavoritesWithEmptyList() {
        // 执行获取收藏列表操作
        List<Book> result = favoriteService.getAllUserFavorites(userId);
        
        // 验证结果
        assertNotNull(result, "即使没有收藏，返回结果也不应为null");
        assertTrue(result.isEmpty(), "没有收藏时应返回空列表");
    }
} 