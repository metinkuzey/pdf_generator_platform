package com.pdfgenerator.controller;

import com.pdfgenerator.service.PDFGenerationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for PDF generation operations
 */
@RestController
@RequestMapping("/api/pdf")
@Tag(name = "PDF Generation", description = "PDF generation operations")
public class PDFController {
    
    private static final Logger logger = LoggerFactory.getLogger(PDFController.class);
    
    @Autowired
    private PDFGenerationService pdfGenerationService;
    
    /**
     * Generate PDF from template and data
     * 
     * @param templateId Template identifier
     * @param data Dynamic data to populate in template
     * @return PDF file as byte array
     */
    @PostMapping("/generate/{templateId}")
    @Operation(summary = "Generate PDF from template", 
               description = "Generate a PDF document using the specified template and provided data")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "PDF generated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "404", description = "Template not found"),
        @ApiResponse(responseCode = "500", description = "PDF generation failed")
    })
    public ResponseEntity<byte[]> generatePDF(
            @Parameter(description = "Template ID", required = true)
            @PathVariable String templateId,
            @Parameter(description = "Dynamic data for template", required = true)
            @RequestBody Map<String, Object> data) {
        
        logger.info("PDF generation request received for template: {}", templateId);
        
        try {
            // Generate PDF
            byte[] pdfBytes = pdfGenerationService.generatePDF(templateId, data);
            
            // Prepare response headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "document.pdf");
            headers.setContentLength(pdfBytes.length);
            
            logger.info("PDF generation completed successfully for template: {}", templateId);
            
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
            
        } catch (Exception e) {
            logger.error("PDF generation failed for template: {}", templateId, e);
            throw e; // Let GlobalExceptionHandler handle it
        }
    }
    
    /**
     * Generate PDF preview (smaller size, watermarked)
     * 
     * @param templateId Template identifier
     * @param data Dynamic data to populate in template
     * @return PDF preview as byte array
     */
    @PostMapping("/preview/{templateId}")
    @Operation(summary = "Generate PDF preview", 
               description = "Generate a preview PDF with sample data for template validation")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "PDF preview generated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "404", description = "Template not found"),
        @ApiResponse(responseCode = "500", description = "PDF preview generation failed")
    })
    public ResponseEntity<byte[]> generatePreview(
            @Parameter(description = "Template ID", required = true)
            @PathVariable String templateId,
            @Parameter(description = "Sample data for preview", required = false)
            @RequestBody(required = false) Map<String, Object> data) {
        
        logger.info("PDF preview request received for template: {}", templateId);
        
        try {
            // Use sample data if none provided
            if (data == null || data.isEmpty()) {
                data = createSampleData();
            }
            
            // Generate PDF (for now, same as regular generation - will be enhanced later)
            byte[] pdfBytes = pdfGenerationService.generatePDF(templateId, data);
            
            // Prepare response headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("inline", "preview.pdf");
            headers.setContentLength(pdfBytes.length);
            
            logger.info("PDF preview generated successfully for template: {}", templateId);
            
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
            
        } catch (Exception e) {
            logger.error("PDF preview generation failed for template: {}", templateId, e);
            throw e; // Let GlobalExceptionHandler handle it
        }
    }
    
    /**
     * Create sample data for preview
     */
    private Map<String, Object> createSampleData() {
        Map<String, Object> sampleData = new HashMap<>();
        sampleData.put("title", "Örnek Belge");
        sampleData.put("customer_name", "Ahmet Yılmaz");
        sampleData.put("customer_id", "12345678901");
        sampleData.put("card_number", "**** **** **** 1234");
        sampleData.put("policy_number", "POL-2025-001");
        sampleData.put("insured_name", "Mehmet Demir");
        sampleData.put("premium_amount", "2.500,00 TL");
        sampleData.put("account_number", "TR12 3456 7890 1234 5678 90");
        sampleData.put("account_holder", "Ayşe Kaya");
        sampleData.put("balance", "15.750,50 TL");
        sampleData.put("receipt_number", "RCP-2025-001");
        sampleData.put("payer_name", "Fatma Özkan");
        sampleData.put("amount", "1.250,00 TL");
        sampleData.put("payment_date", "15/01/2025");
        sampleData.put("transactions", "sample_transactions");
        return sampleData;
    }
}