package com.bookstore.service;

import com.bookstore.entity.Book;
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
 * 图书搜索模块测试类(乙)
 * 包含黑盒测试(决策表测试)和白盒测试(语句覆盖、条件覆盖)
 */
@SpringBootTest
@Transactional
@DisplayName("图书搜索模块测试")
public class BookSearchTest {

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
        
        // 图书1: Java相关, Bruce作者
        Book book1 = new Book();
        book1.setName("Java编程思想");
        book1.setAuthor("Bruce Eckel");
        book1.setCategory("编程");
        book1.setPrice(new BigDecimal("99.00"));
        book1.setDescription("Java经典教程");
        book1.setFavoriteCount(100);
        bookService.save(book1);
        
        // 图书2: Java相关, 非Bruce作者
        Book book2 = new Book();
        book2.setName("Effective Java");
        book2.setAuthor("Joshua Bloch");
        book2.setCategory("编程");
        book2.setPrice(new BigDecimal("89.00"));
        book2.setDescription("Java进阶书籍");
        book2.setFavoriteCount(80);
        bookService.save(book2);
        
        // 图书3: 非Java相关, Bruce作者
        Book book3 = new Book();
        book3.setName("Python入门");
        book3.setAuthor("Bruce Lee");
        book3.setCategory("编程");
        book3.setPrice(new BigDecimal("79.00"));
        book3.setDescription("Python基础教程");
        book3.setFavoriteCount(60);
        bookService.save(book3);
        
        // 图书4: 非Java相关, 非Bruce作者
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
    
    /**
     * 测试用例: TC-S01 标题+作者组合搜索
     * 决策表测试: 规则1(标题条件存在 + 作者条件存在)
     */
    @DisplayName("标题+作者组合搜索测试")
    @Test
    public void testSearchByTitleAndAuthor() {
        // 准备测试数据
        String query = "Java Bruce";
        
        // 执行测试
        List<Book> results = bookService.listAllBooks(query);
        
        // 验证结果
        assertNotNull(results);
        assertEquals(1, results.size(), "应只返回1本同时匹配标题和作者的图书");
        assertTrue(results.stream().anyMatch(book -> 
            book.getName().contains("Java") && book.getAuthor().contains("Bruce")));
    }
    
    /**
     * 测试用例: TC-S02 仅标题搜索
     * 决策表测试: 规则2(标题条件存在 + 作者条件不存在)
     */
    @DisplayName("仅标题搜索测试")
    @Test
    public void testSearchByTitleOnly() {
        // 准备测试数据
        String query = "Java";
        
        // 执行测试
        List<Book> results = bookService.listAllBooks(query);
        
        // 验证结果
        assertNotNull(results);
        assertTrue(results.size() >= 2, "应返回至少2本匹配标题的图书");
        assertTrue(results.stream().allMatch(book -> book.getName().contains("Java") 
                || book.getAuthor().contains("Java") 
                || book.getCategory().contains("Java") 
                || (book.getDescription() != null && book.getDescription().contains("Java"))));
    }
    
    /**
     * 测试用例: TC-S03 仅作者搜索
     * 决策表测试: 规则3(标题条件不存在 + 作者条件存在)
     */
    @DisplayName("仅作者搜索测试")
    @Test
    public void testSearchByAuthorOnly() {
        // 准备测试数据
        String query = "Bruce";
        
        // 执行测试
        List<Book> results = bookService.listAllBooks(query);
        
        // 验证结果
        assertNotNull(results);
        assertTrue(results.size() >= 2, "应返回至少2本匹配作者的图书");
        assertTrue(results.stream().allMatch(book -> 
            book.getName().contains("Bruce") 
            || book.getAuthor().contains("Bruce") 
            || book.getCategory().contains("Bruce")
            || (book.getDescription() != null && book.getDescription().contains("Bruce"))));
    }
    
    /**
     * 测试用例: TC-S04 无条件搜索
     * 决策表测试: 规则4(标题条件不存在 + 作者条件不存在)
     */
    @DisplayName("无条件搜索测试")
    @Test
    public void testSearchWithoutConditions() {
        // 准备测试数据
        String query = "";
        
        // 执行测试
        List<Book> results = bookService.listAllBooks(query);
        
        // 验证结果
        assertNotNull(results);
        assertTrue(results.size() >= 4, "应返回所有图书");
    }
    
    /**
     * 测试用例: TC-S05 按收藏量排序
     * 测试按收藏量从高到低排序功能
     */
    @DisplayName("收藏量排序测试")
    @Test
    public void testSortByFavoriteCount() {
        // 准备测试数据
        String query = "";
        String sortType = "desc"; // 降序排列
        
        // 执行测试
        List<Book> results = bookService.listAllBooksAndSortByFavoriteCount(query, sortType);
        
        // 验证结果
        assertNotNull(results);
        assertTrue(results.size() >= 4, "应返回所有图书");
        
        // 验证排序顺序
        for (int i = 0; i < results.size() - 1; i++) {
            assertTrue(results.get(i).getFavoriteCount() >= results.get(i + 1).getFavoriteCount(),
                    "图书应按收藏量降序排列");
        }
    }

    /**
     * 测试用例: TC-WS01 简单搜索语句覆盖
     * 白盒测试: 覆盖BookServiceImpl.listAllBooks()方法的所有语句
     */
    @DisplayName("简单搜索语句覆盖测试")
    @Test
    public void testSimpleSearchStatementCoverage() {
        // 准备测试数据
        String query = "Java";
        
        // 执行测试
        List<Book> results = bookService.listAllBooks(query);
        
        // 验证结果
        assertNotNull(results, "搜索结果不应为null");
        assertTrue(results.size() > 0, "搜索结果应包含至少一条数据");
    }
    
    /**
     * 测试用例: TC-WS03 排序功能语句覆盖
     * 白盒测试: 覆盖BookServiceImpl.listAllBooksAndSortByFavoriteCount()方法的所有语句
     */
    @DisplayName("排序功能语句覆盖测试")
    @Test
    public void testSortFunctionStatementCoverage() {
        // 准备测试数据
        String query = "Java";
        String sortType = "desc";
        
        // 执行测试
        List<Book> results = bookService.listAllBooksAndSortByFavoriteCount(query, sortType);
        
        // 验证结果
        assertNotNull(results, "排序结果不应为null");
        assertTrue(results.size() > 0, "搜索结果应包含至少一条数据");
    }
    
    /**
     * 测试用例: TC-CS01 搜索条件非空判断(标题)
     * 白盒测试: 条件覆盖 - 测试StringUtils.hasText(query)条件的true和false分支
     */
    @DisplayName("搜索条件非空判断测试")
    @Test
    public void testQueryNotEmptyCondition() {
        // 测试条件为真的情况
        String query = "Java";
        
        // 执行测试
        List<Book> results = bookService.listAllBooks(query);
        
        // 验证结果 - 应应用过滤条件
        assertNotNull(results, "搜索结果不应为null");
        assertTrue(results.size() > 0, "应返回匹配的图书");
    }
    
    /**
     * 测试用例: TC-CS02 搜索条件空判断
     * 白盒测试: 条件覆盖 - 测试StringUtils.hasText(query)条件的false分支
     */
    @DisplayName("搜索条件空判断测试")
    @Test
    public void testQueryEmptyCondition() {
        // 测试条件为假的情况
        String query = "";
        
        // 执行测试
        List<Book> results = bookService.listAllBooks(query);
        
        // 验证结果 - 应返回所有数据
        assertNotNull(results);
        assertTrue(results.size() >= 4, "空查询条件应返回所有图书");
    }
} 