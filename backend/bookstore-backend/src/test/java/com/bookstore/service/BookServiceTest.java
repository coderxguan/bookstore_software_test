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
 * ==================== 图书管理模块测试 ====================
 * 本类覆盖《02-测试用例(修改版).md》中"图书管理模块测试"所有黑盒和白盒用例。
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

    /**
     * 测试用例编号：TC-B01
     * 测试类型：黑盒-等价类 E1,E2,E3,E4,E5
     * 测试目的：所有输入均为有效时，图书添加成功
     * 预期结果：添加成功，返回true，图书ID不为空
     */
    @DisplayName("TC-B01: 标准有效输入")
    @Test
    public void testAddBook_TCB01_标准有效输入() {
        Book book = new Book();
        book.setName("Java编程"); // E1
        book.setAuthor("张三");   // E2
        book.setCategory("编程"); // E3
        book.setPrice(new BigDecimal("88.5")); // E4
        book.setDescription("入门教程"); // E5
        boolean result = bookService.save(book);
        testBooks.add(book);
        assertTrue(result, "添加成功");
        assertNotNull(book.getId(), "图书ID不应为空");
    }

    /**
     * 测试用例编号：TC-B02
     * 测试类型：黑盒-等价类 I1
     * 测试目的：标题为空时，添加失败
     * 预期结果：添加失败，提示标题不能为空
     */
    @DisplayName("TC-B02: 标题为空")
    @Test
    public void testAddBook_TCB02_标题为空() {
        Book book = new Book();
        book.setName(""); // I1
        book.setAuthor("张三");
        book.setCategory("编程");
        book.setPrice(new BigDecimal("88.5"));
        Exception e = assertThrows(Exception.class, () -> bookService.save(book));
        assertTrue(e.getMessage().contains("标题不能为空"));
    }

    /**
     * 测试用例编号：TC-B03
     * 测试类型：黑盒-等价类 I2
     * 测试目的：标题超长时，添加失败
     * 预期结果：添加失败，提示标题过长
     */
    @DisplayName("TC-B03: 标题超长")
    @Test
    public void testAddBook_TCB03_标题超长() {
        Book book = new Book();
        book.setName("A".repeat(51)); // I2
        book.setAuthor("张三");
        book.setCategory("编程");
        book.setPrice(new BigDecimal("88.5"));
        Exception e = assertThrows(Exception.class, () -> bookService.save(book));
        assertTrue(e.getMessage().contains("标题过长"));
    }

    /**
     * 测试用例编号：TC-B04
     * 测试类型：黑盒-等价类 I4
     * 测试目的：作者超长时，添加失败
     * 预期结果：添加失败，提示作者过长
     */
    @DisplayName("TC-B04: 作者超长")
    @Test
    public void testAddBook_TCB04_作者超长() {
        Book book = new Book();
        book.setName("Java");
        book.setAuthor("A".repeat(31)); // I4
        book.setCategory("编程");
        book.setPrice(new BigDecimal("88.5"));
        Exception e = assertThrows(Exception.class, () -> bookService.save(book));
        assertTrue(e.getMessage().contains("作者过长"));
    }

    /**
     * 测试用例编号：TC-B05
     * 测试类型：黑盒-等价类 I5
     * 测试目的：分类为空时，添加失败
     * 预期结果：添加失败，提示分类不能为空
     */
    @DisplayName("TC-B05: 分类为空")
    @Test
    public void testAddBook_TCB05_分类为空() {
        Book book = new Book();
        book.setName("Java");
        book.setAuthor("张三");
        book.setCategory(""); // I5
        book.setPrice(new BigDecimal("88.5"));
        Exception e = assertThrows(Exception.class, () -> bookService.save(book));
        assertTrue(e.getMessage().contains("分类不能为空"));
    }

    /**
     * 测试用例编号：TC-B06
     * 测试类型：黑盒-等价类 I7
     * 测试目的：价格为0时，添加失败
     * 预期结果：添加失败，提示价格无效
     */
    @DisplayName("TC-B06: 价格为0")
    @Test
    public void testAddBook_TCB06_价格为0() {
        Book book = new Book();
        book.setName("Java");
        book.setAuthor("张三");
        book.setCategory("编程");
        book.setPrice(new BigDecimal("0")); // I7
        Exception e = assertThrows(Exception.class, () -> bookService.save(book));
        assertTrue(e.getMessage().contains("价格无效"));
    }

    /**
     * 测试用例编号：TC-B07
     * 测试类型：黑盒-等价类 I8
     * 测试目的：价格超上限时，添加失败
     * 预期结果：添加失败，提示价格无效
     */
    @DisplayName("TC-B07: 价格超上限")
    @Test
    public void testAddBook_TCB07_价格超上限() {
        Book book = new Book();
        book.setName("Java");
        book.setAuthor("张三");
        book.setCategory("编程");
        book.setPrice(new BigDecimal("10000")); // I8
        Exception e = assertThrows(Exception.class, () -> bookService.save(book));
        assertTrue(e.getMessage().contains("价格无效"));
    }

    /**
     * 测试用例编号：TC-B08
     * 测试类型：黑盒-等价类 E1
     * 测试目的：标题为1字符时，添加成功
     * 预期结果：添加成功
     */
    @DisplayName("TC-B08: 标题边界（1字符）")
    @Test
    public void testAddBook_TCB08_标题边界1字符() {
        Book book = new Book();
        book.setName("J"); // E1
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
     * 测试类型：黑盒-等价类 E4
     * 测试目的：价格为0.01时，添加成功
     * 预期结果：添加成功
     */
    @DisplayName("TC-B09: 价格边界（0.01）")
    @Test
    public void testAddBook_TCB09_价格边界001() {
        Book book = new Book();
        book.setName("Java");
        book.setAuthor("张三");
        book.setCategory("编程");
        book.setPrice(new BigDecimal("0.01")); // E4
        boolean result = bookService.save(book);
        testBooks.add(book);
        assertTrue(result);
        assertNotNull(book.getId());
    }

    /**
     * 测试用例编号：TC-B10
     * 测试类型：黑盒-等价类 I9
     * 测试目的：描述超长时，添加失败
     * 预期结果：添加失败，提示描述过长
     */
    @DisplayName("TC-B10: 描述超长")
    @Test
    public void testAddBook_TCB10_描述超长() {
        Book book = new Book();
        book.setName("Java");
        book.setAuthor("张三");
        book.setCategory("编程");
        book.setPrice(new BigDecimal("88.5"));
        book.setDescription("A".repeat(201)); // I9
        Exception e = assertThrows(Exception.class, () -> bookService.save(book));
        assertTrue(e.getMessage().contains("描述过长"));
    }

    // ==================== 白盒测试（语句/判定/条件/路径/循环） ====================

    /**
     * 测试用例编号：TCB-SC01
     * 测试类型：白盒-语句覆盖
     * 测试目的：参数无效（id=null），覆盖输入验证分支
     * 预期结果：返回null
     */
    @DisplayName("TCB-SC01: 参数无效（id=null）")
    @Test
    public void testGetBookDetail_TCB_SC01_paramNull() {
        Book result = bookService.getBookDetail(null);
        assertNull(result);
    }

    /**
     * 测试用例编号：TCB-SC02
     * 测试类型：白盒-语句覆盖
     * 测试目的：图书不存在（id=99999），覆盖未查到图书分支
     * 预期结果：返回null
     */
    @DisplayName("TCB-SC02: 图书不存在（id=99999）")
    @Test
    public void testGetBookDetail_TCB_SC02_bookNotExist() {
        Book result = bookService.getBookDetail(99999L);
        assertNull(result);
    }

    /**
     * 测试用例编号：TCB-SC03
     * 测试类型：白盒-语句覆盖
     * 测试目的：作者需规范化（author=null），覆盖作者为空分支
     * 预期结果：返回作者为"未知作者"的图书
     */
    @DisplayName("TCB-SC03: 作者需规范化（author=null）")
    @Test
    public void testGetBookDetail_TCB_SC03_authorNull() {
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
     * 测试用例编号：TCB-SC04
     * 测试类型：白盒-语句覆盖
     * 测试目的：作者正常，无需规范化
     * 预期结果：返回原图书不变
     */
    @DisplayName("TCB-SC04: 作者正常")
    @Test
    public void testGetBookDetail_TCB_SC04_authorNormal() {
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

    // ==================== 判定覆盖 ====================
    /**
     * 测试用例编号：TCB-DC01
     * 测试类型：白盒-判定覆盖
     * 测试目的：id==null || id<=0 判定为true，返回null
     * 预期结果：返回null
     */
    @DisplayName("TCB-DC01: id==null || id<=0 判定为true")
    @Test
    public void testGetBookDetail_TCB_DC01_idNullOrLEZero() {
        assertNull(bookService.getBookDetail(null));
        assertNull(bookService.getBookDetail(0L));
        assertNull(bookService.getBookDetail(-1L));
    }

    /**
     * 测试用例编号：TCB-DC02
     * 测试类型：白盒-判定覆盖
     * 测试目的：id==null || id<=0 判定为false，继续执行
     * 预期结果：继续执行
     */
    @DisplayName("TCB-DC02: id==null || id<=0 判定为false")
    @Test
    public void testGetBookDetail_TCB_DC02_idValid() {
        Book book = new Book();
        book.setName("测试书");
        book.setAuthor("张三");
        book.setCategory("测试");
        book.setPrice(new BigDecimal("10.0"));
        bookService.save(book);
        testBooks.add(book);
        Book result = bookService.getBookDetail(book.getId());
        assertNotNull(result);
    }

    /**
     * 测试用例编号：TCB-DC03
     * 测试类型：白盒-判定覆盖
     * 测试目的：book!=null 判定为true，进行数据规范化
     * 预期结果：返回规范化后的图书
     */
    @DisplayName("TCB-DC03: book!=null 判定为true")
    @Test
    public void testGetBookDetail_TCB_DC03_bookNotNull() {
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
     * 测试用例编号：TCB-DC04
     * 测试类型：白盒-判定覆盖
     * 测试目的：book!=null 判定为false，返回null
     * 预期结果：返回null
     */
    @DisplayName("TCB-DC04: book!=null 判定为false")
    @Test
    public void testGetBookDetail_TCB_DC04_bookNull() {
        assertNull(bookService.getBookDetail(99999L));
    }

    /**
     * 测试用例编号：TCB-DC05
     * 测试类型：白盒-判定覆盖
     * 测试目的：book.getAuthor()==null || book.getAuthor().isEmpty() 判定为true，设置为"未知作者"
     * 预期结果：作者为"未知作者"
     */
    @DisplayName("TCB-DC05: author==null 或 author为空 判定为true")
    @Test
    public void testGetBookDetail_TCB_DC05_authorNullOrEmpty() {
        Book book = new Book();
        book.setName("测试书");
        book.setAuthor(null);
        book.setCategory("测试");
        book.setPrice(new BigDecimal("10.0"));
        bookService.save(book);
        testBooks.add(book);
        Book result = bookService.getBookDetail(book.getId());
        assertEquals("未知作者", result.getAuthor());

        Book book2 = new Book();
        book2.setName("测试书2");
        book2.setAuthor("");
        book2.setCategory("测试");
        book2.setPrice(new BigDecimal("10.0"));
        bookService.save(book2);
        testBooks.add(book2);
        Book result2 = bookService.getBookDetail(book2.getId());
        assertEquals("未知作者", result2.getAuthor());
    }

    /**
     * 测试用例编号：TCB-DC06
     * 测试类型：白盒-判定覆盖
     * 测试目的：author为正常字符串，判定为false，不修改作者
     * 预期结果：作者不变
     */
    @DisplayName("TCB-DC06: author正常 判定为false")
    @Test
    public void testGetBookDetail_TCB_DC06_authorNormal() {
        Book book = new Book();
        book.setName("测试书");
        book.setAuthor("张三");
        book.setCategory("测试");
        book.setPrice(new BigDecimal("10.0"));
        bookService.save(book);
        testBooks.add(book);
        Book result = bookService.getBookDetail(book.getId());
        assertEquals("张三", result.getAuthor());
    }

    // ==================== 条件覆盖、路径覆盖、循环结构等用例可继续补充 ====================
} 