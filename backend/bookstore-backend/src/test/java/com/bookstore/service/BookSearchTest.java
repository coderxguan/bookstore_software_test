package com.bookstore.service;

import com.bookstore.entity.Book;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ==================== 图书搜索模块测试（管海峰） ====================
 * 本类先覆盖白盒测试用例，后覆盖黑盒测试用例。
 * 每个测试方法前有详细注释，DisplayName与文档编号和用例名称一致。
 */
@SpringBootTest
@Transactional
@DisplayName("图书搜索模块测试")
public class BookSearchTest {

    @Autowired
    private BookService bookService;

    private List<Book> testBooks;

    @BeforeEach
    public void setUp() {
        testBooks = new ArrayList<>();
        // 初始化测试数据
        Book book1 = new Book();
        book1.setName("Java编程思想");
        book1.setAuthor("Bruce Eckel");
        book1.setCategory("编程");
        book1.setPrice(new BigDecimal("99.00"));
        book1.setDescription("Java经典教程");
        book1.setFavoriteCount(100);
        bookService.save(book1);

        Book book2 = new Book();
        book2.setName("Effective Java");
        book2.setAuthor("Joshua Bloch");
        book2.setCategory("编程");
        book2.setPrice(new BigDecimal("89.00"));
        book2.setDescription("Java进阶书籍");
        book2.setFavoriteCount(80);
        bookService.save(book2);

        Book book3 = new Book();
        book3.setName("Python入门");
        book3.setAuthor("Bruce Lee");
        book3.setCategory("编程");
        book3.setPrice(new BigDecimal("79.00"));
        book3.setDescription("Python基础教程");
        book3.setFavoriteCount(60);
        bookService.save(book3);

        Book book4 = new Book();
        book4.setName("JavaScript高级程序设计");
        book4.setAuthor("Nicholas C. Zakas");
        book4.setCategory("编程");
        book4.setPrice(new BigDecimal("109.00"));
        book4.setDescription("JavaScript权威指南");
        book4.setFavoriteCount(90);
        bookService.save(book4);

        testBooks.add(book1);
        testBooks.add(book2);
        testBooks.add(book3);
        testBooks.add(book4);
    }

    @AfterEach
    public void tearDown() {
        for (Book book : testBooks) {
            if (book.getId() != null) {
                bookService.removeById(book.getId());
            }
        }
        testBooks = null;
    }

    // ==================== 白盒测试（语句/判定/条件/路径） ====================

    /**
     * SS01: bookId为null，返回false
     */
    @Test
    @DisplayName("SS01: bookId为null，返回false")
    public void testDecrementFavoriteCount_SS01_bookIdNull() {
        assertFalse(bookService.decrementFavoriteCount(null));
    }

    /**
     * SS02: bookId<=0，返回false
     */
    @Test
    @DisplayName("SS02: bookId<=0，返回false")
    public void testDecrementFavoriteCount_SS02_bookIdLEZero() {
        assertFalse(bookService.decrementFavoriteCount(0L));
        assertFalse(bookService.decrementFavoriteCount(-1L));
    }

    /**
     * SS03: 图书不存在，返回false
     */
    @Test
    @DisplayName("SS03: 图书不存在，返回false")
    public void testDecrementFavoriteCount_SS03_bookNotExist() {
        assertFalse(bookService.decrementFavoriteCount(99999L));
    }

    /**
     * SS04: 收藏数为null，设置为0并返回true
     */
    @Test
    @DisplayName("SS04: 收藏数为null，设置为0并返回true")
    public void testDecrementFavoriteCount_SS04_favoriteCountNull() {
        Book book = new Book();
        book.setName("收藏数为null");
        book.setAuthor("作者");
        book.setCategory("测试");
        book.setPrice(new BigDecimal("10.0"));
        book.setFavoriteCount(null);
        bookService.save(book);
        testBooks.add(book);
        assertTrue(bookService.decrementFavoriteCount(book.getId()));
        Book updated = bookService.getById(book.getId());
        assertEquals(0, updated.getFavoriteCount());
    }

