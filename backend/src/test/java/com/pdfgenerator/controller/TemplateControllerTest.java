package com.pdfgenerator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pdfgenerator.dto.TemplateRequest;
import com.pdfgenerator.dto.TemplateResponse;
import com.pdfgenerator.entity.TemplateVersion;
import com.pdfgenerator.enums.TemplateCategory;
import com.pdfgenerator.exception.TemplateNotFoundException;
import com.pdfgenerator.exception.ValidationException;
import com.pdfgenerator.service.TemplateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TemplateController.class)
class TemplateControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private TemplateService templateService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private TemplateRequest validRequest;
    private TemplateResponse sampleResponse;
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
        
        // Sample response
        sampleResponse = new TemplateResponse();
        sampleResponse.setId("test-id");
        sampleResponse.setName("Test Template");
        sampleResponse.setCategory(TemplateCategory.CREDIT_CARD_STATEMENT);
        sampleResponse.setSchema(sampleSchema);
        sampleResponse.setDescription("Test description");
        sampleResponse.setActive(true);
        sampleResponse.setVersion(1);
        sampleResponse.setCreatedAt(LocalDateTime.now());
        sampleResponse.setCreatedBy("system");
    }
    
    @Test
    void createTemplate_WithValidRequest_ShouldReturnCreatedTemplate() throws Exception {
        // Given
        when(templateService.createTemplate(any(TemplateRequest.class))).thenReturn(sampleResponse);
        
        // When & Then
        mockMvc.perform(post("/api/templates")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("test-id"))
                .andExpect(jsonPath("$.name").value("Test Template"))
                .andExpect(jsonPath("$.category").value("CREDIT_CARD_STATEMENT"))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.version").value(1));
        
        verify(templateService).createTemplate(any(TemplateRequest.class));
    }
    
    @Test
    void createTemplate_WithInvalidRequest_ShouldReturnBadRequest() throws Exception {
        // Given
        when(templateService.createTemplate(any(TemplateRequest.class)))
            .thenThrow(new ValidationException("Validation failed", List.of("Name is required")));
        
        // When & Then
        mockMvc.perform(post("/api/templates")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors[0]").value("Name is required"));
        
        verify(templateService).createTemplate(any(TemplateRequest.class));
    }
    
    @Test
    void getTemplate_WithExistingId_ShouldReturnTemplate() throws Exception {
        // Given
        when(templateService.getTemplate("test-id")).thenReturn(sampleResponse);
        
        // When & Then
        mockMvc.perform(get("/api/templates/test-id"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("test-id"))
                .andExpect(jsonPath("$.name").value("Test Template"));
        
        verify(templateService).getTemplate("test-id");
    }
    
    @Test
    void getTemplate_WithNonExistingId_ShouldReturnNotFound() throws Exception {
        // Given
        when(templateService.getTemplate("non-existing"))
            .thenThrow(new TemplateNotFoundException("non-existing"));
        
        // When & Then
        mockMvc.perform(get("/api/templates/non-existing"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("TEMPLATE_NOT_FOUND"));
        
        verify(templateService).getTemplate("non-existing");
    }
    
    @Test
    void getAllTemplates_ShouldReturnAllTemplates() throws Exception {
        // Given
        List<TemplateResponse> templates = Arrays.asList(sampleResponse);
        when(templateService.getAllTemplates()).thenReturn(templates);
        
        // When & Then
        mockMvc.perform(get("/api/templates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value("test-id"))
                .andExpect(jsonPath("$[0].name").value("Test Template"));
        
        verify(templateService).getAllTemplates();
    }
    
    @Test
    void getAllTemplates_WithSearchParameter_ShouldReturnSearchResults() throws Exception {
        // Given
        List<TemplateResponse> templates = Arrays.asList(sampleResponse);
        when(templateService.searchTemplates("test")).thenReturn(templates);
        
        // When & Then
        mockMvc.perform(get("/api/templates?search=test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Test Template"));
        
        verify(templateService).searchTemplates("test");
        verify(templateService, never()).getAllTemplates();
        verify(templateService, never()).getTemplatesByCategory(any());
    }
    
    @Test
    void getAllTemplates_WithCategoryParameter_ShouldReturnCategoryTemplates() throws Exception {
        // Given
        List<TemplateResponse> templates = Arrays.asList(sampleResponse);
        when(templateService.getTemplatesByCategory(TemplateCategory.CREDIT_CARD_STATEMENT)).thenReturn(templates);
        
        // When & Then
        mockMvc.perform(get("/api/templates?category=CREDIT_CARD_STATEMENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].category").value("CREDIT_CARD_STATEMENT"));
        
        verify(templateService).getTemplatesByCategory(TemplateCategory.CREDIT_CARD_STATEMENT);
        verify(templateService, never()).getAllTemplates();
        verify(templateService, never()).searchTemplates(any());
    }
    
    @Test
    void getTemplatesPaginated_ShouldReturnPagedResults() throws Exception {
        // Given
        Page<TemplateResponse> templatePage = new PageImpl<>(Arrays.asList(sampleResponse), PageRequest.of(0, 10), 1);
        when(templateService.getTemplatesPaginated(any())).thenReturn(templatePage);
        
        // When & Then
        mockMvc.perform(get("/api/templates/paginated?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value("test-id"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.number").value(0));
        
        verify(templateService).getTemplatesPaginated(any());
    }
    
    @Test
    void getTemplatesByCategory_ShouldReturnCategoryTemplates() throws Exception {
        // Given
        List<TemplateResponse> templates = Arrays.asList(sampleResponse);
        when(templateService.getTemplatesByCategory(TemplateCategory.CREDIT_CARD_STATEMENT)).thenReturn(templates);
        
        // When & Then
        mockMvc.perform(get("/api/templates/category/CREDIT_CARD_STATEMENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].category").value("CREDIT_CARD_STATEMENT"));
        
        verify(templateService).getTemplatesByCategory(TemplateCategory.CREDIT_CARD_STATEMENT);
    }
    
    @Test
    void searchTemplates_ShouldReturnSearchResults() throws Exception {
        // Given
        List<TemplateResponse> templates = Arrays.asList(sampleResponse);
        when(templateService.searchTemplates("test")).thenReturn(templates);
        
        // When & Then
        mockMvc.perform(get("/api/templates/search?q=test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Test Template"));
        
        verify(templateService).searchTemplates("test");
    }
    
    @Test
    void updateTemplate_WithValidRequest_ShouldReturnUpdatedTemplate() throws Exception {
        // Given
        when(templateService.updateTemplate(eq("test-id"), any(TemplateRequest.class))).thenReturn(sampleResponse);
        
        // When & Then
        mockMvc.perform(put("/api/templates/test-id")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("test-id"))
                .andExpect(jsonPath("$.name").value("Test Template"));
        
        verify(templateService).updateTemplate(eq("test-id"), any(TemplateRequest.class));
    }
    
    @Test
    void deleteTemplate_WithExistingId_ShouldReturnNoContent() throws Exception {
        // Given
        doNothing().when(templateService).deleteTemplate("test-id");
        
        // When & Then
        mockMvc.perform(delete("/api/templates/test-id"))
                .andExpect(status().isNoContent());
        
        verify(templateService).deleteTemplate("test-id");
    }
    
    @Test
    void permanentDeleteTemplate_WithExistingId_ShouldReturnNoContent() throws Exception {
        // Given
        doNothing().when(templateService).permanentDeleteTemplate("test-id");
        
        // When & Then
        mockMvc.perform(delete("/api/templates/test-id/permanent"))
                .andExpect(status().isNoContent());
        
        verify(templateService).permanentDeleteTemplate("test-id");
    }
    
    @Test
    void getTemplateVersions_ShouldReturnVersions() throws Exception {
        // Given
        TemplateVersion version = new TemplateVersion();
        version.setId("version-1");
        version.setTemplateId("test-id");
        version.setVersion(1);
        
        List<TemplateVersion> versions = Arrays.asList(version);
        when(templateService.getTemplateVersions("test-id")).thenReturn(versions);
        
        // When & Then
        mockMvc.perform(get("/api/templates/test-id/versions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value("version-1"))
                .andExpect(jsonPath("$[0].templateId").value("test-id"))
                .andExpect(jsonPath("$[0].version").value(1));
        
        verify(templateService).getTemplateVersions("test-id");
    }
    
    @Test
    void restoreTemplateVersion_ShouldReturnRestoredTemplate() throws Exception {
        // Given
        when(templateService.restoreTemplateVersion("test-id", 1)).thenReturn(sampleResponse);
        
        // When & Then
        mockMvc.perform(post("/api/templates/test-id/versions/1/restore"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("test-id"));
        
        verify(templateService).restoreTemplateVersion("test-id", 1);
    }
    
    @Test
    void checkTemplateExists_WithExistingId_ShouldReturnTrue() throws Exception {
        // Given
        when(templateService.templateExists("test-id")).thenReturn(true);
        
        // When & Then
        mockMvc.perform(get("/api/templates/test-id/exists"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.exists").value(true));
        
        verify(templateService).templateExists("test-id");
    }
    
    @Test
    void getTemplateCount_ShouldReturnCount() throws Exception {
        // Given
        when(templateService.getTemplateCount()).thenReturn(5L);
        
        // When & Then
        mockMvc.perform(get("/api/templates/stats/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalCount").value(5));
        
        verify(templateService).getTemplateCount();
    }
    
    @Test
    void getTemplateCountByCategory_ShouldReturnCategoryCount() throws Exception {
        // Given
        when(templateService.getTemplateCountByCategory(TemplateCategory.CREDIT_CARD_STATEMENT)).thenReturn(3L);
        
        // When & Then
        mockMvc.perform(get("/api/templates/stats/count/category/CREDIT_CARD_STATEMENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(3));
        
        verify(templateService).getTemplateCountByCategory(TemplateCategory.CREDIT_CARD_STATEMENT);
    }
    
    @Test
    void getTemplateCategories_ShouldReturnAllCategories() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/templates/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}