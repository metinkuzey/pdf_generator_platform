package com.pdfgenerator.repository;

import com.pdfgenerator.entity.Template;
import com.pdfgenerator.enums.TemplateCategory;
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
public interface TemplateRepository extends JpaRepository<Template, String> {

    /**
     * Find all active templates
     */
    List<Template> findByActiveTrue();

    /**
     * Find all active templates ordered by creation date descending
     */
    List<Template> findByActiveTrueOrderByCreatedAtDesc();

    /**
     * Find all active templates with pagination ordered by creation date descending
     */
    Page<Template> findByActiveTrueOrderByCreatedAtDesc(Pageable pageable);

    /**
     * Find templates by category ordered by creation date descending
     */
    List<Template> findByCategoryAndActiveTrueOrderByCreatedAtDesc(TemplateCategory category);

    /**
     * Find templates by name containing (case insensitive) ordered by creation date descending
     */
    List<Template> findByNameContainingIgnoreCaseAndActiveTrueOrderByCreatedAtDesc(String name);

    /**
     * Check if template exists by ID and active status
     */
    boolean existsByIdAndActiveTrue(String id);

    /**
     * Count all active templates
     */
    long countByActiveTrue();

    /**
     * Find templates by category
     */
    List<Template> findByCategoryAndActiveTrue(TemplateCategory category);

    /**
     * Find templates by category with pagination
     */
    Page<Template> findByCategoryAndActiveTrue(TemplateCategory category, Pageable pageable);

    /**
     * Find templates created by specific user
     */
    List<Template> findByCreatedByAndActiveTrue(String createdBy);

    /**
     * Find templates by name containing (case insensitive)
     */
    @Query("SELECT t FROM Template t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :name, '%')) AND t.active = true")
    List<Template> findByNameContainingIgnoreCaseAndActiveTrue(@Param("name") String name);

    /**
     * Find templates created within date range
     */
    @Query("SELECT t FROM Template t WHERE t.createdAt BETWEEN :startDate AND :endDate AND t.active = true")
    List<Template> findByCreatedAtBetweenAndActiveTrue(
            @Param("startDate") LocalDateTime startDate, 
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * Find template by ID and active status
     */
    Optional<Template> findByIdAndActiveTrue(String id);

    /**
     * Count templates by category
     */
    long countByCategoryAndActiveTrue(TemplateCategory category);

    /**
     * Count templates created by user
     */
    long countByCreatedByAndActiveTrue(String createdBy);

    /**
     * Find latest version of template
     */
    @Query("SELECT t FROM Template t WHERE t.id = :id AND t.active = true ORDER BY t.version DESC")
    Optional<Template> findLatestVersionById(@Param("id") String id);

    /**
     * Soft delete template (set active = false)
     */
    @Query("UPDATE Template t SET t.active = false WHERE t.id = :id")
    void softDeleteById(@Param("id") String id);

    /**
     * Find templates with pagination and sorting
     */
    Page<Template> findByActiveTrue(Pageable pageable);

    /**
     * Search templates by multiple criteria
     */
    @Query("SELECT t FROM Template t WHERE " +
           "(:category IS NULL OR t.category = :category) AND " +
           "(:createdBy IS NULL OR t.createdBy = :createdBy) AND " +
           "(:name IS NULL OR LOWER(t.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "t.active = true")
    Page<Template> searchTemplates(
            @Param("category") TemplateCategory category,
            @Param("createdBy") String createdBy,
            @Param("name") String name,
            Pageable pageable
    );
}