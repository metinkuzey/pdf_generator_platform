package com.pdfgenerator.repository;

import com.pdfgenerator.entity.TemplateVersion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TemplateVersionRepository extends JpaRepository<TemplateVersion, String> {

    /**
     * Find all versions of a template ordered by version number
     */
    List<TemplateVersion> findByTemplateIdOrderByVersionDesc(String templateId);

    /**
     * Find all versions of a template with pagination
     */
    Page<TemplateVersion> findByTemplateIdOrderByVersionDesc(String templateId, Pageable pageable);

    /**
     * Find specific version of a template
     */
    Optional<TemplateVersion> findByTemplateIdAndVersion(String templateId, Integer version);

    /**
     * Find latest version of a template
     */
    @Query("SELECT tv FROM TemplateVersion tv WHERE tv.templateId = :templateId ORDER BY tv.version DESC LIMIT 1")
    Optional<TemplateVersion> findLatestVersionByTemplateId(@Param("templateId") String templateId);

    /**
     * Get maximum version number for a template
     */
    @Query("SELECT MAX(tv.version) FROM TemplateVersion tv WHERE tv.templateId = :templateId")
    Optional<Integer> findMaxVersionByTemplateId(@Param("templateId") String templateId);

    /**
     * Count versions for a template
     */
    long countByTemplateId(String templateId);

    /**
     * Find versions created by specific user
     */
    List<TemplateVersion> findByCreatedByOrderByCreatedAtDesc(String createdBy);

    /**
     * Delete all versions of a template
     */
    void deleteByTemplateId(String templateId);

    /**
     * Find versions within a range for a template
     */
    @Query("SELECT tv FROM TemplateVersion tv WHERE tv.templateId = :templateId AND tv.version BETWEEN :minVersion AND :maxVersion ORDER BY tv.version DESC")
    List<TemplateVersion> findVersionsInRange(
            @Param("templateId") String templateId,
            @Param("minVersion") Integer minVersion,
            @Param("maxVersion") Integer maxVersion
    );
}