package com.pdfgenerator.service;

import com.pdfgenerator.entity.Template;
import com.pdfgenerator.enums.TemplateCategory;
import com.pdfgenerator.exception.PDFGenerationException;
import com.pdfgenerator.exception.ErrorCode;
import com.pdfgenerator.repository.TemplateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for PDFGenerationService
 */
@ExtendWith(MockitoExtension.class)
class PDFGenerationServiceTest {
    
    @Mock
    private TemplateRepository templateRepository;
    
    @InjectMocks
    private PDFGenerationService pdfGenerationService;
    
    private Template sampleTemplate;
    private Map<String, Object> sampleData;
    
    @BeforeEach
    void setUp() {
        sampleTemplate = new Template();
        sampleTemplate.setId("test-template-001");
        sampleTemplate.setName("Test Template");
        sampleTemplate.setCategory(TemplateCategory.CREDIT_CARD_STATEMENT);
        sampleTemplate.setActive(true);
        sampleTemplate.setCreatedAt(LocalDateTime.now());
        
        sampleData = Map.of(
            "customer_name", "Ahmet Yılmaz",
            "customer_id", "12345678901",
            "card_number", "**** **** **** 1234",
            "transactions", "sample_transactions"
        );
    }
    
    @Test
    void generatePDF_WithValidTemplateAndData_ShouldReturnPDFBytes() {
        // Given
        when(templateRepository.findById("test-template-001")).thenReturn(Optional.of(sampleTemplate));
        
        // When
        byte[] result = pdfGenerationService.generatePDF("test-template-001", sampleData);
        
        // Then
        assertNotNull(result);
        assertTrue(result.length > 0);
        verify(templateRepository).findById("test-template-001");
    }
    
    @Test
    void generatePDF_WithNonExistentTemplate_ShouldThrowException() {
        // Given
        when(templateRepository.findById("non-existent")).thenReturn(Optional.empty());
        
        // When & Then
        PDFGenerationException exception = assertThrows(PDFGenerationException.class, () -> {
            pdfGenerationService.generatePDF("non-existent", sampleData);
        });
        
        assertEquals(ErrorCode.TEMPLATE_NOT_FOUND, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("Template not found"));
        verify(templateRepository).findById("non-existent");
    }
    
    @Test
    void generatePDF_WithCreditCardTemplate_ShouldGenerateCorrectContent() {
        // Given
        sampleTemplate.setCategory(TemplateCategory.CREDIT_CARD_STATEMENT);
        when(templateRepository.findById("test-template-001")).thenReturn(Optional.of(sampleTemplate));
        
        // When
        byte[] result = pdfGenerationService.generatePDF("test-template-001", sampleData);
        
        // Then
        assertNotNull(result);
        assertTrue(result.length > 0);
        // PDF should contain credit card specific content
        // Note: In a real scenario, we would parse PDF content to verify specific elements
    }
    
    @Test
    void generatePDF_WithInsurancePolicyTemplate_ShouldGenerateCorrectContent() {
        // Given
        sampleTemplate.setCategory(TemplateCategory.HEALTH_INSURANCE_POLICY);
        Map<String, Object> insuranceData = Map.of(
            "policy_number", "POL-2025-001",
            "insured_name", "Mehmet Demir",
            "premium_amount", "2.500,00 TL"
        );
        when(templateRepository.findById("test-template-001")).thenReturn(Optional.of(sampleTemplate));
        
        // When
        byte[] result = pdfGenerationService.generatePDF("test-template-001", insuranceData);
        
        // Then
        assertNotNull(result);
        assertTrue(result.length > 0);
    }
    
    @Test
    void generatePDF_WithAccountStatementTemplate_ShouldGenerateCorrectContent() {
        // Given
        sampleTemplate.setCategory(TemplateCategory.ACCOUNT_STATEMENT);
        Map<String, Object> accountData = Map.of(
            "account_number", "TR12 3456 7890 1234 5678 90",
            "account_holder", "Ayşe Kaya",
            "balance", "15.750,50 TL"
        );
        when(templateRepository.findById("test-template-001")).thenReturn(Optional.of(sampleTemplate));
        
        // When
        byte[] result = pdfGenerationService.generatePDF("test-template-001", accountData);
        
        // Then
        assertNotNull(result);
        assertTrue(result.length > 0);
    }
    
    @Test
    void generatePDF_WithPaymentReceiptTemplate_ShouldGenerateCorrectContent() {
        // Given
        sampleTemplate.setCategory(TemplateCategory.PAYMENT_RECEIPT);
        Map<String, Object> paymentData = Map.of(
            "receipt_number", "RCP-2025-001",
            "payer_name", "Fatma Özkan",
            "amount", "1.250,00 TL",
            "payment_date", "15/01/2025"
        );
        when(templateRepository.findById("test-template-001")).thenReturn(Optional.of(sampleTemplate));
        
        // When
        byte[] result = pdfGenerationService.generatePDF("test-template-001", paymentData);
        
        // Then
        assertNotNull(result);
        assertTrue(result.length > 0);
    }
    
    @Test
    void generatePDF_WithGenericTemplate_ShouldGenerateCorrectContent() {
        // Given
        sampleTemplate.setCategory(TemplateCategory.OTHER);
        Map<String, Object> genericData = Map.of(
            "field1", "value1",
            "field2", "value2",
            "field3", "value3"
        );
        when(templateRepository.findById("test-template-001")).thenReturn(Optional.of(sampleTemplate));
        
        // When
        byte[] result = pdfGenerationService.generatePDF("test-template-001", genericData);
        
        // Then
        assertNotNull(result);
        assertTrue(result.length > 0);
    }
    
