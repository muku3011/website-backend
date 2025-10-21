package com.irku.blog.dto;

import com.irku.blog.entity.Blog;
import com.irku.blog.entity.BlogStatus;

import java.time.LocalDateTime;

public class BlogSummaryDto {
    private Long id;
    private String title;
    private String excerpt;
    private String author;
    private String featuredImageUrl;
    private String slug;
    private BlogStatus status;
    private Long viewCount;
    private Boolean isFeatured;
    private LocalDateTime publishedAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public BlogSummaryDto() {}
    
    public BlogSummaryDto(Blog blog) {
        this.id = blog.getId();
        this.title = blog.getTitle();
        this.excerpt = blog.getExcerpt();
        this.author = blog.getAuthor();
        this.featuredImageUrl = blog.getFeaturedImageUrl();
        this.slug = blog.getSlug();
        this.status = blog.getStatus();
        this.viewCount = blog.getViewCount();
        this.isFeatured = blog.getIsFeatured();
        this.publishedAt = blog.getPublishedAt();
        this.updatedAt = blog.getUpdatedAt();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getExcerpt() {
        return excerpt;
    }
    
    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }
    
    public String getFeaturedImageUrl() {
        return featuredImageUrl;
    }
    
    public void setFeaturedImageUrl(String featuredImageUrl) {
        this.featuredImageUrl = featuredImageUrl;
    }
    
    public String getSlug() {
        return slug;
    }
    
    public void setSlug(String slug) {
        this.slug = slug;
    }
    
    public BlogStatus getStatus() {
        return status;
    }
    
    public void setStatus(BlogStatus status) {
        this.status = status;
    }
    
    public Long getViewCount() {
        return viewCount;
    }
    
    public void setViewCount(Long viewCount) {
        this.viewCount = viewCount;
    }
    
    public Boolean getIsFeatured() {
        return isFeatured;
    }
    
    public void setIsFeatured(Boolean isFeatured) {
        this.isFeatured = isFeatured;
    }
    
    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }
    
    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
