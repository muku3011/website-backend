package com.irku.blog.controller;

import com.irku.blog.dto.BlogDto;
import com.irku.blog.dto.BlogSummaryDto;
import com.irku.blog.entity.ContactRequest;
import com.irku.blog.service.BlogService;
import com.irku.blog.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@Tag(name = "Blogs", description = "Operations for browsing and managing blogs. Also, include contact endpoint")
public class BlogController {
    
    @Autowired
    private BlogService blogService;

    @Autowired
    private EmailService emailService;

    @Operation(summary = "Submit a contact request")
    @PostMapping("/contact")
    public ResponseEntity<Void> submit(@Valid @RequestBody ContactRequest request) {
        emailService.sendContactEmail(request);
        return ResponseEntity.accepted().build();
    }

    @Operation(summary = "Get all blogs")
    @GetMapping("blogs/all")
    public ResponseEntity<List<BlogSummaryDto>> getAllBlogs() {
        List<BlogSummaryDto> blogs = blogService.getAllBlogs();
        return ResponseEntity.ok(blogs);
    }

    @Operation(summary = "Get all published blogs")
    @GetMapping("/blogs")
    public ResponseEntity<List<BlogSummaryDto>> getAllPublishedBlogs() {
        List<BlogSummaryDto> blogs = blogService.getAllPublishedBlogs();
        return ResponseEntity.ok(blogs);
    }

    @Operation(summary = "Get blog statistics (total published blogs and total views)")
    @GetMapping("/blogs/stats")
    public ResponseEntity<BlogService.BlogStats> getBlogStats() {
        BlogService.BlogStats stats = blogService.getBlogStats();
        return ResponseEntity.ok(stats);
    }

    @Operation(summary = "Get published blogs (paginated)")
    @GetMapping("/blogs/page")
    public ResponseEntity<Page<BlogSummaryDto>> getBlogsWithPagination(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<BlogSummaryDto> blogs = blogService.getPublishedBlogs(page, size);
        return ResponseEntity.ok(blogs);
    }
    
    @Operation(summary = "Get blog by ID")
    @GetMapping("/blogs/{id}")
    public ResponseEntity<BlogDto> getBlogById(@PathVariable Long id) {
        Optional<BlogDto> blog = blogService.getBlogById(id);
        return blog.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get published blog by slug and increment view count")
    @GetMapping("/blogs/slug/{slug}")
    public ResponseEntity<BlogDto> getBlogBySlug(@PathVariable String slug) {
        Optional<BlogDto> blog = blogService.getPublishedBlogBySlug(slug);
        if (blog.isPresent()) {
            blogService.incrementViewCount(slug);
            return ResponseEntity.ok(blog.get());
        }
        return ResponseEntity.notFound().build();
    }
    
    @Operation(summary = "Get featured blogs")
    @GetMapping("/blogs/featured")
    public ResponseEntity<List<BlogSummaryDto>> getFeaturedBlogs() {
        List<BlogSummaryDto> blogs = blogService.getFeaturedBlogs();
        return ResponseEntity.ok(blogs);
    }
    
    @Operation(summary = "Search blogs")
    @GetMapping("/blogs/search")
    public ResponseEntity<Page<BlogSummaryDto>> searchBlogs(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<BlogSummaryDto> blogs = blogService.searchBlogs(q, page, size);
        return ResponseEntity.ok(blogs);
    }
    
    @Operation(summary = "Get recent blogs")
    @GetMapping("/blogs/recent")
    public ResponseEntity<List<BlogSummaryDto>> getRecentBlogs(
            @RequestParam(defaultValue = "5") int limit) {
        List<BlogSummaryDto> blogs = blogService.getRecentBlogs(limit);
        return ResponseEntity.ok(blogs);
    }
    
    @Operation(summary = "Get popular blogs")
    @GetMapping("/blogs/popular")
    public ResponseEntity<List<BlogSummaryDto>> getPopularBlogs(
            @RequestParam(defaultValue = "5") int limit) {
        List<BlogSummaryDto> blogs = blogService.getPopularBlogs(limit);
        return ResponseEntity.ok(blogs);
    }
    
    @Operation(summary = "Create a new blog")
    @PostMapping("/blogs")
    public ResponseEntity<BlogDto> createBlog(@RequestBody BlogDto blogDto) {
        try {
            BlogDto createdBlog = blogService.createBlog(blogDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdBlog);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @Operation(summary = "Update a blog")
    @PutMapping("/blogs/{id}")
    public ResponseEntity<BlogDto> updateBlog(@PathVariable Long id, @RequestBody BlogDto blogDto) {
        Optional<BlogDto> updatedBlog = blogService.updateBlog(id, blogDto);
        return updatedBlog.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @Operation(summary = "Delete a blog")
    @DeleteMapping("/blogs/{id}")
    public ResponseEntity<Void> deleteBlog(@PathVariable Long id) {
        boolean deleted = blogService.deleteBlog(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}