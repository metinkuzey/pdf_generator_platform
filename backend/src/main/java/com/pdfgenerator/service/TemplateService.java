package com.pdfgenerator.service;

import com.pdfgenerator.dto.TemplateRequest;
import com.pdfgenerator.dto.TemplateResponse;
import com.pdfgenerator.entity.Template;
import com.pdfgenerator.entity.TemplateVersion;
import com.pdfgenerator.enums.TemplateCategory;
import com.pdfgenerator.exception.TemplateNotFoundException;
import com.pdfgenerator.repository.TemplateRepository;
import com.pdfgenerator.repository.TemplateVersionRepository;
import com.pdfgenerator.util.UUIDGenerator;
import com.pdfgenerator.validation.TemplateValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class TemplateService {
    
    private final TemplateRepository templateRepository;
    private final TemplateVersionRepository templateVersionRepository;
    private final TemplateValidator templateValidator;
    
    @Autowired
    public TemplateService(TemplateRepository templateRepository,
                          TemplateVersionRepository templateVersionRepository,
                          TemplateValidator templateValidator) {
        this.templateRepository = templateRepository;
        this.templateVersionRepository = templateVersionRepository;
        this.templateValidator = templateValidator;
    }
    
    public TemplateResponse createTemplate(TemplateRequest request) {
        // Validate request
        templateValidator.validateTemplateRequest(request);
        
        // Create new template
        Template template = new Template();
        template.setId(UUIDGenerator.generateUUID());
        template.setName(request.getName());
        template.setCategory(request.getCategory());
        template.setSchema(request.getSchema());
        template.setMetadata(request.getMetadata());
        template.setDescription(request.getDescription());
        template.setActive(true);
        template.setVersion(1);
        template.setCreatedBy("system"); // TODO: Get from security context
        
        // Save template
        Template savedTemplate = templateRepository.save(template);
        
        // Create initial version
        createTemplateVersion(savedTemplate);
        
        return convertToResponse(savedTemplate);
    }
    
    @Transactional(readOnly = true)
    public TemplateResponse getTemplate(String id) {
        Template template = templateRepository.findByIdAndActiveTrue(id)
            .orElseThrow(() -> new TemplateNotFoundException(id));
        return convertToResponse(template);
    }
    
    @Transactional(readOnly = true)
    public List<TemplateResponse> getAllTemplates() {
        List<Template> templates = templateRepository.findByActiveTrueOrderByCreatedAtDesc();
        return templates.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public Page<TemplateResponse> getTemplatesPaginated(Pageable pageable) {
        Page<Template> templates = templateRepository.findByActiveTrueOrderByCreatedAtDesc(pageable);
        return templates.map(this::convertToResponse);
    }
    
    @Transactional(readOnly = true)
    public List<TemplateResponse> getTemplatesByCategory(TemplateCategory category) {
        List<Template> templates = templateRepository.findByCategoryAndActiveTrueOrderByCreatedAtDesc(category);
        return templates.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<TemplateResponse> searchTemplates(String searchTerm) {
        List<Template> templates = templateRepository.findByNameContainingIgnoreCaseAndActiveTrueOrderByCreatedAtDesc(searchTerm);
        return templates.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }
    
    public TemplateResponse updateTemplate(String id, TemplateRequest request) {
        // Validate request
        templateValidator.validateTemplateRequest(request);
        
        // Find existing template
        Template existingTemplate = templateRepository.findByIdAndActiveTrue(id)
            .orElseThrow(() -> new TemplateNotFoundException(id));
        
        // Create version backup before update
        createTemplateVersion(existingTemplate);
        
        // Update template
        existingTemplate.setName(request.getName());
        existingTemplate.setCategory(request.getCategory());
        existingTemplate.setSchema(request.getSchema());
        existingTemplate.setMetadata(request.getMetadata());
        existingTemplate.setDescription(request.getDescription());
        existingTemplate.setVersion(existingTemplate.getVersion() + 1);
        existingTemplate.setUpdatedAt(LocalDateTime.now());
        
        Template updatedTemplate = templateRepository.save(existingTemplate);
        return convertToResponse(updatedTemplate);
    }
    
    public void deleteTemplate(String id) {
        Template template = templateRepository.findByIdAndActiveTrue(id)
            .orElseThrow(() -> new TemplateNotFoundException(id));
        
        // Soft delete
        template.setActive(false);
        template.setUpdatedAt(LocalDateTime.now());
        templateRepository.save(template);
    }
    
    public void permanentDeleteTemplate(String id) {
        Template template = templateRepository.findById(id)
            .orElseThrow(() -> new TemplateNotFoundException(id));
        
        // Delete all versions first
        templateVersionRepository.deleteByTemplateId(id);
        
        // Delete template
        templateRepository.delete(template);
    }
    
    @Transactional(readOnly = true)
    public List<TemplateVersion> getTemplateVersions(String templateId) {
        // Verify template exists
        templateRepository.findByIdAndActiveTrue(templateId)
            .orElseThrow(() -> new TemplateNotFoundException(templateId));
        
        return templateVersionRepository.findByTemplateIdOrderByVersionDesc(templateId);
    }
    
    public TemplateResponse restoreTemplateVersion(String templateId, Integer version) {
        // Find template
        Template template = templateRepository.findByIdAndActiveTrue(templateId)
            .orElseThrow(() -> new TemplateNotFoundException(templateId));
        
        // Find version to restore
        TemplateVersion templateVersion = templateVersionRepository
            .findByTemplateIdAndVersion(templateId, version)
            .orElseThrow(() -> new RuntimeException("Template version not found: " + version));
        
        // Create backup of current version
        createTemplateVersion(template);
        
        // Restore from version
        template.setSchema(templateVersion.getSchema());
        template.setVersion(template.getVersion() + 1);
        template.setUpdatedAt(LocalDateTime.now());
        
        Template restoredTemplate = templateRepository.save(template);
        return convertToResponse(restoredTemplate);
    }
    
    @Transactional(readOnly = true)
    public boolean templateExists(String id) {
        return templateRepository.existsByIdAndActiveTrue(id);
    }
    
    @Transactional(readOnly = true)
    public long getTemplateCount() {
        return templateRepository.countByActiveTrue();
    }
    
    @Transactional(readOnly = true)
    public long getTemplateCountByCategory(TemplateCategory category) {
        return templateRepository.countByCategoryAndActiveTrue(category);
    }
    
    private void createTemplateVersion(Template template) {
        TemplateVersion version = new TemplateVersion();
        version.setId(UUIDGenerator.generateUUID());
        version.setTemplateId(template.getId());
        version.setVersion(template.getVersion());
        version.setSchema(template.getSchema());
        version.setCreatedBy(template.getCreatedBy());
        
        templateVersionRepository.save(version);
    }
    
    private TemplateResponse convertToResponse(Template template) {
        TemplateResponse response = new TemplateResponse();
        response.setId(template.getId());
        response.setName(template.getName());
        response.setCategory(template.getCategory());
        response.setSchema(template.getSchema());
        response.setMetadata(template.getMetadata());
        response.setDescription(template.getDescription());
        response.setCreatedAt(template.getCreatedAt());
        response.setUpdatedAt(template.getUpdatedAt());
        response.setCreatedBy(template.getCreatedBy());
        response.setActive(template.isActive());
        response.setVersion(template.getVersion());
        
        return response;
    }
}