package com.irku.blog.service;

import com.irku.blog.dto.BlogDto;
import com.irku.blog.dto.BlogSummaryDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface BlogService {

    /**
     * Retrieve all blogs (any status) as lightweight summaries.
     *
     * @return list of BlogSummaryDto for all blogs
     */
    List<BlogSummaryDto> getAllBlogs();

    /**
     * Retrieve all published blogs as lightweight summaries sorted by publish date desc.
     *
     * @return list of BlogSummaryDto for published blogs
     */
    List<BlogSummaryDto> getAllPublishedBlogs();

    /**
     * Retrieve published blogs with pagination, sorted by publish date desc.
     *
     * @param page zero-based page index
     * @param size page size (items per page)
     * @return a Page of BlogSummaryDto
     */
    Page<BlogSummaryDto> getPublishedBlogs(int page, int size);

    /**
     * Find a blog by its database identifier.
     *
     * @param id blog id
     * @return Optional containing BlogDto if found, otherwise empty
     */
    Optional<BlogDto> getBlogById(Long id);

    /**
     * Find a published blog by its slug.
     *
     * @param slug unique slug
     * @return Optional containing BlogDto if found and published, otherwise empty
     */
    Optional<BlogDto> getPublishedBlogBySlug(String slug);

    /**
     * Find a blog by its slug (any status).
     *
     * @param slug unique slug
     * @return Optional containing BlogDto if found, otherwise empty
     */
    Optional<BlogDto> getBlogBySlug(String slug);

    /**
     * Retrieve featured published blogs as summaries sorted by publish date desc.
     *
     * @return list of BlogSummaryDto for featured blogs
     */
    List<BlogSummaryDto> getFeaturedBlogs();

    /**
     * Search published blogs by a free-text term with pagination.
     *
     * @param searchTerm query text to search in title/content/etc.
     * @param page       zero-based page index
     * @param size       page size (items per page)
     * @return a Page of BlogSummaryDto matching the query
     */
    Page<BlogSummaryDto> searchBlogs(String searchTerm, int page, int size);

    /**
     * Retrieve most recent published blogs within a time window.
     *
     * @param limit maximum number of items to return
     * @return list of BlogSummaryDto for recent blogs
     */
    List<BlogSummaryDto> getRecentBlogs(int limit);

    /**
     * Retrieve most popular published blogs by view count.
     *
     * @param limit maximum number of items to return
     * @return list of BlogSummaryDto for popular blogs
     */
    List<BlogSummaryDto> getPopularBlogs(int limit);

    /**
     * Create a new blog entry.
     * Implementations should ensure slug uniqueness and set timestamps appropriately.
     *
     * @param blogDto input data
     * @return created BlogDto
     */
    BlogDto createBlog(BlogDto blogDto);

    /**
     * Update an existing blog by id.
     * Implementations may update slug if title changes while preserving uniqueness.
     *
     * @param id      blog id to update
     * @param blogDto new values
     * @return Optional of updated BlogDto if blog exists, otherwise empty
     */
    Optional<BlogDto> updateBlog(Long id, BlogDto blogDto);

    /**
     * Delete a blog by id.
     *
     * @param id blog id to delete
     * @return true if a blog was deleted, false if not found
     */
    boolean deleteBlog(Long id);

    /**
     * Increment view count for a published blog identified by slug.
     * No-op if blog not found or not published.
     *
     * @param slug blog slug
     */
    void incrementViewCount(String slug);

    /**
     * Aggregate blog statistics such as total published blogs and total views.
     *
     * @return BlogStats aggregate metrics
     */
    BlogServiceImpl.BlogStats getBlogStats();
}