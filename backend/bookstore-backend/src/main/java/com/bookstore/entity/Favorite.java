package com.bookstore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("favorite")
public class Favorite {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private Long bookId;
    
    private LocalDateTime createTime;
    
    // 构造函数方便创建收藏记录
    public Favorite() {
    }
    
    public Favorite(Long userId, Long bookId) {
        this.userId = userId;
        this.bookId = bookId;
        this.createTime = LocalDateTime.now();
    }
} 