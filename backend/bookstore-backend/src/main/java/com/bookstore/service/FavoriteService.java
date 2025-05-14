package com.bookstore.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.bookstore.entity.Book;
import com.bookstore.entity.Favorite;

import java.util.List;

public interface FavoriteService extends IService<Favorite> {
    
    /**
     * 用户收藏图书
     * @param userId 用户ID
     * @param bookId 图书ID
     * @return 是否收藏成功
     */
    boolean addFavorite(Long userId, Long bookId);
    
    /**
     * 用户取消收藏
     * @param userId 用户ID
     * @param bookId 图书ID
     * @return 是否取消成功
     */
    boolean removeFavorite(Long userId, Long bookId);
    
    /**
     * 查询用户是否已收藏该图书
     * @param userId 用户ID
     * @param bookId 图书ID
     * @return 是否已收藏
     */
    boolean isFavorite(Long userId, Long bookId);
    
    /**
     * 获取用户收藏的图书列表(分页)
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页数量
     * @return 图书列表
     */
    IPage<Book> getUserFavorites(Long userId, int page, int size);
    
    /**
     * 获取用户收藏的所有图书
     * @param userId 用户ID
     * @return 图书列表
     */
    List<Book> getAllUserFavorites(Long userId);
} 