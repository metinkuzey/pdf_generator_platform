package com.pdfgenerator.service;

import com.pdfgenerator.dto.TemplateRequest;
import com.pdfgenerator.dto.TemplateResponse;
import com.pdfgenerator.entity.Template;
import com.pdfgenerator.entity.TemplateVersion;
import com.pdfgenerator.enums.TemplateCategory;
import com.pdfgenerator.exception.TemplateNotFoundException;
import com.pdfgenerator.exception.ValidationException;
import com.pdfgenerator.repository.TemplateRepository;
import com.pdfgenerator.repository.TemplateVersionRepository;
import com.pdfgenerator.validation.TemplateValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TemplateServiceTest {
    
    @Mock
    private TemplateRepository templateRepository;
    
    @Mock
    private TemplateVersionRepository templateVersionRepository;
    
    @Mock
    private TemplateValidator templateValidator;
    
    @InjectMocks
    private TemplateService templateService;
    
    private TemplateRequest validRequest;
    private Template sampleTemplate;
    private Map<String, Object> sampleSchema;
    
    @BeforeEach
    void setUp() {
        // Sample schema
        sampleSchema = new HashMap<>();
        sampleSchema.put("layout", Map.of("pageSize", "A4", "orientation", "PORTRAIT"));
        sampleSchema.put("elements", List.of(
            Map.of("id", "title", "type", "TEXT", "position", Map.of("x", 0, "y", 0, "width", 100, "height", 10))
        ));
        
        // Valid request
        validRequest = new TemplateRequest();
        validRequest.setName("Test Template");
        validRequest.setCategory(TemplateCategory.CREDIT_CARD_STATEMENT);
        validRequest.setSchema(sampleSchema);
        validRequest.setDescription("Test description");
        
        // Sample template
        sampleTemplate = new Template();
        sampleTemplate.setId("test-id");
        sampleTemplate.setName("Test Template");
        sampleTemplate.setCategory(TemplateCategory.CREDIT_CARD_STATEMENT);
        sampleTemplate.setSchema(sampleSchema);
        sampleTemplate.setDescription("Test description");
        sampleTemplate.setActive(true);
        sampleTemplate.setVersion(1);
        sampleTemplate.setCreatedAt(LocalDateTime.now());
        sampleTemplate.setCreatedBy("system");
    }
    
    @Test
    void createTemplate_WithValidRequest_ShouldReturnTemplateResponse() {
        // Given
        doNothing().when(templateValidator).validateTemplateRequest(validRequest);
        when(templateRepository.save(any(Template.class))).thenReturn(sampleTemplate);
        when(templateVersionRepository.save(any(TemplateVersion.class))).thenReturn(new TemplateVersion());
        
        // When
        TemplateResponse response = templateService.createTemplate(validRequest);
        
        // Then
        assertNotNull(response);
        assertEquals("Test Template", response.getName());
        assertEquals(TemplateCategory.CREDIT_CARD_STATEMENT, response.getCategory());
        assertEquals(sampleSchema, response.getSchema());
        assertTrue(response.isActive());
        assertEquals(1, response.getVersion());
        
        verify(templateValidator).validateTemplateRequest(validRequest);
        verify(templateRepository).save(any(Template.class));
        verify(templateVersionRepository).save(any(TemplateVersion.class));
    }
    
    @Test
    void createTemplate_WithInvalidRequest_ShouldThrowValidationException() {
        // Given
        doThrow(new ValidationException("Validation failed", List.of("Name is required")))
            .when(templateValidator).validateTemplateRequest(validRequest);
        
        // When & Then
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> templateService.createTemplate(validRequest));
        
        assertEquals("Validation failed", exception.getMessage());
        assertEquals(1, exception.getErrors().size());
        assertEquals("Name is required", exception.getErrors().get(0));
        
        verify(templateValidator).validateTemplateRequest(validRequest);
        verify(templateRepository, never()).save(any(Template.class));
    }
    
    @Test
    void getTemplate_WithExistingId_ShouldReturnTemplate() {
        // Given
        when(templateRepository.findByIdAndActiveTrue("test-id")).thenReturn(Optional.of(sampleTemplate));
        
        // When
        TemplateResponse response = templateService.getTemplate("test-id");
        
        // Then
        assertNotNull(response);
        assertEquals("test-id", response.getId());
        assertEquals("Test Template", response.getName());
        
        verify(templateRepository).findByIdAndActiveTrue("test-id");
    }
    
    @Test
    void getTemplate_WithNonExistingId_ShouldThrowNotFoundException() {
        // Given
        when(templateRepository.findByIdAndActiveTrue("non-existing")).thenReturn(Optional.empty());
        
        // When & Then
        TemplateNotFoundException exception = assertThrows(TemplateNotFoundException.class,
            () -> templateService.getTemplate("non-existing"));
        
        assertEquals("Template not found with id: non-existing", exception.getMessage());
        
        verify(templateRepository).findByIdAndActiveTrue("non-existing");
    }
    
    @Test
    void getAllTemplates_ShouldReturnAllActiveTemplates() {
        // Given
        List<Template> templates = Arrays.asList(sampleTemplate);
        when(templateRepository.findByActiveTrueOrderByCreatedAtDesc()).thenReturn(templates);
        
        // When
        List<TemplateResponse> responses = templateService.getAllTemplates();
        
        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("Test Template", responses.get(0).getName());
        
        verify(templateRepository).findByActiveTrueOrderByCreatedAtDesc();
    }
    
    @Test
    void getTemplatesPaginated_ShouldReturnPagedResults() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Template> templatePage = new PageImpl<>(Arrays.asList(sampleTemplate), pageable, 1);
        when(templateRepository.findByActiveTrueOrderByCreatedAtDesc(pageable)).thenReturn(templatePage);
        
        // When
        Page<TemplateResponse> responses = templateService.getTemplatesPaginated(pageable);
        
        // Then
        assertNotNull(responses);
        assertEquals(1, responses.getTotalElements());
        assertEquals(1, responses.getContent().size());
        assertEquals("Test Template", responses.getContent().get(0).getName());
        
        verify(templateRepository).findByActiveTrueOrderByCreatedAtDesc(pageable);
    }
    
    @Test
    void getTemplatesByCategory_ShouldReturnTemplatesOfSpecificCategory() {
        // Given
        List<Template> templates = Arrays.asList(sampleTemplate);
        when(templateRepository.findByCategoryAndActiveTrueOrderByCreatedAtDesc(TemplateCategory.CREDIT_CARD_STATEMENT))
            .thenReturn(templates);
        
        // When
        List<TemplateResponse> responses = templateService.getTemplatesByCategory(TemplateCategory.CREDIT_CARD_STATEMENT);
        
        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(TemplateCategory.CREDIT_CARD_STATEMENT, responses.get(0).getCategory());
        
        verify(templateRepository).findByCategoryAndActiveTrueOrderByCreatedAtDesc(TemplateCategory.CREDIT_CARD_STATEMENT);
    }
    
    @Test
    void searchTemplates_ShouldReturnMatchingTemplates() {
        // Given
        List<Template> templates = Arrays.asList(sampleTemplate);
        when(templateRepository.findByNameContainingIgnoreCaseAndActiveTrueOrderByCreatedAtDesc("test"))
            .thenReturn(templates);
        
        // When
        List<TemplateResponse> responses = templateService.searchTemplates("test");
        
        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertTrue(responses.get(0).getName().toLowerCase().contains("test"));
        
        verify(templateRepository).findByNameContainingIgnoreCaseAndActiveTrueOrderByCreatedAtDesc("test");
    }
    
    @Test
    void updateTemplate_WithValidRequest_ShouldUpdateAndReturnTemplate() {
        // Given
        when(templateRepository.findByIdAndActiveTrue("test-id")).thenReturn(Optional.of(sampleTemplate));
        doNothing().when(templateValidator).validateTemplateRequest(validRequest);
        when(templateRepository.save(any(Template.class))).thenReturn(sampleTemplate);
        when(templateVersionRepository.save(any(TemplateVersion.class))).thenReturn(new TemplateVersion());
        
        validRequest.setName("Updated Template");
        
        // When
        TemplateResponse response = templateService.updateTemplate("test-id", validRequest);
        
        // Then
        assertNotNull(response);
        verify(templateValidator).validateTemplateRequest(validRequest);
        verify(templateRepository).findByIdAndActiveTrue("test-id");
        verify(templateRepository).save(any(Template.class));
        verify(templateVersionRepository).save(any(TemplateVersion.class));
    }
    
    @Test
    void deleteTemplate_WithExistingId_ShouldSoftDeleteTemplate() {
        // Given
        when(templateRepository.findByIdAndActiveTrue("test-id")).thenReturn(Optional.of(sampleTemplate));
        when(templateRepository.save(any(Template.class))).thenReturn(sampleTemplate);
        
        // When
        templateService.deleteTemplate("test-id");
        
        // Then
        verify(templateRepository).findByIdAndActiveTrue("test-id");
        verify(templateRepository).save(argThat(template -> !template.isActive()));
    }
    
    @Test
    void permanentDeleteTemplate_WithExistingId_ShouldDeleteTemplateAndVersions() {
        // Given
        when(templateRepository.findById("test-id")).thenReturn(Optional.of(sampleTemplate));
        doNothing().when(templateVersionRepository).deleteByTemplateId("test-id");
        doNothing().when(templateRepository).delete(sampleTemplate);
        
        // When
        templateService.permanentDeleteTemplate("test-id");
        
        // Then
        verify(templateRepository).findById("test-id");
        verify(templateVersionRepository).deleteByTemplateId("test-id");
        verify(templateRepository).delete(sampleTemplate);
    }
    
    @Test
    void getTemplateVersions_WithExistingTemplate_ShouldReturnVersions() {
        // Given
        when(templateRepository.findByIdAndActiveTrue("test-id")).thenReturn(Optional.of(sampleTemplate));
        
        TemplateVersion version1 = new TemplateVersion();
        version1.setId("version-1");
        version1.setTemplateId("test-id");
        version1.setVersion(1);
        
        List<TemplateVersion> versions = Arrays.asList(version1);
        when(templateVersionRepository.findByTemplateIdOrderByVersionDesc("test-id")).thenReturn(versions);
        
        // When
        List<TemplateVersion> result = templateService.getTemplateVersions("test-id");
        
        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("version-1", result.get(0).getId());
        
        verify(templateRepository).findByIdAndActiveTrue("test-id");
        verify(templateVersionRepository).findByTemplateIdOrderByVersionDesc("test-id");
    }
    
    @Test
    void templateExists_WithExistingId_ShouldReturnTrue() {
        // Given
        when(templateRepository.existsByIdAndActiveTrue("test-id")).thenReturn(true);
        
        // When
        boolean exists = templateService.templateExists("test-id");
        
        // Then
        assertTrue(exists);
        verify(templateRepository).existsByIdAndActiveTrue("test-id");
    }
    
    @Test
    void templateExists_WithNonExistingId_ShouldReturnFalse() {
        // Given
        when(templateRepository.existsByIdAndActiveTrue("non-existing")).thenReturn(false);
        
        // When
        boolean exists = templateService.templateExists("non-existing");
        
        // Then
        assertFalse(exists);
        verify(templateRepository).existsByIdAndActiveTrue("non-existing");
    }
    
    @Test
    void getTemplateCount_ShouldReturnTotalCount() {
        // Given
        when(templateRepository.countByActiveTrue()).thenReturn(5L);
        
        // When
        long count = templateService.getTemplateCount();
        
        // Then
        assertEquals(5L, count);
        verify(templateRepository).countByActiveTrue();
    }
    
    @Test
    void getTemplateCountByCategory_ShouldReturnCategoryCount() {
        // Given
        when(templateRepository.countByCategoryAndActiveTrue(TemplateCategory.CREDIT_CARD_STATEMENT)).thenReturn(3L);
        
        // When
        long count = templateService.getTemplateCountByCategory(TemplateCategory.CREDIT_CARD_STATEMENT);
        
        // Then
        assertEquals(3L, count);
        verify(templateRepository).countByCategoryAndActiveTrue(TemplateCategory.CREDIT_CARD_STATEMENT);
    }
}