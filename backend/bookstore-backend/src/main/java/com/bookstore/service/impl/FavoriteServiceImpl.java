package com.bookstore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bookstore.entity.Book;
import com.bookstore.entity.Favorite;
import com.bookstore.mapper.BookMapper;
import com.bookstore.mapper.FavoriteMapper;
import com.bookstore.service.BookService;
import com.bookstore.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements FavoriteService {

    @Autowired
    private BookMapper bookMapper;
    
    @Autowired
    private BookService bookService;
    
    @Override
    @Transactional
    public boolean addFavorite(Long userId, Long bookId) {
        // 先检查是否已收藏
        if (isFavorite(userId, bookId)) {
            return false;
        }
        
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setBookId(bookId);
        
        boolean success = save(favorite);
        if (success) {
            // 增加图书收藏数
            bookService.incrementFavoriteCount(bookId);
        }
        return success;
    }
    
    @Override
    @Transactional
    public boolean removeFavorite(Long userId, Long bookId) {
        LambdaQueryWrapper<Favorite> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Favorite::getUserId, userId).eq(Favorite::getBookId, bookId);
        
        boolean success = remove(queryWrapper);
        if (success) {
            // 减少图书收藏数
            bookService.decrementFavoriteCount(bookId);
        }
        return success;
    }
    
    @Override
    public boolean isFavorite(Long userId, Long bookId) {
        // 构建查询条件
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
               .eq(Favorite::getBookId, bookId);
        
        // 统计结果
        return count(wrapper) > 0;
    }
    
    @Override
    public List<Book> getAllUserFavorites(Long userId) {
        // 查询用户所有收藏记录
        LambdaQueryWrapper<Favorite> favoriteWrapper = new LambdaQueryWrapper<>();
        favoriteWrapper.eq(Favorite::getUserId, userId)
                      .orderByDesc(Favorite::getCreateTime);
        
        List<Favorite> favorites = list(favoriteWrapper);
        
        // 如果没有收藏记录，返回空列表
        if (favorites.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 提取所有收藏的图书ID
        List<Long> bookIds = favorites.stream()
                                    .map(Favorite::getBookId)
                                    .collect(Collectors.toList());
        
        // 查询所有收藏的图书
        LambdaQueryWrapper<Book> bookWrapper = new LambdaQueryWrapper<>();
        bookWrapper.in(Book::getId, bookIds);
        List<Book> books = bookMapper.selectList(bookWrapper);
        
        // 维护原始顺序
        List<Book> orderedBooks = new ArrayList<>(books.size());
        for (Long bookId : bookIds) {
            for (Book book : books) {
                if (book.getId().equals(bookId)) {
                    orderedBooks.add(book);
                    break;
                }
            }
        }
        
        return orderedBooks;
    }
} 