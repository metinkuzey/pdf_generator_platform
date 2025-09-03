package com.pdfgenerator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pdfgenerator.exception.PDFGenerationException;
import com.pdfgenerator.exception.ErrorCode;
import com.pdfgenerator.service.PDFGenerationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for PDFController
 */
@WebMvcTest(PDFController.class)
class PDFControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private PDFGenerationService pdfGenerationService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private Map<String, Object> sampleData;
    private byte[] samplePdfBytes;
    
    @BeforeEach
    void setUp() {
        sampleData = Map.of(
            "customer_name", "Ahmet Yılmaz",
            "customer_id", "12345678901",
            "card_number", "**** **** **** 1234"
        );
        
        samplePdfBytes = "Sample PDF content".getBytes();
    }
    
    @Test
    void generatePDF_WithValidRequest_ShouldReturnPDF() throws Exception {
        // Given
        when(pdfGenerationService.generatePDF(eq("test-template-001"), any(Map.class)))
            .thenReturn(samplePdfBytes);
        
        // When & Then
        mockMvc.perform(post("/api/pdf/generate/test-template-001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleData)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"document.pdf\""))
                .andExpect(content().bytes(samplePdfBytes));
        
        verify(pdfGenerationService).generatePDF("test-template-001", sampleData);
    }
    
    @Test
    void generatePDF_WithEmptyData_ShouldReturnPDF() throws Exception {
        // Given
        when(pdfGenerationService.generatePDF(eq("test-template-001"), any(Map.class)))
            .thenReturn(samplePdfBytes);
        
        // When & Then
        mockMvc.perform(post("/api/pdf/generate/test-template-001")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));
        
        verify(pdfGenerationService).generatePDF(eq("test-template-001"), any(Map.class));
    }
    
    @Test
    void generatePDF_WithTemplateNotFound_ShouldReturnNotFound() throws Exception {
        // Given
        when(pdfGenerationService.generatePDF(eq("non-existent"), any(Map.class)))
            .thenThrow(new PDFGenerationException(
                ErrorCode.TEMPLATE_NOT_FOUND, 
                "Template not found: non-existent",
                Map.of("templateId", "non-existent")
            ));
        
        // When & Then
        mockMvc.perform(post("/api/pdf/generate/non-existent")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleData)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("TMPL_005"))
                .andExpect(jsonPath("$.message").value("Template not found: non-existent"));
        
        verify(pdfGenerationService).generatePDF("non-existent", sampleData);
    }
    
    @Test
    void generatePDF_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Given
        when(pdfGenerationService.generatePDF(eq("test-template-001"), any(Map.class)))
            .thenThrow(new PDFGenerationException(
                ErrorCode.INVALID_DATA_FORMAT, 
                "Invalid data format",
                Map.of("templateId", "test-template-001")
            ));
        
        // When & Then
        mockMvc.perform(post("/api/pdf/generate/test-template-001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleData)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("PDF_002"))
                .andExpect(jsonPath("$.message").value("Invalid data format"));
        
        verify(pdfGenerationService).generatePDF("test-template-001", sampleData);
    }
    
    @Test
    void generatePDF_WithGenerationFailure_ShouldReturnInternalServerError() throws Exception {
        // Given
        when(pdfGenerationService.generatePDF(eq("test-template-001"), any(Map.class)))
            .thenThrow(new PDFGenerationException(
                ErrorCode.PDF_GENERATION_FAILED, 
                "PDF generation failed",
                Map.of("templateId", "test-template-001")
            ));
        
        // When & Then
        mockMvc.perform(post("/api/pdf/generate/test-template-001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleData)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("PDF_001"))
                .andExpect(jsonPath("$.message").value("PDF generation failed"));
        
        verify(pdfGenerationService).generatePDF("test-template-001", sampleData);
    }
    
    @Test
    void generatePreview_WithValidRequest_ShouldReturnPDF() throws Exception {
        // Given
        when(pdfGenerationService.generatePDF(eq("test-template-001"), any(Map.class)))
            .thenReturn(samplePdfBytes);
        
        // When & Then
        mockMvc.perform(post("/api/pdf/preview/test-template-001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleData)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(header().string("Content-Disposition", "inline; filename=\"preview.pdf\""))
                .andExpect(content().bytes(samplePdfBytes));
        
        verify(pdfGenerationService).generatePDF(eq("test-template-001"), any(Map.class));
    }
    
    @Test
    void generatePreview_WithoutData_ShouldUseSampleData() throws Exception {
        // Given
        when(pdfGenerationService.generatePDF(eq("test-template-001"), any(Map.class)))
            .thenReturn(samplePdfBytes);
        
        // When & Then
        mockMvc.perform(post("/api/pdf/preview/test-template-001")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));
        
        verify(pdfGenerationService).generatePDF(eq("test-template-001"), any(Map.class));
    }
    
    @Test
    void generatePreview_WithEmptyBody_ShouldUseSampleData() throws Exception {
        // Given
        when(pdfGenerationService.generatePDF(eq("test-template-001"), any(Map.class)))
            .thenReturn(samplePdfBytes);
        
        // When & Then
        mockMvc.perform(post("/api/pdf/preview/test-template-001"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));
        
        verify(pdfGenerationService).generatePDF(eq("test-template-001"), any(Map.class));
    }
    
    @Test
    void generatePreview_WithTemplateNotFound_ShouldReturnNotFound() throws Exception {
        // Given
        when(pdfGenerationService.generatePDF(eq("non-existent"), any(Map.class)))
            .thenThrow(new PDFGenerationException(
                ErrorCode.TEMPLATE_NOT_FOUND, 
                "Template not found: non-existent"
            ));
        
        // When & Then
        mockMvc.perform(post("/api/pdf/preview/non-existent")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value("TMPL_005"));
        
        verify(pdfGenerationService).generatePDF(eq("non-existent"), any(Map.class));
    }
    
    @Test
    void generatePDF_WithInvalidJSON_ShouldReturnBadRequest() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/pdf/generate/test-template-001")
                .contentType(MediaType.APPLICATION_JSON)
                .content("invalid json"))
                .andExpect(status().isBadRequest());
        
        verifyNoInteractions(pdfGenerationService);
    }
    
    @Test
    void generatePDF_WithLargeData_ShouldHandleCorrectly() throws Exception {
        // Given
        Map<String, Object> largeData = Map.of(
            "field1", "value1".repeat(1000),
            "field2", "value2".repeat(1000),
            "field3", "value3".repeat(1000)
        );
        
        when(pdfGenerationService.generatePDF(eq("test-template-001"), any(Map.class)))
            .thenReturn(samplePdfBytes);
        
        // When & Then
        mockMvc.perform(post("/api/pdf/generate/test-template-001")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(largeData)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));
        
        verify(pdfGenerationService).generatePDF(eq("test-template-001"), any(Map.class));
    }
}