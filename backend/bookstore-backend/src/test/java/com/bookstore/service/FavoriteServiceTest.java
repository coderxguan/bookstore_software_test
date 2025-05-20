package com.bookstore.service;

import com.bookstore.entity.Book;
import com.bookstore.entity.Favorite;
import com.bookstore.entity.User;
import com.bookstore.mapper.BookMapper;
import com.bookstore.mapper.FavoriteMapper;
import com.bookstore.mapper.UserMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ==================== 收藏管理模块测试（吴豪） ====================
 * 本类先覆盖白盒测试用例，后覆盖黑盒测试用例。
 * 每个测试方法前有详细注释，DisplayName与文档编号和用例名称一致。
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

    private Long userId;
    private Long bookId;
    private Book testBook;
    private User testUser;

    @BeforeEach
    public void setUp() {
        // 创建测试用户
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("password");
        userMapper.insert(user);
        userId = user.getId();
        testUser = user;

        // 创建测试图书
        testBook = new Book();
        testBook.setName("测试图书");
        testBook.setAuthor("测试作者");
        testBook.setCategory("测试分类");
        testBook.setPrice(new BigDecimal("50.00"));
        testBook.setDescription("测试描述");
        testBook.setFavoriteCount(10);
        bookMapper.insert(testBook);
        bookId = testBook.getId();
    }

    @AfterEach
    public void tearDown() {
        if (testBook != null && testBook.getId() != null) {
            bookMapper.deleteById(testBook.getId());
        }
        if (testUser != null && testUser.getId() != null) {
            userMapper.deleteById(testUser.getId());
        }
        favoriteMapper.delete(null); // 清空所有收藏
    }

    // ==================== 白盒测试（语句/判定/条件/路径） ====================

    /** SF01: userId为null，返回false */
    @Test
    @DisplayName("SF01: userId为null，返回false")
    public void testAddFavorite_SF01_userIdNull() {
        assertFalse(favoriteService.addFavorite(null, bookId));
    }

    /** SF02: bookId为null，返回false */
    @Test
    @DisplayName("SF02: bookId为null，返回false")
    public void testAddFavorite_SF02_bookIdNull() {
        assertFalse(favoriteService.addFavorite(userId, null));
    }

    /** SF03: 已收藏，返回false */
    @Test
    @DisplayName("SF03: 已收藏，返回false")
    public void testAddFavorite_SF03_alreadyFavorited() {
        favoriteService.addFavorite(userId, bookId);
        assertFalse(favoriteService.addFavorite(userId, bookId));
    }

    /** SF04: 正常收藏，返回true */
    @Test
    @DisplayName("SF04: 正常收藏，返回true")
    public void testAddFavorite_SF04_normal() {
        assertTrue(favoriteService.addFavorite(userId, bookId));
    }



    // ==================== 黑盒测试（等价类划分/边界值分析） ====================

    /** TC-F01: 正常收藏 */
    @DisplayName("TC-F01: 正常收藏")
    @Test
    public void testAddFavorite_TC_F01_normal() {
        boolean result = favoriteService.addFavorite(userId, bookId);
        assertTrue(result);
        assertTrue(favoriteService.isFavorite(userId, bookId));
    }

    /** TC-F02: 用户ID无效 */
    @DisplayName("TC-F02: 用户ID无效")
    @Test
    public void testAddFavorite_TC_F02_invalidUserId() {
        boolean result = favoriteService.addFavorite(-1L, bookId);
        assertFalse(result);
    }

    /** TC-F03: 图书ID无效 */
    @DisplayName("TC-F03: 图书ID无效")
    @Test
    public void testAddFavorite_TC_F03_invalidBookId() {
        boolean result = favoriteService.addFavorite(userId, -1L);
        assertFalse(result);
    }

    /** TC-F04: 已收藏重复收藏 */
    @DisplayName("TC-F04: 已收藏重复收藏")
    @Test
    public void testAddFavorite_TC_F04_alreadyFavorited() {
        favoriteService.addFavorite(userId, bookId);
        boolean result = favoriteService.addFavorite(userId, bookId);
        assertFalse(result);
    }

    /** TC-F05: 正常取消收藏 */
    @DisplayName("TC-F05: 正常取消收藏")
    @Test
    public void testRemoveFavorite_TC_F05_normal() {
        favoriteService.addFavorite(userId, bookId);
        boolean result = favoriteService.removeFavorite(userId, bookId);
        assertTrue(result);
        assertFalse(favoriteService.isFavorite(userId, bookId));
    }

    /** TC-F06: 未收藏取消收藏 */
    @DisplayName("TC-F06: 未收藏取消收藏")
    @Test
    public void testRemoveFavorite_TC_F06_notFavorited() {
        boolean result = favoriteService.removeFavorite(userId, bookId);
        assertFalse(result);
    }
} 