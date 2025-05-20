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

    // ==================== 白盒测试（语句/判定/条件/路径/循环） ====================

    /**
     * 测试用例编号：TCB-SC01
     * 测试类型：白盒-语句覆盖
     * 测试目的：参数无效（id=null），覆盖输入验证分支
     * 预期结果：返回null
     */
    @DisplayName("TCB-SC01: 参数无效（id=null）")
    @Test
    public void testGetBookDetail_paramNull() {
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
    public void testGetBookDetail_bookNotExist() {
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
    public void testGetBookDetail_authorNull() {
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
     * 预期结果：返回原作者
     */
    @DisplayName("TCB-SC04: 作者正常，无需规范化")
    @Test
    public void testGetBookDetail_authorNormal() {
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

    // 判定覆盖
    /**
     * 测试用例编号：TCB-DC01
     * 测试类型：白盒-判定覆盖
     * 测试目的：id==null || id<=0 为真，返回null
     * 预期结果：返回null
     */
    @DisplayName("TCB-DC01: id==null || id<=0 为真")
    @Test
    public void testGetBookDetail_idNullOrLEZero() {
        assertNull(bookService.getBookDetail(null));
        assertNull(bookService.getBookDetail(0L));
        assertNull(bookService.getBookDetail(-1L));
    }

    /**
     * 测试用例编号：TCB-DC02
     * 测试类型：白盒-判定覆盖
     * 测试目的：id==null || id<=0 为假，继续执行
     * 预期结果：返回图书对象
     */
    @DisplayName("TCB-DC02: id==null || id<=0 为假")
    @Test
    public void testGetBookDetail_idValid() {
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
     * 测试目的：book!=null 为真，进行数据规范化
     * 预期结果：返回作者为"未知作者"的图书
     */
    @DisplayName("TCB-DC03: book!=null 为真")
    @Test
    public void testGetBookDetail_bookNotNull() {
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
     * 测试目的：book!=null 为假，返回null
     * 预期结果：返回null
     */
    @DisplayName("TCB-DC04: book!=null 为假")
    @Test
    public void testGetBookDetail_bookNull() {
        assertNull(bookService.getBookDetail(99999L));
    }

    /**
     * 测试用例编号：TCB-DC05
     * 测试类型：白盒-判定覆盖
     * 测试目的：book.getAuthor()==null || book.getAuthor().isEmpty() 为真，设置为"未知作者"
     * 预期结果：返回作者为"未知作者"的图书
     */
    @DisplayName("TCB-DC05: author==null 或 author为空 为真")
    @Test
    public void testGetBookDetail_authorNullOrEmpty() {
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
     * 测试目的：作者为正常字符串，为假，不修改作者
     * 预期结果：返回原作者
     */
    @DisplayName("TCB-DC06: 作者为正常字符串，为假")
    @Test
    public void testGetBookDetail_authorNormalString() {
        Book book = new Book();
        book.setName("测试书");
        book.setAuthor("李四");
        book.setCategory("测试");
        book.setPrice(new BigDecimal("10.0"));
        bookService.save(book);
        testBooks.add(book);
        Book result = bookService.getBookDetail(book.getId());
        assertEquals("李四", result.getAuthor());
    }

    // 条件覆盖
    /**
     * 测试用例编号：TCB-CC01
     * 测试类型：白盒-条件覆盖
     * 测试目的：id==null 为真，返回null
     * 预期结果：返回null
     */
    @DisplayName("TCB-CC01: id==null 为真")
    @Test
    public void testGetBookDetail_idIsNull() {
        assertNull(bookService.getBookDetail(null));
    }

    /**
     * 测试用例编号：TCB-CC02
     * 测试类型：白盒-条件覆盖
     * 测试目的：id==null 为假，id<=0 为真，返回null
     * 预期结果：返回null
     */
    @DisplayName("TCB-CC02: id==null 为假，id<=0 为真")
    @Test
    public void testGetBookDetail_idLEZero() {
        assertNull(bookService.getBookDetail(-1L));
    }

    /**
     * 测试用例编号：TCB-CC03
     * 测试类型：白盒-条件覆盖
     * 测试目的：id<=0 为假，继续执行
     * 预期结果：返回图书对象
     */
    @DisplayName("TCB-CC03: id<=0 为假")
    @Test
    public void testGetBookDetail_idGTZero() {
        Book book = new Book();
        book.setName("测试书");
        book.setAuthor("王五");
        book.setCategory("测试");
        book.setPrice(new BigDecimal("10.0"));
        bookService.save(book);
        testBooks.add(book);
        Book result = bookService.getBookDetail(book.getId());
        assertNotNull(result);
    }

    /**
     * 测试用例编号：TCB-CC04
     * 测试类型：白盒-条件覆盖
     * 测试目的：book.getAuthor()==null 为真，设置为"未知作者"
     * 预期结果：返回作者为"未知作者"的图书
     */
    @DisplayName("TCB-CC04: author==null 为真")
    @Test
    public void testGetBookDetail_authorIsNull() {
        Book book = new Book();
        book.setName("测试书");
        book.setAuthor(null);
        book.setCategory("测试");
        book.setPrice(new BigDecimal("10.0"));
        bookService.save(book);
        testBooks.add(book);
        Book result = bookService.getBookDetail(book.getId());
        assertEquals("未知作者", result.getAuthor());
    }

    /**
     * 测试用例编号：TCB-CC05
     * 测试类型：白盒-条件覆盖
     * 测试目的：book.getAuthor().isEmpty() 为真，设置为"未知作者"
     * 预期结果：返回作者为"未知作者"的图书
     */
    @DisplayName("TCB-CC05: author为空字符串 为真")
    @Test
    public void testGetBookDetail_authorIsEmpty() {
        Book book = new Book();
        book.setName("测试书");
        book.setAuthor("");
        book.setCategory("测试");
        book.setPrice(new BigDecimal("10.0"));
        bookService.save(book);
        testBooks.add(book);
        Book result = bookService.getBookDetail(book.getId());
        assertEquals("未知作者", result.getAuthor());
    }

    // 判定/条件组合覆盖
    /**
     * 测试用例编号：TCB-MCC01
     * 测试类型：白盒-判定/条件组合覆盖
     * 测试目的：id为null，短路返回null
     * 预期结果：返回null
     */
    @DisplayName("TCB-MCC01: id为null，短路返回null")
    @Test
    public void testGetBookDetail_idNullShortCircuit() {
        assertNull(bookService.getBookDetail(null));
    }

    /**
     * 测试用例编号：TCB-MCC02
     * 测试类型：白盒-判定/条件组合覆盖
     * 测试目的：id为负数，短路返回null
     * 预期结果：返回null
     */
    @DisplayName("TCB-MCC02: id为负数，短路返回null")
    @Test
    public void testGetBookDetail_idNegativeShortCircuit() {
        assertNull(bookService.getBookDetail(-1L));
    }

    /**
     * 测试用例编号：TCB-MCC03
     * 测试类型：白盒-判定/条件组合覆盖
     * 测试目的：id为正数，继续执行
     * 预期结果：返回图书对象
     */
    @DisplayName("TCB-MCC03: id为正数，继续执行")
    @Test
    public void testGetBookDetail_idPositiveContinue() {
        Book book = new Book();
        book.setName("测试书");
        book.setAuthor("赵六");
        book.setCategory("测试");
        book.setPrice(new BigDecimal("10.0"));
        bookService.save(book);
        testBooks.add(book);
        Book result = bookService.getBookDetail(book.getId());
        assertNotNull(result);
    }

    // 路径覆盖
    /**
     * 测试用例编号：TCB-PC01
     * 测试类型：白盒-路径覆盖
     * 测试目的：id参数验证失败路径
     * 预期结果：返回null
     */
    @DisplayName("TCB-PC01: id参数验证失败路径")
    @Test
    public void testGetBookDetail_paramCheckFailPath() {
        assertNull(bookService.getBookDetail(null));
        assertNull(bookService.getBookDetail(-1L));
    }

    /**
     * 测试用例编号：TCB-PC02
     * 测试类型：白盒-路径覆盖
     * 测试目的：图书查询失败路径
     * 预期结果：返回null
     */
    @DisplayName("TCB-PC02: 图书查询失败路径")
    @Test
    public void testGetBookDetail_bookNotFoundPath() {
        assertNull(bookService.getBookDetail(99999L));
    }

    /**
     * 测试用例编号：TCB-PC03
     * 测试类型：白盒-路径覆盖
     * 测试目的：作者需规范化路径
     * 预期结果：返回作者为"未知作者"的图书
     */
    @DisplayName("TCB-PC03: 作者需规范化路径")
    @Test
    public void testGetBookDetail_authorNormalizePath() {
        Book book = new Book();
        book.setName("测试书");
        book.setAuthor(null);
        book.setCategory("测试");
        book.setPrice(new BigDecimal("10.0"));
        bookService.save(book);
        testBooks.add(book);
        Book result = bookService.getBookDetail(book.getId());
        assertEquals("未知作者", result.getAuthor());
    }

    /**
     * 测试用例编号：TCB-PC04
     * 测试类型：白盒-路径覆盖
     * 测试目的：图书正常无需规范化路径
     * 预期结果：返回原作者
     */
    @DisplayName("TCB-PC04: 图书正常无需规范化路径")
    @Test
    public void testGetBookDetail_authorNormalPath() {
        Book book = new Book();
        book.setName("测试书");
        book.setAuthor("王五");
        book.setCategory("测试");
        book.setPrice(new BigDecimal("10.0"));
        bookService.save(book);
        testBooks.add(book);
        Book result = bookService.getBookDetail(book.getId());
        assertEquals("王五", result.getAuthor());
    }

    // ==================== 循环结构测试 ====================

    /**
     * 测试用例编号：TCB-LT01
     * 测试类型：白盒-循环结构
     * 测试目的：无数据循环，空的图书列表，任意查询词
     * 预期结果：返回空列表
     */
    @DisplayName("TCB-LT01: 无数据循环")
    @Test
    public void testListAllBooks_emptyList() {
        // 假设数据库为空，直接调用
        List<Book> result = bookService.listAllBooksAndSortByFavoriteCount("任意", "desc");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /**
     * 测试用例编号：TCB-LT02
     * 测试类型：白盒-循环结构
     * 测试目的：单次循环，包含1本图书的列表，匹配的查询词
     * 预期结果：返回包含该图书的列表
     */
    @DisplayName("TCB-LT02: 单次循环")
    @Test
    public void testListAllBooks_singleBookMatch() {
        Book book = new Book();
        book.setName("Java编程");
        book.setAuthor("张三");
        book.setCategory("编程");
        book.setPrice(new BigDecimal("88.5"));
        bookService.save(book);
        testBooks.add(book);
        List<Book> result = bookService.listAllBooksAndSortByFavoriteCount("Java", "desc");
        assertEquals(1, result.size());
        assertEquals("Java编程", result.get(0).getName());
    }

    /**
     * 测试用例编号：TCB-LT03
     * 测试类型：白盒-循环结构
     * 测试目的：多次循环-部分匹配，包含多本图书的列表，只匹配部分图书的查询词
     * 预期结果：返回匹配的图书列表
     */
    @DisplayName("TCB-LT03: 多次循环-部分匹配")
    @Test
    public void testListAllBooks_partialMatch() {
        Book book1 = new Book();
        book1.setName("Java编程");
        book1.setAuthor("张三");
        book1.setCategory("编程");
        book1.setPrice(new BigDecimal("88.5"));
        bookService.save(book1);
        testBooks.add(book1);
        Book book2 = new Book();
        book2.setName("Python入门");
        book2.setAuthor("李四");
        book2.setCategory("编程");
        book2.setPrice(new BigDecimal("66.6"));
        bookService.save(book2);
        testBooks.add(book2);
        List<Book> result = bookService.listAllBooksAndSortByFavoriteCount("Java", "desc");
        assertEquals(1, result.size());
        assertEquals("Java编程", result.get(0).getName());
    }

    /**
     * 测试用例编号：TCB-LT04
     * 测试类型：白盒-循环结构
     * 测试目的：多次循环-全部匹配，包含多本图书的列表，匹配所有图书的查询词
     * 预期结果：返回所有图书的列表
     */
    @DisplayName("TCB-LT04: 多次循环-全部匹配")
    @Test
    public void testListAllBooks_allMatch() {
        Book book1 = new Book();
        book1.setName("Java编程");
        book1.setAuthor("张三");
        book1.setCategory("编程");
        book1.setPrice(new BigDecimal("88.5"));
        bookService.save(book1);
        testBooks.add(book1);
        Book book2 = new Book();
        book2.setName("Java进阶");
        book2.setAuthor("李四");
        book2.setCategory("编程");
        book2.setPrice(new BigDecimal("99.9"));
        bookService.save(book2);
        testBooks.add(book2);
        List<Book> result = bookService.listAllBooksAndSortByFavoriteCount("Java", "desc");
        assertEquals(2, result.size());
    }

    /**
     * 测试用例编号：TCB-LT05
     * 测试类型：白盒-循环结构
     * 测试目的：多次循环-无匹配，包含多本图书的列表，不匹配任何图书的查询词
     * 预期结果：返回空列表
     */
    @DisplayName("TCB-LT05: 多次循环-无匹配")
    @Test
    public void testListAllBooks_noMatch() {
        Book book1 = new Book();
        book1.setName("Java编程");
        book1.setAuthor("张三");
        book1.setCategory("编程");
        book1.setPrice(new BigDecimal("88.5"));
        bookService.save(book1);
        testBooks.add(book1);
        Book book2 = new Book();
        book2.setName("Python入门");
        book2.setAuthor("李四");
        book2.setCategory("编程");
        book2.setPrice(new BigDecimal("66.6"));
        bookService.save(book2);
        testBooks.add(book2);
        List<Book> result = bookService.listAllBooksAndSortByFavoriteCount("C++", "desc");
        assertTrue(result.isEmpty());
    }

    /**
     * 测试用例编号：TCB-LT06
     * 测试类型：白盒-循环结构
     * 测试目的：大数据量循环，接近系统处理上限的图书列表
     * 预期结果：正确返回匹配的图书列表，无性能问题
     */
    @DisplayName("TCB-LT06: 大数据量循环")
    @Test
    public void testListAllBooks_largeData() {
        for (int i = 0; i < 100; i++) {
            Book book = new Book();
            book.setName("Java编程" + i);
            book.setAuthor("作者" + i);
            book.setCategory("编程");
            book.setPrice(new BigDecimal("88.5"));
            bookService.save(book);
            testBooks.add(book);
        }
        List<Book> result = bookService.listAllBooksAndSortByFavoriteCount("Java", "desc");
        assertEquals(100, result.size());
    }

    /**
     * 测试用例编号：TCB-LT07
     * 测试类型：白盒-循环结构
     * 测试目的：特殊数据处理，包含部分字段为null的图书
     * 预期结果：正确处理空值不抛出异常
     */
    @DisplayName("TCB-LT07: 特殊数据处理")
    @Test
    public void testListAllBooks_nullFields() {
        Book book = new Book();
        book.setName(null);
        book.setAuthor(null);
        book.setCategory("编程");
        book.setPrice(new BigDecimal("88.5"));
        bookService.save(book);
        testBooks.add(book);
        List<Book> result = bookService.listAllBooksAndSortByFavoriteCount("Java", "desc");
        assertNotNull(result);
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