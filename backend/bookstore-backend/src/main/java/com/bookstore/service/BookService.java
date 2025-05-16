package com.bookstore.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.bookstore.entity.Book;

import java.util.List;

/**
 * 图书服务接口
 */
public interface BookService extends IService<Book> {
    
    /**
     * 获取所有图书列表
     * @param query 查询条件
     * @return 图书列表
     */
    List<Book> listAllBooks(String query);
    
    /**
     * 获取图书详情
     * @param id 图书ID
     * @return 图书详情
     */
    Book getBookDetail(Long id);
    
    /**
     * 获取所有图书并按收藏量排序
     * @param query 搜索关键词
     * @param sortType 排序类型: 'asc'升序, 'desc'降序
     * @return 图书列表
     */
    List<Book> listAllBooksAndSortByFavoriteCount(String query, String sortType);
    
    /**
     * 增加图书收藏数
     * @param bookId 图书ID
     * @return 是否成功
     */
    boolean incrementFavoriteCount(Long bookId);
    
    /**
     * 减少图书收藏数
     * @param bookId 图书ID
     * @return 是否成功
     */
    boolean decrementFavoriteCount(Long bookId);
} 