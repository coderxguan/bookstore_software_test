package com.bookstore.service;

import com.bookstore.entity.Book;
import com.bookstore.mapper.BookMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ==================== 图书管理模块测试（范仕洲） ====================
 * 负责人：范仕洲
 * 本类先覆盖白盒测试用例，后覆盖黑盒测试用例。
 * 每个测试方法前有详细注释，DisplayName与文档编号和用例名称一致。
 */
@SpringBootTest
@Transactional
@DisplayName("图书管理模块测试")
public class BookServiceTest {

    @Autowired
    private BookMapper bookMapper;
    @Autowired
    private BookService bookService;

    private List<Book> testBooks;

    @BeforeEach
    public void setUp() {
        testBooks = new ArrayList<>();
    }

    @AfterEach
    public void tearDown() {
        for (Book book : testBooks) {
            if (book.getId() != null) {
                bookMapper.deleteById(book.getId());
            }
        }
        testBooks = null;
    }

    // ==================== 白盒测试（语句/判定/条件/路径） ====================

    /**
     * SB01: id为null，返回null
     */
    @Test
    @DisplayName("SB01: id为null，返回null")
    public void testGetBookDetail_SB01_idNull() {
        assertNull(bookService.getBookDetail(null));
    }

    /**
     * SB02: id<=0，返回null
     */
    @Test
    @DisplayName("SB02: id<=0，返回null")
    public void testGetBookDetail_SB02_idLEZero() {
        assertNull(bookService.getBookDetail(0L));
        assertNull(bookService.getBookDetail(-1L));
    }

    /**
     * SB03: 图书不存在，返回null
     */
    @Test
    @DisplayName("SB03: 图书不存在，返回null")
    public void testGetBookDetail_SB03_bookNotExist() {
        assertNull(bookService.getBookDetail(99999L));
    }

    /**
     * SB04: 作者为null，返回规范化后的图书
     */
    @Test
    @DisplayName("SB04: 作者为null，返回规范化后的图书")
    public void testGetBookDetail_SB04_authorNull() {
        Book book = new Book();
        book.setName("测试书");
        book.setAuthor(null);
        book.setCategory("测试");
        book.setPrice(new BigDecimal("10.0"));
        bookService.save(book);
        testBooks.add(book);
        Book result = bookService.getBookDetail(book.getId());
        assertNotNull(result);
        assertEquals("未知作者", result.getAuthor());
    }

    /**
     * SB05: 作者为空字符串，返回规范化后的图书
     */
    @Test
    @DisplayName("SB05: 作者为空字符串，返回规范化后的图书")
    public void testGetBookDetail_SB05_authorEmpty() {
        Book book = new Book();
        book.setName("测试书");
        book.setAuthor("");
        book.setCategory("测试");
        book.setPrice(new BigDecimal("10.0"));
        bookService.save(book);
        testBooks.add(book);
        Book result = bookService.getBookDetail(book.getId());
        assertNotNull(result);
        assertEquals("未知作者", result.getAuthor());
    }

    /**
     * SB06: 作者正常，返回原图书
     */
    @Test
    @DisplayName("SB06: 作者正常，返回原图书")
    public void testGetBookDetail_SB06_authorNormal() {
        Book book = new Book();
        book.setName("测试书");
        book.setAuthor("张三");
        book.setCategory("测试");
        book.setPrice(new BigDecimal("10.0"));
        bookService.save(book);
        testBooks.add(book);
        Book result = bookService.getBookDetail(book.getId());
        assertNotNull(result);
        assertEquals("张三", result.getAuthor());
    }

    // ==================== 黑盒测试（等价类划分法） ====================

    /**
     * 测试用例编号：TC-B01
     * 测试类型：黑盒-等价类划分
     * 测试目的：标准有效输入，所有字段有效，添加成功
     * 预期结果：添加成功，返回true，bookId不为null
     */
    @DisplayName("TC-B01: 标准有效输入")
    @Test
    public void testAddBook_standardValidInput() {
        Book book = new Book();
        book.setName("Java编程");
        book.setAuthor("张三");
        book.setCategory("编程");
        book.setPrice(new BigDecimal("88.5"));
        book.setDescription("入门教程");
        boolean result = bookService.save(book);
        testBooks.add(book);
        assertTrue(result);
        assertNotNull(book.getId());
    }

    /**
     * 测试用例编号：TC-B02
     * 测试类型：黑盒-等价类划分
     * 测试目的：标题为空，添加失败
     * 预期结果：添加失败，提示标题不能为空
     */
    @DisplayName("TC-B02: 标题为空")
    @Test
    public void testAddBook_titleEmpty() {
        Book book = new Book();
        book.setName("");
        book.setAuthor("张三");
        book.setCategory("编程");
        book.setPrice(new BigDecimal("88.5"));
        Exception e = assertThrows(Exception.class, () -> bookService.save(book));
        assertTrue(e.getMessage().contains("标题不能为空"));
    }

