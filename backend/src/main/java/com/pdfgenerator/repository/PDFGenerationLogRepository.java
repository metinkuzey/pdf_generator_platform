package com.pdfgenerator.repository;

import com.pdfgenerator.entity.PDFGenerationLog;
import com.pdfgenerator.enums.PDFGenerationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PDFGenerationLogRepository extends JpaRepository<PDFGenerationLog, String> {

    /**
     * Find logs by template ID
     */
    List<PDFGenerationLog> findByTemplateIdOrderByCreatedAtDesc(String templateId);

    /**
     * Find logs by template ID with pagination
     */
    Page<PDFGenerationLog> findByTemplateIdOrderByCreatedAtDesc(String templateId, Pageable pageable);

    /**
     * Find logs by status
     */
    List<PDFGenerationLog> findByStatusOrderByCreatedAtDesc(PDFGenerationStatus status);

    /**
     * Find logs by status with pagination
     */
    Page<PDFGenerationLog> findByStatusOrderByCreatedAtDesc(PDFGenerationStatus status, Pageable pageable);

    /**
     * Find logs created by specific user
     */
    List<PDFGenerationLog> findByCreatedByOrderByCreatedAtDesc(String createdBy);

    /**
     * Find logs within date range
     */
    @Query("SELECT log FROM PDFGenerationLog log WHERE log.createdAt BETWEEN :startDate AND :endDate ORDER BY log.createdAt DESC")
    List<PDFGenerationLog> findByCreatedAtBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * Find logs within date range with pagination
     */
    @Query("SELECT log FROM PDFGenerationLog log WHERE log.createdAt BETWEEN :startDate AND :endDate ORDER BY log.createdAt DESC")
    Page<PDFGenerationLog> findByCreatedAtBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

    /**
     * Count logs by status
     */
    long countByStatus(PDFGenerationStatus status);

    /**
     * Count logs by template ID
     */
    long countByTemplateId(String templateId);

    /**
     * Count logs by template ID and status
     */
    long countByTemplateIdAndStatus(String templateId, PDFGenerationStatus status);

    /**
     * Find failed logs for retry
     */
    @Query("SELECT log FROM PDFGenerationLog log WHERE log.status = 'FAILED' AND log.createdAt > :since ORDER BY log.createdAt DESC")
    List<PDFGenerationLog> findFailedLogsSince(@Param("since") LocalDateTime since);

    /**
     * Calculate average processing time
     */
    @Query("SELECT AVG(log.processingTimeMs) FROM PDFGenerationLog log WHERE log.status = 'COMPLETED' AND log.processingTimeMs IS NOT NULL")
    Double calculateAverageProcessingTime();

    /**
     * Calculate average processing time by template
     */
    @Query("SELECT AVG(log.processingTimeMs) FROM PDFGenerationLog log WHERE log.templateId = :templateId AND log.status = 'COMPLETED' AND log.processingTimeMs IS NOT NULL")
    Double calculateAverageProcessingTimeByTemplate(@Param("templateId") String templateId);

    /**
     * Find logs with processing time above threshold
     */
    @Query("SELECT log FROM PDFGenerationLog log WHERE log.processingTimeMs > :thresholdMs ORDER BY log.processingTimeMs DESC")
    List<PDFGenerationLog> findSlowProcessingLogs(@Param("thresholdMs") Integer thresholdMs);

    /**
     * Get success rate statistics
     */
    @Query("SELECT " +
           "COUNT(CASE WHEN log.status = 'COMPLETED' THEN 1 END) as completed, " +
           "COUNT(CASE WHEN log.status = 'FAILED' THEN 1 END) as failed, " +
           "COUNT(*) as total " +
           "FROM PDFGenerationLog log WHERE log.createdAt >= :since")
    Object[] getSuccessRateStatistics(@Param("since") LocalDateTime since);

    /**
     * Search logs by multiple criteria
     */
    @Query("SELECT log FROM PDFGenerationLog log WHERE " +
           "(:templateId IS NULL OR log.templateId = :templateId) AND " +
           "(:status IS NULL OR log.status = :status) AND " +
           "(:createdBy IS NULL OR log.createdBy = :createdBy) AND " +
           "(:startDate IS NULL OR log.createdAt >= :startDate) AND " +
           "(:endDate IS NULL OR log.createdAt <= :endDate) " +
           "ORDER BY log.createdAt DESC")
    Page<PDFGenerationLog> searchLogs(
            @Param("templateId") String templateId,
            @Param("status") PDFGenerationStatus status,
            @Param("createdBy") String createdBy,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );
}