package com.irku.blog.repository;

import com.irku.blog.entity.Blog;
import com.irku.blog.entity.BlogStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {

    // Find published blogs
    List<Blog> findByStatusOrderByPublishedAtDesc(BlogStatus status);

    // Find published blogs with pagination
    Page<Blog> findByStatusOrderByPublishedAtDesc(BlogStatus status, Pageable pageable);

    // Find featured blogs
    List<Blog> findByIsFeaturedTrueAndStatusOrderByPublishedAtDesc(BlogStatus status);

    // Find blog by slug
    Optional<Blog> findBySlugAndStatus(String slug, BlogStatus status);

    // Find the blog by slug (any status)
    Optional<Blog> findBySlug(String slug);

    // Search blogs by title or content
    @Query("SELECT b FROM Blog b WHERE b.status = :status AND " +
           "(LOWER(b.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(b.content) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(b.excerpt) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "ORDER BY b.publishedAt DESC")
    Page<Blog> searchPublishedBlogs(@Param("searchTerm") String searchTerm,
                                   @Param("status") BlogStatus status,
                                   Pageable pageable);

    // Find recent blogs
    @Query("SELECT b FROM Blog b WHERE b.status = :status AND b.publishedAt >= :since " +
           "ORDER BY b.publishedAt DESC")
    List<Blog> findRecentBlogs(@Param("status") BlogStatus status,
                              @Param("since") LocalDateTime since,
                              Pageable pageable);

    // Count blogs by status
    long countByStatus(BlogStatus status);

    // Find blogs with view count above a threshold
    @Query("SELECT b FROM Blog b WHERE b.status = :status AND b.viewCount >= :minViews " +
           "ORDER BY b.viewCount DESC")
    List<Blog> findPopularBlogs(@Param("status") BlogStatus status,
                               @Param("minViews") Long minViews,
                               Pageable pageable);

    // Check if slug exists (excluding current blog)
    @Query("SELECT COUNT(b) > 0 FROM Blog b WHERE b.slug = :slug AND b.id != :id")
    boolean existsBySlugAndIdNot(@Param("slug") String slug, @Param("id") Long id);

    // Check if a slug exists
    boolean existsBySlug(String slug);
}