    /**
     * 测试用例编号：TC-B03
     * 测试类型：黑盒-等价类划分
     * 测试目的：标题超长，添加失败
     * 预期结果：添加失败，提示标题过长
     */
    @DisplayName("TC-B03: 标题超长")
    @Test
    public void testAddBook_titleTooLong() {
        Book book = new Book();
        book.setName("A".repeat(51));
        book.setAuthor("张三");
        book.setCategory("编程");
        book.setPrice(new BigDecimal("88.5"));
        Exception e = assertThrows(Exception.class, () -> bookService.save(book));
        assertTrue(e.getMessage().contains("标题过长"));
    }

    /**
     * 测试用例编号：TC-B04
     * 测试类型：黑盒-等价类划分
     * 测试目的：作者超长，添加失败
     * 预期结果：添加失败，提示作者过长
     */
    @DisplayName("TC-B04: 作者超长")
    @Test
    public void testAddBook_authorTooLong() {
        Book book = new Book();
        book.setName("Java编程");
        book.setAuthor("A".repeat(31));
        book.setCategory("编程");
        book.setPrice(new BigDecimal("88.5"));
        Exception e = assertThrows(Exception.class, () -> bookService.save(book));
        assertTrue(e.getMessage().contains("作者过长"));
    }

    /**
     * 测试用例编号：TC-B05
     * 测试类型：黑盒-等价类划分
     * 测试目的：分类为空，添加失败
     * 预期结果：添加失败，提示分类不能为空
     */
    @DisplayName("TC-B05: 分类为空")
    @Test
    public void testAddBook_categoryEmpty() {
        Book book = new Book();
        book.setName("Java编程");
        book.setAuthor("张三");
        book.setCategory("");
        book.setPrice(new BigDecimal("88.5"));
        Exception e = assertThrows(Exception.class, () -> bookService.save(book));
        assertTrue(e.getMessage().contains("分类不能为空"));
    }

    /**
     * 测试用例编号：TC-B06
     * 测试类型：黑盒-等价类划分
     * 测试目的：价格为0，添加失败
     * 预期结果：添加失败，提示价格无效
     */
    @DisplayName("TC-B06: 价格为0")
    @Test
    public void testAddBook_priceZero() {
        Book book = new Book();
        book.setName("Java编程");
        book.setAuthor("张三");
        book.setCategory("编程");
        book.setPrice(new BigDecimal("0"));
        Exception e = assertThrows(Exception.class, () -> bookService.save(book));
        assertTrue(e.getMessage().contains("价格无效"));
    }

    /**
     * 测试用例编号：TC-B07
     * 测试类型：黑盒-等价类划分
     * 测试目的：价格超上限，添加失败
     * 预期结果：添加失败，提示价格无效
     */
    @DisplayName("TC-B07: 价格超上限")
    @Test
    public void testAddBook_priceTooHigh() {
        Book book = new Book();
        book.setName("Java编程");
        book.setAuthor("张三");
        book.setCategory("编程");
        book.setPrice(new BigDecimal("10000"));
        Exception e = assertThrows(Exception.class, () -> bookService.save(book));
        assertTrue(e.getMessage().contains("价格无效"));
    }

    /**
     * 测试用例编号：TC-B08
     * 测试类型：黑盒-等价类划分
     * 测试目的：标题边界（1字符），添加成功
     * 预期结果：添加成功
     */
    @DisplayName("TC-B08: 标题边界（1字符）")
    @Test
    public void testAddBook_titleOneCharacter() {
        Book book = new Book();
        book.setName("J");
        book.setAuthor("张三");
        book.setCategory("编程");
        book.setPrice(new BigDecimal("88.5"));
        boolean result = bookService.save(book);
        testBooks.add(book);
        assertTrue(result);
        assertNotNull(book.getId());
    }

    /**
     * 测试用例编号：TC-B09
     * 测试类型：黑盒-等价类划分
     * 测试目的：价格边界（0.01），添加成功
     * 预期结果：添加成功
     */
    @DisplayName("TC-B09: 价格边界（0.01）")
    @Test
    public void testAddBook_priceZeroPointZeroOne() {
        Book book = new Book();
        book.setName("Java编程");
        book.setAuthor("张三");
        book.setCategory("编程");
        book.setPrice(new BigDecimal("0.01"));
        boolean result = bookService.save(book);
        testBooks.add(book);
        assertTrue(result);
        assertNotNull(book.getId());
    }

    /**
     * 测试用例编号：TC-B10
     * 测试类型：黑盒-等价类划分
     * 测试目的：描述超长，添加失败
     * 预期结果：添加失败，提示描述过长
     */
    @DisplayName("TC-B10: 描述超长")
    @Test
    public void testAddBook_descriptionTooLong() {
        Book book = new Book();
        book.setName("Java编程");
        book.setAuthor("张三");
        book.setCategory("编程");
        book.setPrice(new BigDecimal("88.5"));
        book.setDescription("A".repeat(201));
        Exception e = assertThrows(Exception.class, () -> bookService.save(book));
        assertTrue(e.getMessage().contains("描述过长"));
    }
} 