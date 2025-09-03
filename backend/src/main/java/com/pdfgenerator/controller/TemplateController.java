package com.pdfgenerator.controller;

import com.pdfgenerator.dto.TemplateRequest;
import com.pdfgenerator.dto.TemplateResponse;
import com.pdfgenerator.entity.TemplateVersion;
import com.pdfgenerator.enums.TemplateCategory;
import com.pdfgenerator.service.TemplateService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/templates")
@CrossOrigin(origins = "*") // TODO: Configure proper CORS
public class TemplateController {
    
    private final TemplateService templateService;
    
    @Autowired
    public TemplateController(TemplateService templateService) {
        this.templateService = templateService;
    }
    
    @PostMapping
    public ResponseEntity<TemplateResponse> createTemplate(@Valid @RequestBody TemplateRequest request) {
        TemplateResponse response = templateService.createTemplate(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<TemplateResponse> getTemplate(@PathVariable String id) {
        TemplateResponse response = templateService.getTemplate(id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<List<TemplateResponse>> getAllTemplates(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) TemplateCategory category) {
        
        List<TemplateResponse> templates;
        
        if (search != null && !search.trim().isEmpty()) {
            templates = templateService.searchTemplates(search.trim());
        } else if (category != null) {
            templates = templateService.getTemplatesByCategory(category);
        } else {
            templates = templateService.getAllTemplates();
        }
        
        return ResponseEntity.ok(templates);
    }
    
    @GetMapping("/paginated")
    public ResponseEntity<Page<TemplateResponse>> getTemplatesPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<TemplateResponse> templates = templateService.getTemplatesPaginated(pageable);
        return ResponseEntity.ok(templates);
    }
    
    @GetMapping("/category/{category}")
    public ResponseEntity<List<TemplateResponse>> getTemplatesByCategory(@PathVariable TemplateCategory category) {
        List<TemplateResponse> templates = templateService.getTemplatesByCategory(category);
        return ResponseEntity.ok(templates);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<TemplateResponse>> searchTemplates(@RequestParam String q) {
        List<TemplateResponse> templates = templateService.searchTemplates(q);
        return ResponseEntity.ok(templates);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<TemplateResponse> updateTemplate(
            @PathVariable String id,
            @Valid @RequestBody TemplateRequest request) {
        
        TemplateResponse response = templateService.updateTemplate(id, request);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTemplate(@PathVariable String id) {
        templateService.deleteTemplate(id);
        return ResponseEntity.noContent().build();
    }
    
    @DeleteMapping("/{id}/permanent")
    public ResponseEntity<Void> permanentDeleteTemplate(@PathVariable String id) {
        templateService.permanentDeleteTemplate(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{id}/versions")
    public ResponseEntity<List<TemplateVersion>> getTemplateVersions(@PathVariable String id) {
        List<TemplateVersion> versions = templateService.getTemplateVersions(id);
        return ResponseEntity.ok(versions);
    }
    
    @PostMapping("/{id}/versions/{version}/restore")
    public ResponseEntity<TemplateResponse> restoreTemplateVersion(
            @PathVariable String id,
            @PathVariable Integer version) {
        
        TemplateResponse response = templateService.restoreTemplateVersion(id, version);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}/exists")
    public ResponseEntity<Map<String, Boolean>> checkTemplateExists(@PathVariable String id) {
        boolean exists = templateService.templateExists(id);
        return ResponseEntity.ok(Map.of("exists", exists));
    }
    
    @GetMapping("/stats/count")
    public ResponseEntity<Map<String, Long>> getTemplateCount() {
        long count = templateService.getTemplateCount();
        return ResponseEntity.ok(Map.of("totalCount", count));
    }
    
    @GetMapping("/stats/count/category/{category}")
    public ResponseEntity<Map<String, Object>> getTemplateCountByCategory(@PathVariable TemplateCategory category) {
        long count = templateService.getTemplateCountByCategory(category);
        return ResponseEntity.ok(Map.of("count", count, "category", category.name()));
    }
    
    @GetMapping("/categories")
    public ResponseEntity<TemplateCategory[]> getTemplateCategories() {
        return ResponseEntity.ok(TemplateCategory.values());
    }
}