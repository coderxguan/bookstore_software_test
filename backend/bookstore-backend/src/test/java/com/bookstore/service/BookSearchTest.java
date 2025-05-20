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

    // ==================== 白盒测试（语句/判定/条件/路径/循环） ====================

    // ----------- 选择结构 decrementFavoriteCount 相关 -----------

    /** TCS-SC01: 参数无效（bookId=null） */
    @DisplayName("TCS-SC01: 参数无效（bookId=null）")
    @Test
    public void testDecrementFavoriteCount_TCS_SC01_paramNull() {
        boolean result = bookService.decrementFavoriteCount(null);
        assertFalse(result, "bookId为null时应返回false");
    }

    /** TCS-SC02: bookId<=0 */
    @DisplayName("TCS-SC02: bookId<=0")
    @Test
    public void testDecrementFavoriteCount_TCS_SC02_idLEZero() {
        assertFalse(bookService.decrementFavoriteCount(0L));
        assertFalse(bookService.decrementFavoriteCount(-1L));
    }

    /** TCS-SC03: 图书不存在（bookId=99999） */
    @DisplayName("TCS-SC03: 图书不存在（bookId=99999）")
    @Test
    public void testDecrementFavoriteCount_TCS_SC03_bookNotExist() {
        boolean result = bookService.decrementFavoriteCount(99999L);
        assertFalse(result, "图书不存在时应返回false");
    }

    /** TCS-SC04: 收藏数为null */
    @DisplayName("TCS-SC04: 收藏数为null")
    @Test
    public void testDecrementFavoriteCount_TCS_SC04_favoriteCountNull() {
        Book book = new Book();
        book.setName("收藏数为null");
        book.setAuthor("作者");
        book.setCategory("测试");
        book.setPrice(new BigDecimal("10.0"));
        book.setFavoriteCount(null);
        bookService.save(book);
        testBooks.add(book);
        boolean result = bookService.decrementFavoriteCount(book.getId());
        assertTrue(result, "收藏数为null时应返回true");
        Book updated = bookService.getById(book.getId());
        assertEquals(0, updated.getFavoriteCount(), "收藏数应被设置为0");
    }

    /** TCS-SC05: 收藏数大于0 */
    @DisplayName("TCS-SC05: 收藏数大于0")
    @Test
    public void testDecrementFavoriteCount_TCS_SC05_favoriteCountGT0() {
        Book book = new Book();
        book.setName("收藏数大于0");
        book.setAuthor("作者");
        book.setCategory("测试");
        book.setPrice(new BigDecimal("10.0"));
        book.setFavoriteCount(5);
        bookService.save(book);
        testBooks.add(book);
        boolean result = bookService.decrementFavoriteCount(book.getId());
        assertTrue(result, "收藏数大于0时应返回true");
        Book updated = bookService.getById(book.getId());
        assertEquals(4, updated.getFavoriteCount(), "收藏数应减少1");
    }

    /** TCS-SC06: 收藏数为0 */
    @DisplayName("TCS-SC06: 收藏数为0")
    @Test
    public void testDecrementFavoriteCount_TCS_SC06_favoriteCountZero() {
        Book book = new Book();
        book.setName("收藏数为0");
        book.setAuthor("作者");
        book.setCategory("测试");
        book.setPrice(new BigDecimal("10.0"));
        book.setFavoriteCount(0);
        bookService.save(book);
        testBooks.add(book);
        boolean result = bookService.decrementFavoriteCount(book.getId());
        assertTrue(result, "收藏数为0时应返回true");
        Book updated = bookService.getById(book.getId());
        assertEquals(0, updated.getFavoriteCount(), "收藏数应保持为0");
    }

    // ----------- 判定覆盖/条件覆盖/路径覆盖 -----------

    /** TCS-DC01: book==null 判定为true */
    @DisplayName("TCS-DC01: book==null 判定为true")
    @Test
    public void testDecrementFavoriteCount_TCS_DC01_bookNull() {
        assertFalse(bookService.decrementFavoriteCount(99999L));
    }

    /** TCS-DC02: book!=null 判定为true */
    @DisplayName("TCS-DC02: book!=null 判定为true")
    @Test
    public void testDecrementFavoriteCount_TCS_DC02_bookNotNull() {
        Book book = new Book();
        book.setName("判定覆盖测试");
        book.setAuthor("作者");
        book.setCategory("测试");
        book.setPrice(new BigDecimal("10.0"));
        book.setFavoriteCount(1);
        bookService.save(book);
        testBooks.add(book);
        assertTrue(bookService.decrementFavoriteCount(book.getId()));
    }

    /** TCS-DC03: favoriteCount==null 判定为true */
    @DisplayName("TCS-DC03: favoriteCount==null 判定为true")
    @Test
    public void testDecrementFavoriteCount_TCS_DC03_favoriteCountNull() {
        Book book = new Book();
        book.setName("判定覆盖测试");
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

    /** TCS-DC04: favoriteCount==null 判定为false */
    @DisplayName("TCS-DC04: favoriteCount==null 判定为false")
    @Test
    public void testDecrementFavoriteCount_TCS_DC04_favoriteCountNotNull() {
        Book book = new Book();
        book.setName("判定覆盖测试");
        book.setAuthor("作者");
        book.setCategory("测试");
        book.setPrice(new BigDecimal("10.0"));
        book.setFavoriteCount(2);
        bookService.save(book);
        testBooks.add(book);
        assertTrue(bookService.decrementFavoriteCount(book.getId()));
        Book updated = bookService.getById(book.getId());
        assertEquals(1, updated.getFavoriteCount());
    }

    /** TCS-DC05: favoriteCount>0 判定为true */
    @DisplayName("TCS-DC05: favoriteCount>0 判定为true")
    @Test
    public void testDecrementFavoriteCount_TCS_DC05_favoriteCountGT0() {
        Book book = new Book();
        book.setName("判定覆盖测试");
        book.setAuthor("作者");
        book.setCategory("测试");
        book.setPrice(new BigDecimal("10.0"));
        book.setFavoriteCount(3);
        bookService.save(book);
        testBooks.add(book);
        assertTrue(bookService.decrementFavoriteCount(book.getId()));
        Book updated = bookService.getById(book.getId());
        assertEquals(2, updated.getFavoriteCount());
    }

    /** TCS-DC06: favoriteCount>0 判定为false */
    @DisplayName("TCS-DC06: favoriteCount>0 判定为false")
    @Test
    public void testDecrementFavoriteCount_TCS_DC06_favoriteCountNotGT0() {
        Book book = new Book();
        book.setName("判定覆盖测试");
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

    // ----------- 循环结构 listAllBooksAndSortByFavoriteCount -----------

    /** TCS-LT01: 空列表测试 */
    @DisplayName("TCS-LT01: 空列表测试")
    @Test
    public void testListAllBooksAndSortByFavoriteCount_TCS_LT01_emptyList() {
        // 假设数据库已清空或无数据时
        List<Book> result = bookService.listAllBooksAndSortByFavoriteCount("任意", "desc");
        assertNotNull(result);
        // 只要不抛异常即可
    }

    /** TCS-LT02: 单元素测试 */
    @DisplayName("TCS-LT02: 单元素测试")
    @Test
    public void testListAllBooksAndSortByFavoriteCount_TCS_LT02_singleElement() {
        Book book = new Book();
        book.setName("唯一书");
        book.setAuthor("唯一作者");
        book.setCategory("唯一分类");
        book.setPrice(new BigDecimal("10.0"));
        book.setFavoriteCount(1);
        bookService.save(book);
        testBooks.add(book);
        List<Book> result = bookService.listAllBooksAndSortByFavoriteCount("唯一", "desc");
        assertNotNull(result);
        assertTrue(result.stream().anyMatch(b -> b.getName().equals("唯一书")));
    }

    /** TCS-LT03: 全部匹配测试 */
    @DisplayName("TCS-LT03: 全部匹配测试")
    @Test
    public void testListAllBooksAndSortByFavoriteCount_TCS_LT03_allMatch() {
        Book book1 = new Book();
        book1.setName("Java基础");
        book1.setAuthor("张三");
        book1.setCategory("编程");
        book1.setPrice(new BigDecimal("10.0"));
        book1.setFavoriteCount(1);
        bookService.save(book1);
        testBooks.add(book1);

        Book book2 = new Book();
        book2.setName("Java进阶");
        book2.setAuthor("李四");
        book2.setCategory("编程");
        book2.setPrice(new BigDecimal("12.0"));
        book2.setFavoriteCount(2);
        bookService.save(book2);
        testBooks.add(book2);

        List<Book> result = bookService.listAllBooksAndSortByFavoriteCount("Java", "desc");
        assertNotNull(result);
        assertTrue(result.stream().anyMatch(b -> b.getName().equals("Java基础")));
        assertTrue(result.stream().anyMatch(b -> b.getName().equals("Java进阶")));
    }

    /** TCS-LT04: 部分匹配测试 */
    @DisplayName("TCS-LT04: 部分匹配测试")
    @Test
    public void testListAllBooksAndSortByFavoriteCount_TCS_LT04_partialMatch() {
        Book book1 = new Book();
        book1.setName("Java基础");
        book1.setAuthor("张三");
        book1.setCategory("编程");
        book1.setPrice(new BigDecimal("10.0"));
        book1.setFavoriteCount(1);
        bookService.save(book1);
        testBooks.add(book1);

        Book book2 = new Book();
        book2.setName("Python入门");
        book2.setAuthor("李四");
        book2.setCategory("编程");
        book2.setPrice(new BigDecimal("12.0"));
        book2.setFavoriteCount(2);
        bookService.save(book2);
        testBooks.add(book2);

        List<Book> result = bookService.listAllBooksAndSortByFavoriteCount("Java", "desc");
        assertNotNull(result);
        assertTrue(result.stream().anyMatch(b -> b.getName().contains("Java")));
        assertFalse(result.stream().anyMatch(b -> b.getName().contains("Python")));
    }

    /** TCS-LT05: 无匹配测试 */
    @DisplayName("TCS-LT05: 无匹配测试")
    @Test
    public void testListAllBooksAndSortByFavoriteCount_TCS_LT05_noMatch() {
        Book book1 = new Book();
        book1.setName("Java基础");
        book1.setAuthor("张三");
        book1.setCategory("编程");
        book1.setPrice(new BigDecimal("10.0"));
        book1.setFavoriteCount(1);
        bookService.save(book1);
        testBooks.add(book1);

        Book book2 = new Book();
        book2.setName("Python入门");
        book2.setAuthor("李四");
        book2.setCategory("编程");
        book2.setPrice(new BigDecimal("12.0"));
        book2.setFavoriteCount(2);
        bookService.save(book2);
        testBooks.add(book2);

        List<Book> result = bookService.listAllBooksAndSortByFavoriteCount("C++", "desc");
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /** TCS-LT06: null值处理测试 */
    @DisplayName("TCS-LT06: null值处理测试")
    @Test
    public void testListAllBooksAndSortByFavoriteCount_TCS_LT06_nullField() {
        Book book = new Book();
        book.setName(null);
        book.setAuthor(null);
        book.setCategory(null);
        book.setPrice(new BigDecimal("10.0"));
        book.setFavoriteCount(1);
        bookService.save(book);
        testBooks.add(book);
        List<Book> result = bookService.listAllBooksAndSortByFavoriteCount("", "desc");
        assertNotNull(result);
        // 只要不抛异常即可
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