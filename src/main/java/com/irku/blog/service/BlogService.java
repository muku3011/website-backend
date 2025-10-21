package com.irku.blog.service;

import com.irku.blog.dto.BlogDto;
import com.irku.blog.dto.BlogSummaryDto;
import com.irku.blog.entity.Blog;
import com.irku.blog.entity.BlogStatus;
import com.irku.blog.repository.BlogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class BlogService {

    @Autowired
    private BlogRepository blogRepository;

    // Get all published blogs
    @Transactional(readOnly = true)
    public List<BlogSummaryDto> getAllBlogs() {
        return blogRepository.findAll()
                .stream()
                .map(BlogSummaryDto::new)
                .collect(Collectors.toList());
    }

    // Get all published blogs
    @Transactional(readOnly = true)
    public List<BlogSummaryDto> getAllPublishedBlogs() {
        return blogRepository.findByStatusOrderByPublishedAtDesc(BlogStatus.PUBLISHED)
                .stream()
                .map(BlogSummaryDto::new)
                .collect(Collectors.toList());
    }

    // Get published blogs with pagination
    @Transactional(readOnly = true)
    public Page<BlogSummaryDto> getPublishedBlogs(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("publishedAt").descending());
        return blogRepository.findByStatusOrderByPublishedAtDesc(BlogStatus.PUBLISHED, pageable)
                .map(BlogSummaryDto::new);
    }

    // Get blog by ID
    @Transactional(readOnly = true)
    public Optional<BlogDto> getBlogById(Long id) {
        return blogRepository.findById(id)
                .map(BlogDto::new);
    }

    // Get published blog by slug
    @Transactional(readOnly = true)
    public Optional<BlogDto> getPublishedBlogBySlug(String slug) {
        return blogRepository.findBySlugAndStatus(slug, BlogStatus.PUBLISHED)
                .map(BlogDto::new);
    }

    // Get blog by slug (any status)
    @Transactional(readOnly = true)
    public Optional<BlogDto> getBlogBySlug(String slug) {
        return blogRepository.findBySlug(slug)
                .map(BlogDto::new);
    }

    // Get featured blogs
    @Transactional(readOnly = true)
    public List<BlogSummaryDto> getFeaturedBlogs() {
        return blogRepository.findByIsFeaturedTrueAndStatusOrderByPublishedAtDesc(BlogStatus.PUBLISHED)
                .stream()
                .map(BlogSummaryDto::new)
                .collect(Collectors.toList());
    }

    // Search blogs
    @Transactional(readOnly = true)
    public Page<BlogSummaryDto> searchBlogs(String searchTerm, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("publishedAt").descending());
        return blogRepository.searchPublishedBlogs(searchTerm, BlogStatus.PUBLISHED, pageable)
                .map(BlogSummaryDto::new);
    }

    // Get recent blogs
    @Transactional(readOnly = true)
    public List<BlogSummaryDto> getRecentBlogs(int limit) {
        LocalDateTime since = LocalDateTime.now().minusMonths(3); // Last 3 months
        Pageable pageable = PageRequest.of(0, limit, Sort.by("publishedAt").descending());
        return blogRepository.findRecentBlogs(BlogStatus.PUBLISHED, since, pageable)
                .stream()
                .map(BlogSummaryDto::new)
                .collect(Collectors.toList());
    }

    // Get popular blogs
    @Transactional(readOnly = true)
    public List<BlogSummaryDto> getPopularBlogs(int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by("viewCount").descending());
        return blogRepository.findPopularBlogs(BlogStatus.PUBLISHED, 10L, pageable)
                .stream()
                .map(BlogSummaryDto::new)
                .collect(Collectors.toList());
    }

    // Create new blog
    public BlogDto createBlog(BlogDto blogDto) {
        Blog blog = new Blog();
        blog.setTitle(blogDto.getTitle());
        blog.setContent(blogDto.getContent());
        blog.setExcerpt(blogDto.getExcerpt());
        blog.setAuthor(blogDto.getAuthor() != null ? blogDto.getAuthor() : "Mukesh Joshi");
        blog.setFeaturedImageUrl(blogDto.getFeaturedImageUrl());
        blog.setStatus(blogDto.getStatus() != null ? blogDto.getStatus() : BlogStatus.DRAFT);
        blog.setIsFeatured(blogDto.getIsFeatured() != null ? blogDto.getIsFeatured() : false);
        blog.setCreatedAt(LocalDateTime.now());
        if (blogDto.getStatus().equals(BlogStatus.PUBLISHED)) {
            blog.setPublishedAt(LocalDateTime.now());
        }

        // Ensure unique slug
        String baseSlug = blog.getSlug();
        String slug = baseSlug;
        int counter = 1;
        while (blogRepository.existsBySlug(slug)) {
            slug = baseSlug + "-" + counter;
            counter++;
        }
        blog.setSlug(slug);

        Blog savedBlog = blogRepository.save(blog);
        return new BlogDto(savedBlog);
    }

    // Update blog
    public Optional<BlogDto> updateBlog(Long id, BlogDto blogDto) {
        return blogRepository.findById(id)
                .map(blog -> {
                    blog.setTitle(blogDto.getTitle());
                    blog.setContent(blogDto.getContent());
                    blog.setExcerpt(blogDto.getExcerpt());
                    blog.setAuthor(blogDto.getAuthor());
                    blog.setFeaturedImageUrl(blogDto.getFeaturedImageUrl());
                    blog.setStatus(blogDto.getStatus());
                    blog.setIsFeatured(blogDto.getIsFeatured());
                    blog.setUpdatedAt(LocalDateTime.now());
                    if (blogDto.getStatus().equals(BlogStatus.PUBLISHED)) {
                        blog.setPublishedAt(LocalDateTime.now());
                    }

                    // Update slug if title changed
                    String newSlug = blog.generateSlug(blogDto.getTitle());
                    if (!newSlug.equals(blog.getSlug())) {
                        String slug = newSlug;
                        int counter = 1;
                        while (blogRepository.existsBySlugAndIdNot(slug, id)) {
                            slug = newSlug + "-" + counter;
                            counter++;
                        }
                        blog.setSlug(slug);
                    }

                    Blog savedBlog = blogRepository.save(blog);
                    return new BlogDto(savedBlog);
                });
    }

    // Delete blog
    public boolean deleteBlog(Long id) {
        if (blogRepository.existsById(id)) {
            blogRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Increment view count
    public void incrementViewCount(String slug) {
        blogRepository.findBySlugAndStatus(slug, BlogStatus.PUBLISHED)
                .ifPresent(Blog::incrementViewCount);
    }


    // Get blog statistics
    @Transactional(readOnly = true)
    public BlogStats getBlogStats() {
        long totalBlogs = blogRepository.countByStatus(BlogStatus.PUBLISHED);
        long totalViews = blogRepository.findByStatusOrderByPublishedAtDesc(BlogStatus.PUBLISHED)
                .stream()
                .mapToLong(blog -> blog.getViewCount() != null ? blog.getViewCount() : 0L)
                .sum();

        return new BlogStats(totalBlogs, totalViews);
    }

    // Inner class for blog statistics
    public static class BlogStats {
        private final long totalBlogs;
        private final long totalViews;

        public BlogStats(long totalBlogs, long totalViews) {
            this.totalBlogs = totalBlogs;
            this.totalViews = totalViews;
        }

        public long getTotalBlogs() {
            return totalBlogs;
        }

        public long getTotalViews() {
            return totalViews;
        }
    }
}
