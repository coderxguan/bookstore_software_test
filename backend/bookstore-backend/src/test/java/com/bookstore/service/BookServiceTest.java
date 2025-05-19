package com.bookstore.service;

import com.bookstore.entity.Book;
import com.bookstore.mapper.BookMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 图书管理模块测试类(甲)
 * 包含黑盒测试(等价类划分、边界值分析)和白盒测试(语句覆盖、条件覆盖、路径测试)
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

    /**
     * 测试前准备工作
     */
    @BeforeEach
    public void setUp() {
        // 初始化测试数据
        testBooks = new ArrayList<>();
        
        Book book1 = new Book();
        book1.setName("Java编程");
        book1.setAuthor("张三");
        book1.setCategory("编程");
        book1.setPrice(new BigDecimal("88.50"));
        book1.setDescription("Java入门教程");
        book1.setFavoriteCount(10);
        bookService.save(book1);
        
        Book book2 = new Book();
        book2.setName("Python编程");
        book2.setAuthor("李四");
        book2.setCategory("编程");
        book2.setPrice(new BigDecimal("75.00"));
        book2.setDescription("Python入门教程");
        book2.setFavoriteCount(5);
        bookService.save(book2);
        
        testBooks.add(book1);
        testBooks.add(book2);
    }

    /**
     * 测试后清理工作
     */
    @AfterEach
    public void tearDown() {
        // 清理资源
        for (Book book : testBooks) {
            bookMapper.deleteById(book.getId());
        }
        testBooks = null;
    }

    /**
     * 测试用例: TC-B01 添加有效图书
     * 等价类覆盖: E1(有效标题), E3(有效价格)
     */
    @DisplayName("TC-B01: 添加有效图书测试")
    @Test
    public void testAddValidBook() {
        // 准备测试数据
        Book book = new Book();
        book.setName("Java编程高级"); // 有效标题(E1)
        book.setAuthor("张三");
        book.setCategory("编程");
        book.setPrice(new BigDecimal("88.50")); // 有效价格(E3)
        book.setDescription("Java入门教程");
        
        // 执行测试
        boolean result = bookService.save(book);
        
        // 验证结果
        assertTrue(result, "添加有效图书应该成功");
        assertNotNull(book.getId(), "图书ID不应为空");
    }

    /**
     * 测试用例: TC-B02 添加图书(标题为空)
     * 等价类覆盖: I1(无效标题-空), E3(有效价格)
     * 预期: 添加失败，标题不能为空
     */
    @DisplayName("TC-B02: 添加空标题图书测试")
    @Test
    public void testAddBookWithEmptyTitle() {
        // 准备测试数据
        Book book = new Book();
        book.setName(""); // 无效标题-空(I1)
        book.setAuthor("张三");
        book.setPrice(new BigDecimal("88.50")); // 有效价格(E3)
        
        // 执行测试
        try {
            bookService.save(book);
            fail("应当抛出异常，标题不能为空");
        } catch (Exception e) {
            // 验证异常信息
            assertTrue(e.getMessage().contains("标题不能为空"));
        }
    }

    /**
     * 测试用例: TC-B03 添加图书(价格无效)
     * 等价类覆盖: E1(有效标题), I5(无效价格-负数)
     * 预期: 添加失败，价格必须大于0
     */
    @DisplayName("TC-B03: 添加负价格图书测试")
    @Test
    public void testAddBookWithInvalidPrice() {
        // 准备测试数据
        Book book = new Book();
        book.setName("Java编程"); // 有效标题(E1)
        book.setAuthor("张三");
        book.setPrice(new BigDecimal("-10")); // 无效价格-负数(I5)
        
        // 执行测试
        try {
            bookService.save(book);
            fail("应当抛出异常，价格必须大于0");
        } catch (Exception e) {
            // 验证异常信息
            assertTrue(e.getMessage().contains("价格必须大于0"));
        }
    }

    /**
     * 测试用例: TC-B04 价格边界(最小)
     * 测试最小有效价格0.01
     */
    @DisplayName("TC-B04: 价格最小边界值测试")
    @Test
    public void testMinimumPriceBoundary() {
        // 准备测试数据
        Book book = new Book();
        book.setName("边界测试");
        book.setAuthor("测试");
        book.setPrice(new BigDecimal("0.01")); // 边界值-最小有效价格
        
        // 执行测试
        boolean result = bookService.save(book);
        
        // 验证结果
        assertTrue(result, "添加最小价格图书应该成功");
        assertNotNull(book.getId(), "图书ID不应为空");
    }

    /**
     * 测试用例: TC-B05 价格边界(最大)
     * 测试最大有效价格9999.99
     */
    @DisplayName("TC-B05: 价格最大边界值测试")
    @Test
    public void testMaximumPriceBoundary() {
        // 准备测试数据
        Book book = new Book();
        book.setName("边界测试");
        book.setAuthor("测试");
        book.setPrice(new BigDecimal("9999.99")); // 边界值-最大有效价格
        
        // 执行测试
        boolean result = bookService.save(book);
        
        // 验证结果
        assertTrue(result, "添加最大价格图书应该成功");
        assertNotNull(book.getId(), "图书ID不应为空");
    }

    /**
     * 测试用例: TC-B06 价格边界(超出)
     * 测试超出有效价格范围10000.00
     */
    @DisplayName("TC-B06: 价格超出边界值测试")
    @Test
    public void testExceedMaximumPriceBoundary() {
        // 准备测试数据
        Book book = new Book();
        book.setName("边界测试");
        book.setAuthor("测试");
        book.setPrice(new BigDecimal("10000.00")); // 边界值-超出最大有效价格
        
        // 执行测试
        try {
            bookService.save(book);
            fail("应当抛出异常，价格超出范围");
        } catch (Exception e) {
            // 验证异常信息
            assertTrue(e.getMessage().contains("价格超出范围"));
        }
    }

    /**
     * 测试用例: TC-WB01 添加图书语句覆盖
     * 白盒测试: 语句覆盖 - BookServiceImpl.addBook()
     */
    @DisplayName("TC-WB01: 添加图书语句覆盖测试")
    @Test
    public void testAddBookStatementCoverage() {
        // 准备完整有效的书本数据，确保所有语句都会执行
        Book book = new Book();
        book.setName("Java编程");
        book.setAuthor("张三");
        book.setCategory("编程");
        book.setPrice(new BigDecimal("88.50"));
        book.setDescription("测试描述");
        
        // 执行测试
        boolean result = bookService.save(book);
        
        // 验证结果
        assertTrue(result, "添加图书应该成功");
        assertNotNull(book.getId(), "图书ID不应为空");
    }

    /**
     * 测试用例: TC-WB02 更新图书语句覆盖
     * 白盒测试: 语句覆盖 - BookServiceImpl.updateBook()
     */
    @DisplayName("TC-WB02: 更新图书语句覆盖测试")
    @Test
    public void testUpdateBookStatementCoverage() {
        // 添加一本书
        Book book = new Book();
        book.setName("更新测试");
        book.setAuthor("原作者");
        book.setPrice(new BigDecimal("50.00"));
        bookService.save(book);
        
        // 准备测试数据 - 更新已存在图书
        book.setName("更新标题");
        book.setAuthor("更新作者");
        book.setPrice(new BigDecimal("99.99"));
        
        // 执行测试
        boolean result = bookService.updateById(book);
        
        // 验证结果
        assertTrue(result, "更新图书应该成功");
        
        // 验证更新结果
        Book updated = bookService.getById(book.getId());
        assertEquals("更新标题", updated.getName());
        assertEquals("更新作者", updated.getAuthor());
        assertEquals(0, new BigDecimal("99.99").compareTo(updated.getPrice()));
    }

    /**
     * 测试用例: TC-WB03 删除图书语句覆盖
     * 白盒测试: 语句覆盖 - BookServiceImpl.deleteBook()
     */
    @DisplayName("TC-WB03: 删除图书语句覆盖测试")
    @Test
    public void testDeleteBookStatementCoverage() {
        // 添加一本书
        Book book = new Book();
        book.setName("删除测试");
        book.setAuthor("测试作者");
        book.setPrice(new BigDecimal("30.00"));
        bookService.save(book);
        
        Long bookId = book.getId();
        
        // 执行测试
        boolean result = bookService.removeById(bookId);
        
        // 验证结果
        assertTrue(result, "删除图书应该成功");
        
        // 验证删除结果
        Book deleted = bookService.getById(bookId);
        assertNull(deleted, "删除后不应能查询到图书");
    }

    /**
     * 测试用例: TC-CB01 图书价格条件判断(正值)
     * 白盒测试: 条件覆盖 - if (book.getPrice() <= 0)为false的分支
     */
    @DisplayName("TC-CB01: 图书价格正值条件测试")
    @Test
    public void testBookPriceConditionPositive() {
        // 准备测试数据
        Book book = new Book();
        book.setName("条件测试");
        book.setPrice(new BigDecimal("50.00")); // 正价格，条件判断为false
        
        // 执行测试
        boolean result = bookService.save(book);
        
        // 验证结果
        assertTrue(result, "正价格应该通过验证");
    }

    /**
     * 测试用例: TC-CB02 图书价格条件判断(零值)
     * 白盒测试: 条件覆盖 - if (book.getPrice() <= 0)为true的分支
     */
    @DisplayName("TC-CB02: 图书价格零值条件测试")
    @Test
    public void testBookPriceConditionZero() {
        // 准备测试数据
        Book book = new Book();
        book.setName("条件测试");
        book.setPrice(new BigDecimal("0")); // 零价格，条件判断为true
        
        // 执行测试
        try {
            bookService.save(book);
            fail("价格为零应该失败");
        } catch (Exception e) {
            // 验证异常信息
            assertTrue(e.getMessage().contains("价格必须大于0"));
        }
    }

    /**
     * 测试用例: TC-CB03 图书存在性判断 (存在情况)
     * 白盒测试: 条件覆盖 - if (bookExists)为true的分支
     */
    @DisplayName("TC-CB03: 图书存在条件测试")
    @Test
    public void testBookExistsConditionTrue() {
        // 准备测试数据 - 使用setUp中已保存的图书
        Book originalBook = testBooks.get(0);
        Long bookId = originalBook.getId();
        
        // 执行测试
        Book result = bookService.getById(bookId);
        
        // 验证结果
        assertNotNull(result, "应该返回存在的图书");
        assertEquals(bookId, result.getId(), "图书ID应该匹配");
    }

    /**
     * 测试用例: TC-CB03 图书存在性判断 (不存在情况)
     * 白盒测试: 条件覆盖 - if (bookExists)为false的分支
     */
    @DisplayName("TC-CB03: 图书不存在条件测试")
    @Test
    public void testBookExistsConditionFalse() {
        // 准备测试数据
        Long nonExistingBookId = 999999L;
        
        // 执行测试
        Book result = bookService.getById(nonExistingBookId);
        
        // 验证结果
        assertNull(result, "不存在的图书应返回null");
    }

    /**
     * 测试用例: TC-PB01 添加图书成功路径
     * 白盒测试: 路径测试 - 完整成功路径
     */
    @DisplayName("TC-PB01: 添加图书成功路径测试")
    @Test
    public void testAddBookSuccessPath() {
        // 准备测试数据
        Book book = new Book();
        book.setName("路径测试");
        book.setAuthor("测试作者");
        book.setCategory("测试分类");
        book.setPrice(new BigDecimal("100.00"));
        book.setDescription("测试描述");
        
        // 执行测试
        boolean result = bookService.save(book);
        
        // 验证结果
        assertTrue(result, "添加图书应该成功");
        assertNotNull(book.getId(), "图书ID不应为空");
    }

    /**
     * 测试用例: TC-PB02 添加图书失败路径
     * 白盒测试: 路径测试 - 参数校验失败路径
     */
    @DisplayName("TC-PB02: 添加图书失败路径测试")
    @Test
    public void testAddBookFailurePath() {
        // 准备测试数据 - 无效价格
        Book book = new Book();
        book.setName("路径测试");
        book.setPrice(new BigDecimal("-10.00")); // 无效价格，导致参数校验失败
        
        // 执行测试
        try {
            bookService.save(book);
            fail("添加无效图书应该失败");
        } catch (Exception e) {
            // 验证异常信息
            assertTrue(e.getMessage().contains("价格必须大于0"));
        }
    }
} 