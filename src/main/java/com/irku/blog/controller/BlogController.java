package com.irku.blog.controller;

import com.irku.blog.dto.BlogDto;
import com.irku.blog.dto.BlogSummaryDto;
import com.irku.blog.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/blogs")
@CrossOrigin(origins = "*") // Allow CORS for the frontend
public class BlogController {
    
    @Autowired
    private BlogService blogService;
    
    // Get all published blogs
    @GetMapping
    public ResponseEntity<List<BlogSummaryDto>> getAllBlogs() {
        List<BlogSummaryDto> blogs = blogService.getAllPublishedBlogs();
        return ResponseEntity.ok(blogs);
    }
    
    // Get published blogs with pagination
    @GetMapping("/page")
    public ResponseEntity<Page<BlogSummaryDto>> getBlogsWithPagination(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<BlogSummaryDto> blogs = blogService.getPublishedBlogs(page, size);
        return ResponseEntity.ok(blogs);
    }
    
    // Get blog by ID
    @GetMapping("/{id}")
    public ResponseEntity<BlogDto> getBlogById(@PathVariable Long id) {
        Optional<BlogDto> blog = blogService.getBlogById(id);
        return blog.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Get published blog by slug
    @GetMapping("/slug/{slug}")
    public ResponseEntity<BlogDto> getBlogBySlug(@PathVariable String slug) {
        Optional<BlogDto> blog = blogService.getPublishedBlogBySlug(slug);
        if (blog.isPresent()) {
            // Increment view count
            blogService.incrementViewCount(slug);
            return ResponseEntity.ok(blog.get());
        }
        return ResponseEntity.notFound().build();
    }
    
    // Get featured blogs
    @GetMapping("/featured")
    public ResponseEntity<List<BlogSummaryDto>> getFeaturedBlogs() {
        List<BlogSummaryDto> blogs = blogService.getFeaturedBlogs();
        return ResponseEntity.ok(blogs);
    }
    
    // Search blogs
    @GetMapping("/search")
    public ResponseEntity<Page<BlogSummaryDto>> searchBlogs(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<BlogSummaryDto> blogs = blogService.searchBlogs(q, page, size);
        return ResponseEntity.ok(blogs);
    }
    
    // Get recent blogs
    @GetMapping("/recent")
    public ResponseEntity<List<BlogSummaryDto>> getRecentBlogs(
            @RequestParam(defaultValue = "5") int limit) {
        List<BlogSummaryDto> blogs = blogService.getRecentBlogs(limit);
        return ResponseEntity.ok(blogs);
    }
    
    // Get popular blogs
    @GetMapping("/popular")
    public ResponseEntity<List<BlogSummaryDto>> getPopularBlogs(
            @RequestParam(defaultValue = "5") int limit) {
        List<BlogSummaryDto> blogs = blogService.getPopularBlogs(limit);
        return ResponseEntity.ok(blogs);
    }
    
    // Get blog statistics
    @GetMapping("/stats")
    public ResponseEntity<BlogService.BlogStats> getBlogStats() {
        BlogService.BlogStats stats = blogService.getBlogStats();
        return ResponseEntity.ok(stats);
    }
    
    // Create new blog (Admin endpoint)
    @PostMapping
    public ResponseEntity<BlogDto> createBlog(@RequestBody BlogDto blogDto) {
        try {
            BlogDto createdBlog = blogService.createBlog(blogDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdBlog);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Update blog (Admin endpoint)
    @PutMapping("/{id}")
    public ResponseEntity<BlogDto> updateBlog(@PathVariable Long id, @RequestBody BlogDto blogDto) {
        Optional<BlogDto> updatedBlog = blogService.updateBlog(id, blogDto);
        return updatedBlog.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Delete blog (Admin endpoint)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBlog(@PathVariable Long id) {
        boolean deleted = blogService.deleteBlog(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
