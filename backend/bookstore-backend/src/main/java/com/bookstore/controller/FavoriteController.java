package com.bookstore.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bookstore.entity.Book;
import com.bookstore.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/favorites")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;
    
    // 使用固定用户ID 1
    private static final Long FIXED_USER_ID = 1L;
    
    /**
     * 获取用户收藏列表
     */
    @GetMapping
    public Map<String, Object> list() {
        // 使用固定用户ID
        Long userId = FIXED_USER_ID;
        
        List<Book> favorites = favoriteService.getAllUserFavorites(userId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "获取成功");
        
        Map<String, Object> data = new HashMap<>();
        data.put("list", favorites);
        result.put("data", data);
        
        return result;
    }
    
    /**
     * 添加收藏
     */
    @PostMapping
    public Map<String, Object> add(@RequestBody Map<String, Long> params) {
        Long bookId = params.get("bookId");
        // 使用固定用户ID
        Long userId = FIXED_USER_ID;
        
        Map<String, Object> result = new HashMap<>();
        
        // 检查是否已收藏
        if (favoriteService.isFavorite(userId, bookId)) {
            result.put("code", 400);
            result.put("message", "已收藏该图书");
            return result;
        }
        
        boolean success = favoriteService.addFavorite(userId, bookId);
        
        if (success) {
            result.put("code", 200);
            result.put("message", "收藏成功");
        } else {
            result.put("code", 500);
            result.put("message", "收藏失败");
        }
        
        return result;
    }
    
    /**
     * 取消收藏
     */
    @DeleteMapping("/{bookId}")
    public Map<String, Object> delete(@PathVariable Long bookId) {
        // 使用固定用户ID
        Long userId = FIXED_USER_ID;
        
        Map<String, Object> result = new HashMap<>();
        
        // 检查是否已收藏
        if (!favoriteService.isFavorite(userId, bookId)) {
            result.put("code", 400);
            result.put("message", "未收藏该图书");
            return result;
        }
        
        boolean success = favoriteService.removeFavorite(userId, bookId);
        
        if (success) {
            result.put("code", 200);
            result.put("message", "取消收藏成功");
        } else {
            result.put("code", 500);
            result.put("message", "取消收藏失败");
        }
        
        return result;
    }
    
    /**
     * 检查是否已收藏
     */
    @GetMapping("/check/{bookId}")
    public Map<String, Object> check(@PathVariable Long bookId) {
        // 使用固定用户ID
        Long userId = FIXED_USER_ID;
        
        boolean isFavorite = favoriteService.isFavorite(userId, bookId);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "查询成功");
        result.put("data", isFavorite);
        
        return result;
    }
} 