package com.bookstore.controller;

import com.bookstore.entity.Book;
import com.bookstore.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookService bookService;
    
    /**
     * 获取图书列表
     */
    @GetMapping
    public Map<String, Object> list(
            @RequestParam(required = false) String query) {
        
        List<Book> books = bookService.listAllBooks(query);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "获取成功");
        
        Map<String, Object> data = new HashMap<>();
        data.put("list", books);
        result.put("data", data);
        
        return result;
    }
    
    /**
     * 获取图书详情
     */
    @GetMapping("/{id}")
    public Map<String, Object> detail(@PathVariable Long id) {
        Book book = bookService.getBookDetail(id);
        
        Map<String, Object> result = new HashMap<>();
        if (book != null) {
            result.put("code", 200);
            result.put("message", "获取成功");
            result.put("data", book);
        } else {
            result.put("code", 404);
            result.put("message", "图书不存在");
        }
        
        return result;
    }
    
    /**
     * 添加图书
     */
    @PostMapping
    public Map<String, Object> add(@RequestBody Book book) {
        boolean success = bookService.save(book);
        
        Map<String, Object> result = new HashMap<>();
        if (success) {
            result.put("code", 200);
            result.put("message", "添加成功");
            result.put("data", book);
        } else {
            result.put("code", 500);
            result.put("message", "添加失败");
        }
        
        return result;
    }
    
    /**
     * 更新图书
     */
    @PutMapping("/{id}")
    public Map<String, Object> update(@PathVariable Long id, @RequestBody Book book) {
        book.setId(id);
        boolean exists = bookService.getById(id) != null;
        
        Map<String, Object> result = new HashMap<>();
        if (!exists) {
            result.put("code", 404);
            result.put("message", "图书不存在");
            return result;
        }
        
        boolean success = bookService.updateById(book);
        if (success) {
            result.put("code", 200);
            result.put("message", "更新成功");
            result.put("data", book);
        } else {
            result.put("code", 500);
            result.put("message", "更新失败");
        }
        
        return result;
    }
    
    /**
     * 删除图书
     */
    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        boolean exists = bookService.getById(id) != null;
        
        Map<String, Object> result = new HashMap<>();
        if (!exists) {
            result.put("code", 404);
            result.put("message", "图书不存在");
            return result;
        }
        
        boolean success = bookService.removeById(id);
        if (success) {
            result.put("code", 200);
            result.put("message", "删除成功");
        } else {
            result.put("code", 500);
            result.put("message", "删除失败");
        }
        
        return result;
    }

    /**
     * 获取图书列表（支持按收藏量排序）
     */
    @GetMapping("/favorite-sort")
    public Map<String, Object> listByFavoriteCount(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "desc") String sortType) {
        
        List<Book> books = bookService.listAllBooksAndSortByFavoriteCount(query, sortType);
        
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "获取成功");
        
        Map<String, Object> data = new HashMap<>();
        data.put("list", books);
        result.put("data", data);
        
        return result;
    }
} 