    @Test
    void generatePDF_WithEmptyData_ShouldGenerateBasicPDF() {
        // Given
        when(templateRepository.findById("test-template-001")).thenReturn(Optional.of(sampleTemplate));
        
        // When
        byte[] result = pdfGenerationService.generatePDF("test-template-001", Map.of());
        
        // Then
        assertNotNull(result);
        assertTrue(result.length > 0);
    }
    
    @Test
    void generatePDF_WithNullData_ShouldGenerateBasicPDF() {
        // Given
        when(templateRepository.findById("test-template-001")).thenReturn(Optional.of(sampleTemplate));
        
        // When
        byte[] result = pdfGenerationService.generatePDF("test-template-001", null);
        
        // Then
        assertNotNull(result);
        assertTrue(result.length > 0);
    }
    
    @Test
    void generatePDF_WithCustomTitle_ShouldUseCustomTitle() {
        // Given
        Map<String, Object> dataWithTitle = Map.of(
            "title", "Özel Başlık",
            "customer_name", "Test User"
        );
        when(templateRepository.findById("test-template-001")).thenReturn(Optional.of(sampleTemplate));
        
        // When
        byte[] result = pdfGenerationService.generatePDF("test-template-001", dataWithTitle);
        
        // Then
        assertNotNull(result);
        assertTrue(result.length > 0);
        // In a real scenario, we would verify that the PDF contains "Özel Başlık"
    }
    
    @Test
    void generatePDF_WithRepositoryException_ShouldThrowPDFGenerationException() {
        // Given
        when(templateRepository.findById(anyString())).thenThrow(new RuntimeException("Database error"));
        
        // When & Then
        PDFGenerationException exception = assertThrows(PDFGenerationException.class, () -> {
            pdfGenerationService.generatePDF("test-template-001", sampleData);
        });
        
        assertEquals(ErrorCode.PDF_GENERATION_FAILED, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("Failed to generate PDF"));
    }
    
    @Test
    void generatePDF_WithSchemaBasedTemplate_ShouldGenerateCorrectContent() {
        // Given
        Map<String, Object> schema = Map.of(
            "layout", Map.of(
                "pageSize", "A4",
                "margins", Map.of("top", 20, "right", 20, "bottom", 20, "left", 20)
            ),
            "elements", List.of(
                Map.of(
                    "type", "TEXT",
                    "properties", Map.of(
                        "text", "{{customer_name}} için Belge",
                        "fontSize", 16,
                        "fontWeight", "bold",
                        "textAlign", "center"
                    )
                ),
                Map.of(
                    "type", "TABLE",
                    "properties", Map.of(
                        "columns", List.of(
                            Map.of("header", "Alan", "width", 30),
                            Map.of("header", "Değer", "width", 70)
                        ),
                        "rows", List.of(
                            Map.of("label", "Müşteri Adı", "value", "{{customer_name}}"),
                            Map.of("label", "Müşteri No", "value", "{{customer_id}}")
                        )
                    )
                )
            )
        );
        
        sampleTemplate.setSchema(schema);
        when(templateRepository.findById("test-template-001")).thenReturn(Optional.of(sampleTemplate));
        
        // When
        byte[] result = pdfGenerationService.generatePDF("test-template-001", sampleData);
        
        // Then
        assertNotNull(result);
        assertTrue(result.length > 0);
        // Schema-based generation should produce different content than category-based
    }
    
    @Test
    void generatePDF_WithContainerElement_ShouldRenderChildren() {
        // Given
        Map<String, Object> schema = Map.of(
            "elements", List.of(
                Map.of(
                    "type", "CONTAINER",
                    "children", List.of(
                        Map.of(
                            "type", "TEXT",
                            "properties", Map.of("text", "Başlık: {{title}}")
                        ),
                        Map.of(
                            "type", "TEXT",
                            "properties", Map.of("text", "Müşteri: {{customer_name}}")
                        )
                    )
                )
            )
        );
        
        sampleTemplate.setSchema(schema);
        when(templateRepository.findById("test-template-001")).thenReturn(Optional.of(sampleTemplate));
        
        Map<String, Object> testData = Map.of(
            "title", "Test Belgesi",
            "customer_name", "Test Müşteri"
        );
        
        // When
        byte[] result = pdfGenerationService.generatePDF("test-template-001", testData);
        
        // Then
        assertNotNull(result);
        assertTrue(result.length > 0);
    }
    
    @Test
    void generatePDF_WithImageElement_ShouldRenderPlaceholder() {
        // Given
        Map<String, Object> schema = Map.of(
            "elements", List.of(
                Map.of(
                    "type", "IMAGE",
                    "properties", Map.of("src", "{{company_logo}}")
                )
            )
        );
        
        sampleTemplate.setSchema(schema);
        when(templateRepository.findById("test-template-001")).thenReturn(Optional.of(sampleTemplate));
        
        Map<String, Object> testData = Map.of(
            "company_logo", "logo.png"
        );
        
        // When
        byte[] result = pdfGenerationService.generatePDF("test-template-001", testData);
        
        // Then
        assertNotNull(result);
        assertTrue(result.length > 0);
        // Should render image placeholder since actual image handling is not implemented yet
    }
    
    @Test
    void generatePDF_WithInvalidSchema_ShouldFallbackToBasicContent() {
        // Given
        Map<String, Object> invalidSchema = Map.of(
            "elements", "invalid_elements_format" // Should be a list, not string
        );
        
        sampleTemplate.setSchema(invalidSchema);
        when(templateRepository.findById("test-template-001")).thenReturn(Optional.of(sampleTemplate));
        
        // When
        byte[] result = pdfGenerationService.generatePDF("test-template-001", sampleData);
        
        // Then
        assertNotNull(result);
        assertTrue(result.length > 0);
        // Should fallback to basic content generation when schema parsing fails
    }
}