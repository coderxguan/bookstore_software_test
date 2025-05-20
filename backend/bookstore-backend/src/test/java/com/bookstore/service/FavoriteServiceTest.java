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
    private Long nonExistingBookId = 99999L;
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

    // ==================== 白盒测试（语句/判定/条件/路径/循环） ====================

    /** TCF-SC01: 无效用户ID */
    @DisplayName("TCF-SC01: 无效用户ID")
    @Test
    public void testAddFavorite_TCF_SC01_invalidUserId() {
        boolean result = favoriteService.addFavorite(null, bookId);
        assertFalse(result, "用户ID为null时应返回false");
    }

    /** TCF-SC02: 无效图书ID */
    @DisplayName("TCF-SC02: 无效图书ID")
    @Test
    public void testAddFavorite_TCF_SC02_invalidBookId() {
        boolean result = favoriteService.addFavorite(userId, null);
        assertFalse(result, "图书ID为null时应返回false");
    }

    /** TCF-SC03: 已收藏图书 */
    @DisplayName("TCF-SC03: 已收藏图书")
    @Test
    public void testAddFavorite_TCF_SC03_alreadyFavorited() {
        favoriteService.addFavorite(userId, bookId);
        boolean result = favoriteService.addFavorite(userId, bookId);
        assertFalse(result, "已收藏图书再次收藏应返回false");
    }

    /** TCF-SC04: 正常收藏 */
    @DisplayName("TCF-SC04: 正常收藏")
    @Test
    public void testAddFavorite_TCF_SC04_normal() {
        boolean result = favoriteService.addFavorite(userId, bookId);
        assertTrue(result, "正常收藏应返回true");
        assertTrue(favoriteService.isFavorite(userId, bookId), "应已收藏");
    }

    // 判定覆盖
    /** TCF-DC01: userId==null || bookId==null 判定为true */
    @DisplayName("TCF-DC01: userId==null || bookId==null 判定为true")
    @Test
    public void testAddFavorite_TCF_DC01_userOrBookIdNull() {
        assertFalse(favoriteService.addFavorite(null, bookId));
        assertFalse(favoriteService.addFavorite(userId, null));
    }

    /** TCF-DC02: userId==null || bookId==null 判定为false */
    @DisplayName("TCF-DC02: userId==null || bookId==null 判定为false")
    @Test
    public void testAddFavorite_TCF_DC02_userAndBookIdValid() {
        boolean result = favoriteService.addFavorite(userId, bookId);
        assertTrue(result);
    }

    /** TCF-DC03: isAlreadyFavorite(userId, bookId) 判定为true */
    @DisplayName("TCF-DC03: isAlreadyFavorite(userId, bookId) 判定为true")
    @Test
    public void testAddFavorite_TCF_DC03_alreadyFavoriteTrue() {
        favoriteService.addFavorite(userId, bookId);
        boolean result = favoriteService.addFavorite(userId, bookId);
        assertFalse(result);
    }

    /** TCF-DC04: isAlreadyFavorite(userId, bookId) 判定为false */
    @DisplayName("TCF-DC04: isAlreadyFavorite(userId, bookId) 判定为false")
    @Test
    public void testAddFavorite_TCF_DC04_alreadyFavoriteFalse() {
        boolean result = favoriteService.addFavorite(userId, bookId);
        assertTrue(result);
    }

    // 条件覆盖
    /** TCF-CC01: userId==null 条件为true */
    @DisplayName("TCF-CC01: userId==null 条件为true")
    @Test
    public void testAddFavorite_TCF_CC01_userIdNull() {
        assertFalse(favoriteService.addFavorite(null, bookId));
    }

    /** TCF-CC02: userId==null 条件为false */
    @DisplayName("TCF-CC02: userId==null 条件为false")
    @Test
    public void testAddFavorite_TCF_CC02_userIdNotNull() {
        boolean result = favoriteService.addFavorite(userId, bookId);
        assertTrue(result);
    }

    /** TCF-CC03: bookId==null 条件为true */
    @DisplayName("TCF-CC03: bookId==null 条件为true")
    @Test
    public void testAddFavorite_TCF_CC03_bookIdNull() {
        assertFalse(favoriteService.addFavorite(userId, null));
    }

    /** TCF-CC04: bookId==null 条件为false */
    @DisplayName("TCF-CC04: bookId==null 条件为false")
    @Test
    public void testAddFavorite_TCF_CC04_bookIdNotNull() {
        boolean result = favoriteService.addFavorite(userId, bookId);
        assertTrue(result);
    }

    // 路径覆盖
    /** TCF-PC01: 参数验证失败路径 */
    @DisplayName("TCF-PC01: 参数验证失败路径")
    @Test
    public void testAddFavorite_TCF_PC01_paramInvalidPath() {
        assertFalse(favoriteService.addFavorite(null, null));
        assertFalse(favoriteService.addFavorite(null, bookId));
        assertFalse(favoriteService.addFavorite(userId, null));
    }

    /** TCF-PC02: 已收藏路径 */
    @DisplayName("TCF-PC02: 已收藏路径")
    @Test
    public void testAddFavorite_TCF_PC02_alreadyFavoritedPath() {
        favoriteService.addFavorite(userId, bookId);
        boolean result = favoriteService.addFavorite(userId, bookId);
        assertFalse(result);
    }

    /** TCF-PC03: 正常收藏路径 */
    @DisplayName("TCF-PC03: 正常收藏路径")
    @Test
    public void testAddFavorite_TCF_PC03_normalPath() {
        boolean result = favoriteService.addFavorite(userId, bookId);
        assertTrue(result);
    }

    // ==================== 循环结构 getAllUserFavorites ====================

    /** TCF-LT01: 用户ID为null */
    @DisplayName("TCF-LT01: 用户ID为null")
    @Test
    public void testGetAllUserFavorites_TCF_LT01_userIdNull() {
        List<Book> result = favoriteService.getAllUserFavorites(null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /** TCF-LT02: 用户无收藏 */
    @DisplayName("TCF-LT02: 用户无收藏")
    @Test
    public void testGetAllUserFavorites_TCF_LT02_noFavorites() {
        List<Book> result = favoriteService.getAllUserFavorites(userId);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /** TCF-LT03: 单本收藏 */
    @DisplayName("TCF-LT03: 单本收藏")
    @Test
    public void testGetAllUserFavorites_TCF_LT03_singleFavorite() {
        favoriteService.addFavorite(userId, bookId);
        List<Book> result = favoriteService.getAllUserFavorites(userId);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(bookId, result.get(0).getId());
    }

    /** TCF-LT04: 多本收藏顺序一致 */
    @DisplayName("TCF-LT04: 多本收藏顺序一致")
    @Test
    public void testGetAllUserFavorites_TCF_LT04_multiFavoriteOrder() {
        Book book2 = new Book();
        book2.setName("第二本书");
        book2.setAuthor("作者2");
        book2.setCategory("分类2");
        book2.setPrice(new BigDecimal("20.0"));
        book2.setDescription("描述2");
        bookMapper.insert(book2);
        favoriteService.addFavorite(userId, bookId);
        favoriteService.addFavorite(userId, book2.getId());
        List<Book> result = favoriteService.getAllUserFavorites(userId);
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(book2.getId(), result.get(0).getId()); // 按收藏时间倒序
        assertEquals(bookId, result.get(1).getId());
        bookMapper.deleteById(book2.getId());
    }

    /** TCF-LT05: 收藏的图书被删除后 */
    @DisplayName("TCF-LT05: 收藏的图书被删除后")
    @Test
    public void testGetAllUserFavorites_TCF_LT05_favoriteBookDeleted() {
        favoriteService.addFavorite(userId, bookId);
        bookMapper.deleteById(bookId);
        List<Book> result = favoriteService.getAllUserFavorites(userId);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /** TCF-LT06: 收藏列表包含无效ID */
    @DisplayName("TCF-LT06: 收藏列表包含无效ID")
    @Test
    public void testGetAllUserFavorites_TCF_LT06_invalidBookId() {
        favoriteService.addFavorite(userId, 99999L); // 不存在的图书ID
        List<Book> result = favoriteService.getAllUserFavorites(userId);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /** TCF-LT07: 性能测试-大量收藏 */
    @DisplayName("TCF-LT07: 性能测试-大量收藏")
    @Test
    public void testGetAllUserFavorites_TCF_LT07_performance() {
        List<Long> bookIds = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            Book book = new Book();
            book.setName("批量书" + i);
            book.setAuthor("作者" + i);
            book.setCategory("分类");
            book.setPrice(new BigDecimal("10.0"));
            book.setDescription("描述");
            bookMapper.insert(book);
            favoriteService.addFavorite(userId, book.getId());
            bookIds.add(book.getId());
        }
        long start = System.currentTimeMillis();
        List<Book> result = favoriteService.getAllUserFavorites(userId);
        long duration = System.currentTimeMillis() - start;
        assertEquals(50, result.size());
        assertTrue(duration < 2000, "应在2秒内完成");
        for (Long id : bookIds) {
            bookMapper.deleteById(id);
        }
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