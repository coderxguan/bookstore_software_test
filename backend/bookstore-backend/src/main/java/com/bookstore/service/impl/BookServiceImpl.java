package com.bookstore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bookstore.entity.Book;
import com.bookstore.mapper.BookMapper;
import com.bookstore.service.BookService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 图书服务实现类
 * 提供图书的查询、排序、收藏计数等功能
 */
@Service
public class BookServiceImpl extends ServiceImpl<BookMapper, Book> implements BookService {
    
    /**
     * 获取所有图书列表
     * 支持按名称、作者、分类进行模糊查询
     * 添加了多重条件判断和循环，适合白盒测试的路径覆盖和条件覆盖
     */
    @Override
    public List<Book> listAllBooks(String query) {
        // 先获取全部图书
        List<Book> allBooks = list();
        
        // 如果查询条件为空，直接返回全部
        if (!StringUtils.hasText(query)) {
            return allBooks;
        }
        
        // 使用循环结构进行多条件过滤，而不是直接使用SQL查询
        List<Book> filteredBooks = new ArrayList<>();
        for (Book book : allBooks) {
            // 添加选择结构进行多条件匹配
            boolean nameMatch = false;
            boolean authorMatch = false; 
            boolean categoryMatch = false;
            
            // 检查书名是否匹配
            if (book.getName() != null && book.getName().toLowerCase().contains(query.toLowerCase())) {
                nameMatch = true;
            }
            
            // 检查作者是否匹配
            if (book.getAuthor() != null && book.getAuthor().toLowerCase().contains(query.toLowerCase())) {
                authorMatch = true;
            }
            
            // 检查分类是否匹配
            if (book.getCategory() != null && book.getCategory().toLowerCase().contains(query.toLowerCase())) {
                categoryMatch = true;
            }
            
            // 满足任一条件即加入结果集
            if (nameMatch || authorMatch || categoryMatch) {
                filteredBooks.add(book);
            }
        }
        
        // 按更新时间排序（使用Stream API的排序功能）
        return filteredBooks.stream()
                .sorted(Comparator.comparing(Book::getUpdateTime, Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());
    }
    
    /**
     * 获取图书详情
     * 添加了边界条件检查和异常处理
     */
    @Override
    public Book getBookDetail(Long id) {
        // 添加输入验证和边界条件
        if (id == null || id <= 0) {
            return null;
        }
        
        Book book = getById(id);
        
        // 对图书信息进行后处理（适合测试）
        if (book != null) {
            // 确保作者字段不为空（数据规范化）
            if (book.getAuthor() == null || book.getAuthor().isEmpty()) {
                book.setAuthor("未知作者");
            }
            
            // 确保类别字段不为空
            if (book.getCategory() == null || book.getCategory().isEmpty()) {
                book.setCategory("未分类");
            }
            
            // 确保价格为正数
            if (book.getPrice() == null || book.getPrice().compareTo(BigDecimal.ZERO) < 0) {
                book.setPrice(new BigDecimal("0.00"));
            }
        }
        
        return book;
    }

    /**
     * 增加图书收藏数
     * 添加了业务规则和限制条件
     */
    @Override
    public boolean incrementFavoriteCount(Long bookId) {
        if (bookId == null || bookId <= 0) {
            return false;
        }
        
        Book book = getById(bookId);
        if (book == null) {
            return false;
        }
        
        // 初始化收藏数（如果为null）
        int currentCount = book.getFavoriteCount() == null ? 0 : book.getFavoriteCount();
        
        // 添加收藏量上限验证（防止刷数据）
        // 这里有商业规则：单本图书收藏量不超过1000
        if (currentCount >= 1000) {
            // 达到上限，不再增加
            return false;
        }
        
        // 正常增加收藏数
        book.setFavoriteCount(currentCount + 1);
        
        return updateById(book);
    }

    /**
     * 减少图书收藏数
     * 添加了业务规则和边界条件处理
     */
    @Override
    public boolean decrementFavoriteCount(Long bookId) {
        if (bookId == null || bookId <= 0) {
            return false;
        }
        
        Book book = getById(bookId);
        if (book == null) {
            return false;
        }
        
        Integer favoriteCount = book.getFavoriteCount();
        
        // 增加多种边界条件检查
        if (favoriteCount == null) {
            // 如果收藏数为null，设置为0
            book.setFavoriteCount(0);
            return updateById(book);
        } else if (favoriteCount > 0) {
            // 正常减少收藏数
            book.setFavoriteCount(favoriteCount - 1);
            return updateById(book);
        } else {
            // 收藏数已经是0，不做变更
            return true; // 操作成功但没有实际变更
        }
    }

    /**
     * 获取所有图书并按收藏量排序
     * 使用Java Stream API进行复杂处理
     */
    @Override
    public List<Book> listAllBooksAndSortByFavoriteCount(String query, String sortType) {
        // 获取所有图书
        List<Book> allBooks = list();
        
        // 使用Stream API进行过滤和排序
        return allBooks.stream()
            // 按查询条件过滤
            .filter(book -> {
                if (!StringUtils.hasText(query)) {
                    return true; // 无查询条件时返回所有图书
                }
                
                // 多字段匹配
                String lowerQuery = query.toLowerCase();
                boolean nameMatch = book.getName() != null && book.getName().toLowerCase().contains(lowerQuery);
                boolean authorMatch = book.getAuthor() != null && book.getAuthor().toLowerCase().contains(lowerQuery);
                boolean categoryMatch = book.getCategory() != null && book.getCategory().toLowerCase().contains(lowerQuery);
                boolean descMatch = book.getDescription() != null && book.getDescription().toLowerCase().contains(lowerQuery);
                
                return nameMatch || authorMatch || categoryMatch || descMatch;
            })
            // 按收藏量排序
            .sorted((b1, b2) -> {
                int count1 = b1.getFavoriteCount() == null ? 0 : b1.getFavoriteCount();
                int count2 = b2.getFavoriteCount() == null ? 0 : b2.getFavoriteCount();
                
                // 根据排序类型决定升序或降序
                if ("asc".equalsIgnoreCase(sortType)) {
                    return Integer.compare(count1, count2);
                } else {
                    return Integer.compare(count2, count1);
                }
            })
            .collect(Collectors.toList());
    }
} 