    /**
     * SS05: 收藏数大于0，减1并返回true
     */
    @Test
    @DisplayName("SS05: 收藏数大于0，减1并返回true")
    public void testDecrementFavoriteCount_SS05_favoriteCountGT0() {
        Book book = new Book();
        book.setName("收藏数大于0");
        book.setAuthor("作者");
        book.setCategory("测试");
        book.setPrice(new BigDecimal("10.0"));
        book.setFavoriteCount(5);
        bookService.save(book);
        testBooks.add(book);
        assertTrue(bookService.decrementFavoriteCount(book.getId()));
        Book updated = bookService.getById(book.getId());
        assertEquals(4, updated.getFavoriteCount());
    }

    /**
     * SS06: 收藏数为0，不变并返回true
     */
    @Test
    @DisplayName("SS06: 收藏数为0，不变并返回true")
    public void testDecrementFavoriteCount_SS06_favoriteCountZero() {
        Book book = new Book();
        book.setName("收藏数为0");
        book.setAuthor("作者");
        book.setCategory("测试");
        book.setPrice(new BigDecimal("10.0"));
        book.setFavoriteCount(0);
        bookService.save(book);
        testBooks.add(book);
        assertTrue(bookService.decrementFavoriteCount(book.getId()));
        Book updated = bookService.getById(book.getId());
        assertEquals(0, updated.getFavoriteCount());
    }

    // ==================== 黑盒测试（等价类划分/边界值分析） ====================

    /** TC-S01: 标准有效输入 */
    @DisplayName("TC-S01: 标准有效输入")
    @Test
    public void testSearchBooks_TC_S01_validInput() {
        List<Book> results = bookService.listAllBooks("Java");
        assertNotNull(results);
        assertTrue(results.stream().anyMatch(b -> b.getName().contains("Java")), "应返回包含Java的图书");
    }

    /** TC-S02: 标题关键词超长 */
    @DisplayName("TC-S02: 标题关键词超长")
    @Test
    public void testSearchBooks_TC_S02_titleKeywordTooLong() {
        String longTitle = "A".repeat(51);
        Exception e = assertThrows(Exception.class, () -> bookService.listAllBooks(longTitle));
        assertTrue(e.getMessage().contains("标题关键词过长"));
    }

    /** TC-S03: 作者关键词超长 */
    @DisplayName("TC-S03: 作者关键词超长")
    @Test
    public void testSearchBooks_TC_S03_authorKeywordTooLong() {
        String longAuthor = "A".repeat(31);
        Exception e = assertThrows(Exception.class, () -> bookService.listAllBooksAndSortByFavoriteCount("", longAuthor));
        assertTrue(e.getMessage().contains("作者关键词过长"));
    }

    /** TC-S04: 排序类型非法 */
    @DisplayName("TC-S04: 排序类型非法")
    @Test
    public void testSearchBooks_TC_S04_sortTypeInvalid() {
        Exception e = assertThrows(Exception.class, () -> bookService.listAllBooksAndSortByFavoriteCount("Java", "up"));
        assertTrue(e.getMessage().contains("排序类型非法"));
    }

    /** TC-S05: 仅标题关键词有效 */
    @DisplayName("TC-S05: 仅标题关键词有效")
    @Test
    public void testSearchBooks_TC_S05_titleOnly() {
        List<Book> results = bookService.listAllBooks("Python");
        assertNotNull(results);
        assertTrue(results.stream().anyMatch(b -> b.getName().contains("Python")), "应返回包含Python的图书");
    }

    /** TC-S06: 仅作者关键词有效 */
    @DisplayName("TC-S06: 仅作者关键词有效")
    @Test
    public void testSearchBooks_TC_S06_authorOnly() {
        List<Book> results = bookService.listAllBooksAndSortByFavoriteCount("", "Bruce");
        assertNotNull(results);
        assertTrue(results.stream().anyMatch(b -> b.getAuthor().contains("Bruce")), "应返回作者为Bruce的图书");
    }

    /** TC-S07: 仅排序类型为空 */
    @DisplayName("TC-S07: 仅排序类型为空")
    @Test
    public void testSearchBooks_TC_S07_sortTypeEmpty() {
        List<Book> results = bookService.listAllBooksAndSortByFavoriteCount("", "");
        assertNotNull(results);
        assertTrue(results.size() >= 4, "应返回所有图书");
    }
} 