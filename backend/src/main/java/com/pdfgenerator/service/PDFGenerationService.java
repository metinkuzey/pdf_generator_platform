package com.pdfgenerator.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.io.image.ImageDataFactory;
import com.pdfgenerator.entity.Template;
import com.pdfgenerator.repository.TemplateRepository;
import com.pdfgenerator.exception.PDFGenerationException;
import com.pdfgenerator.exception.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.Optional;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Core PDF generation service using iText 7
 * Handles template-based PDF creation with dynamic data
 */
@Service
public class PDFGenerationService {
    
    private static final Logger logger = LoggerFactory.getLogger(PDFGenerationService.class);
    
    @Autowired
    private TemplateRepository templateRepository;
    
    /**
     * Generate PDF from template and data
     * 
     * @param templateId Template identifier
     * @param data Dynamic data to populate in template
     * @return PDF as byte array
     * @throws PDFGenerationException if generation fails
     */
    public byte[] generatePDF(String templateId, Map<String, Object> data) {
        logger.info("Starting PDF generation for template: {}", templateId);
        
        try {
            // Fetch template
            Template template = getTemplate(templateId);
            
            // Create PDF document
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfDocument pdfDocument = createPdfDocument(outputStream);
            Document document = new Document(pdfDocument);
            
            // Generate content based on template
            generateContent(document, template, data);
            
            // Close document
            document.close();
            
            byte[] pdfBytes = outputStream.toByteArray();
            logger.info("PDF generation completed successfully. Size: {} bytes", pdfBytes.length);
            
            return pdfBytes;
            
        } catch (PDFGenerationException e) {
            // Re-throw PDF generation exceptions as-is
            logger.error("PDF generation failed for template: {}", templateId, e);
            throw e;
        } catch (Exception e) {
            logger.error("PDF generation failed for template: {}", templateId, e);
            throw new PDFGenerationException(
                ErrorCode.PDF_GENERATION_FAILED, 
                "Failed to generate PDF: " + e.getMessage(),
                Map.of("templateId", templateId)
            );
        }
    }
    
    /**
     * Get template by ID
     */
    private Template getTemplate(String templateId) {
        Optional<Template> templateOpt = templateRepository.findById(templateId);
        if (templateOpt.isEmpty()) {
            throw new PDFGenerationException(
                ErrorCode.TEMPLATE_NOT_FOUND,
                "Template not found: " + templateId,
                Map.of("templateId", templateId)
            );
        }
        return templateOpt.get();
    }
    
    /**
     * Create PDF document with basic configuration
     */
    private PdfDocument createPdfDocument(ByteArrayOutputStream outputStream) {
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdfDocument = new PdfDocument(writer);
        
        // Set document metadata
        pdfDocument.getDocumentInfo().setTitle("Generated PDF Document");
        pdfDocument.getDocumentInfo().setCreator("PDF Generator Platform");
        pdfDocument.getDocumentInfo().setProducer("iText 8.0.2");
        
        return pdfDocument;
    }
    
    /**
     * Generate document content based on template and data
     */
    private void generateContent(Document document, Template template, Map<String, Object> data) {
        logger.debug("Generating content for template: {}", template.getName());
        
        // Handle null data
        if (data == null) {
            data = Map.of();
        }
        
        // Check if template has schema-based structure
        if (template.getSchema() != null && !template.getSchema().isEmpty()) {
            generateSchemaBasedContent(document, template.getSchema(), data);
        } else {
            // Fallback to category-based generation
            generateCategoryBasedContent(document, template, data);
        }
    }
    
    /**
     * Generate content based on template schema structure
     */
    private void generateSchemaBasedContent(Document document, Map<String, Object> schema, Map<String, Object> data) {
        logger.debug("Generating schema-based content");
        
        try {
            // Get layout configuration
            Map<String, Object> layout = getLayoutFromSchema(schema);
            configureDocumentLayout(document, layout);
            
            // Get elements from schema
            List<Map<String, Object>> elements = getElementsFromSchema(schema);
            
            // Render each element
            for (Map<String, Object> element : elements) {
                renderElement(document, element, data);
            }
            
        } catch (Exception e) {
            logger.warn("Failed to generate schema-based content, falling back to basic content: {}", e.getMessage());
            // Fallback to basic content generation
            generateBasicContent(document, data);
        }
    }
    
    /**
     * Generate content based on template category (fallback method)
     */
    private void generateCategoryBasedContent(Document document, Template template, Map<String, Object> data) {
        logger.debug("Generating category-based content for: {}", template.getCategory());
        
        // Add title
        String title = template.getName();
        if (data.containsKey("title")) {
            title = data.get("title").toString();
        }
        
        Paragraph titleParagraph = new Paragraph(title)
            .setTextAlignment(TextAlignment.CENTER)
            .setFontSize(18)
            .setBold();
        document.add(titleParagraph);
        
        // Add basic content based on template category
        switch (template.getCategory()) {
            case CREDIT_CARD_STATEMENT:
                generateCreditCardContent(document, data);
                break;
            case HEALTH_INSURANCE_POLICY:
                generateInsurancePolicyContent(document, data);
                break;
            case ACCOUNT_STATEMENT:
                generateAccountStatementContent(document, data);
                break;
            case PAYMENT_RECEIPT:
                generatePaymentReceiptContent(document, data);
                break;
            default:
                generateGenericContent(document, data);
                break;
        }
    }
    
    /**
     * Generate credit card statement content
     */
    private void generateCreditCardContent(Document document, Map<String, Object> data) {
        logger.debug("Generating credit card statement content");
        
        // Customer information table
        Table customerTable = new Table(UnitValue.createPercentArray(new float[]{30, 70}))
            .setWidth(UnitValue.createPercentValue(100));
        
        customerTable.addHeaderCell(new Cell().add(new Paragraph("Müşteri Bilgileri").setBold()));
        customerTable.addHeaderCell(new Cell().add(new Paragraph("Değer").setBold()));
        
        addTableRow(customerTable, "Müşteri Adı", data.get("customer_name"));
        addTableRow(customerTable, "Müşteri No", data.get("customer_id"));
        addTableRow(customerTable, "Kart No", data.get("card_number"));
        
        document.add(customerTable);
        
        // Add spacing
        document.add(new Paragraph("\n"));
        
        // Transaction details
        if (data.containsKey("transactions")) {
            generateTransactionTable(document, data.get("transactions"));
        }
    }
    
    /**
     * Generate insurance policy content
     */
    private void generateInsurancePolicyContent(Document document, Map<String, Object> data) {
        logger.debug("Generating insurance policy content");
        
        // Policy information
        Table policyTable = new Table(UnitValue.createPercentArray(new float[]{30, 70}))
            .setWidth(UnitValue.createPercentValue(100));
        
        policyTable.addHeaderCell(new Cell().add(new Paragraph("Poliçe Bilgileri").setBold()));
        policyTable.addHeaderCell(new Cell().add(new Paragraph("Değer").setBold()));
        
        addTableRow(policyTable, "Poliçe No", data.get("policy_number"));
        addTableRow(policyTable, "Sigortalı", data.get("insured_name"));
        addTableRow(policyTable, "Prim Tutarı", data.get("premium_amount"));
        
        document.add(policyTable);
    }
    
    /**
     * Generate account statement content
     */
    private void generateAccountStatementContent(Document document, Map<String, Object> data) {
        logger.debug("Generating account statement content");
        
        // Account information
        Table accountTable = new Table(UnitValue.createPercentArray(new float[]{30, 70}))
            .setWidth(UnitValue.createPercentValue(100));
        
        accountTable.addHeaderCell(new Cell().add(new Paragraph("Hesap Bilgileri").setBold()));
        accountTable.addHeaderCell(new Cell().add(new Paragraph("Değer").setBold()));
        
        addTableRow(accountTable, "Hesap No", data.get("account_number"));
        addTableRow(accountTable, "Hesap Sahibi", data.get("account_holder"));
        addTableRow(accountTable, "Bakiye", data.get("balance"));
        
        document.add(accountTable);
    }
    
    /**
     * Generate payment receipt content
     */
    private void generatePaymentReceiptContent(Document document, Map<String, Object> data) {
        logger.debug("Generating payment receipt content");
        
        // Payment information
        Table paymentTable = new Table(UnitValue.createPercentArray(new float[]{30, 70}))
            .setWidth(UnitValue.createPercentValue(100));
        
        paymentTable.addHeaderCell(new Cell().add(new Paragraph("Ödeme Bilgileri").setBold()));
        paymentTable.addHeaderCell(new Cell().add(new Paragraph("Değer").setBold()));
        
        addTableRow(paymentTable, "Makbuz No", data.get("receipt_number"));
        addTableRow(paymentTable, "Ödeyen", data.get("payer_name"));
        addTableRow(paymentTable, "Tutar", data.get("amount"));
        addTableRow(paymentTable, "Tarih", data.get("payment_date"));
        
        document.add(paymentTable);
    }
    
    /**
     * Generate generic content for unknown template types
     */
    private void generateGenericContent(Document document, Map<String, Object> data) {
        logger.debug("Generating generic content");
        
        // Simple key-value table for all data
        Table dataTable = new Table(UnitValue.createPercentArray(new float[]{30, 70}))
            .setWidth(UnitValue.createPercentValue(100));
        
        dataTable.addHeaderCell(new Cell().add(new Paragraph("Alan").setBold()));
        dataTable.addHeaderCell(new Cell().add(new Paragraph("Değer").setBold()));
        
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            addTableRow(dataTable, entry.getKey(), entry.getValue());
        }
        
        document.add(dataTable);
    }
    
    /**
     * Generate transaction table for credit card statements
     */
    private void generateTransactionTable(Document document, Object transactions) {
        // Add transaction table title
        document.add(new Paragraph("İşlem Detayları").setBold().setFontSize(14));
        
        // Create transaction table
        Table transactionTable = new Table(UnitValue.createPercentArray(new float[]{20, 40, 20, 20}))
            .setWidth(UnitValue.createPercentValue(100));
        
        // Add headers
        transactionTable.addHeaderCell(new Cell().add(new Paragraph("Tarih").setBold()));
        transactionTable.addHeaderCell(new Cell().add(new Paragraph("Açıklama").setBold()));
        transactionTable.addHeaderCell(new Cell().add(new Paragraph("Tutar").setBold()));
        transactionTable.addHeaderCell(new Cell().add(new Paragraph("Bakiye").setBold()));
        
        // Add sample transaction if transactions is not a proper list
        if (transactions != null) {
            // For now, add a sample row - will be enhanced in later tasks
            transactionTable.addCell(new Cell().add(new Paragraph("01/01/2025")));
            transactionTable.addCell(new Cell().add(new Paragraph("Örnek İşlem")));
            transactionTable.addCell(new Cell().add(new Paragraph("1.000,00 TL")));
            transactionTable.addCell(new Cell().add(new Paragraph("5.000,00 TL")));
        }
        
        document.add(transactionTable);
    }
    
    /**
     * Helper method to add table row
     */
    private void addTableRow(Table table, String label, Object value) {
        table.addCell(new Cell().add(new Paragraph(label)));
        table.addCell(new Cell().add(new Paragraph(value != null ? value.toString() : "")));
    }
    
    // ========== Schema-based PDF Generation Methods ==========
    
    /**
     * Get layout configuration from schema
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> getLayoutFromSchema(Map<String, Object> schema) {
        return (Map<String, Object>) schema.getOrDefault("layout", Map.of());
    }
    
    /**
     * Get elements list from schema
     */
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> getElementsFromSchema(Map<String, Object> schema) {
        Object elements = schema.get("elements");
        if (elements instanceof List) {
            return (List<Map<String, Object>>) elements;
        }
        return List.of();
    }
    
    /**
     * Configure document layout based on schema
     */
    private void configureDocumentLayout(Document document, Map<String, Object> layout) {
        // Set page size if specified
        String pageSize = (String) layout.getOrDefault("pageSize", "A4");
        // Note: Page size is set during document creation, this is for future enhancement
        
        // Set margins if specified
        Map<String, Object> margins = getMargins(layout);
        if (!margins.isEmpty()) {
            float top = getFloatValue(margins, "top", 20);
            float right = getFloatValue(margins, "right", 20);
            float bottom = getFloatValue(margins, "bottom", 20);
            float left = getFloatValue(margins, "left", 20);
            
            document.setMargins(top, right, bottom, left);
        }
    }
    
    /**
     * Get margins from layout configuration
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> getMargins(Map<String, Object> layout) {
        Object margins = layout.get("margins");
        if (margins instanceof Map) {
            return (Map<String, Object>) margins;
        }
        return Map.of();
    }
    
    /**
     * Get float value from map with default
     */
    private float getFloatValue(Map<String, Object> map, String key, float defaultValue) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).floatValue();
        }
        return defaultValue;
    }
    
    /**
     * Render individual element based on type
     */
    private void renderElement(Document document, Map<String, Object> element, Map<String, Object> data) {
        String type = (String) element.getOrDefault("type", "TEXT");
        
        logger.debug("Rendering element of type: {}", type);
        
        switch (type.toUpperCase()) {
            case "TEXT":
                renderTextElement(document, element, data);
                break;
            case "TABLE":
                renderTableElement(document, element, data);
                break;
            case "IMAGE":
                renderImageElement(document, element, data);
                break;
            case "CONTAINER":
                renderContainerElement(document, element, data);
                break;
            default:
                logger.warn("Unknown element type: {}", type);
                renderTextElement(document, element, data); // Fallback to text
                break;
        }
    }
    
    /**
     * Render text element
     */
    private void renderTextElement(Document document, Map<String, Object> element, Map<String, Object> data) {
        Map<String, Object> properties = getElementProperties(element);
        
        String text = (String) properties.getOrDefault("text", "");
        text = replacePlaceholders(text, data);
        
        Paragraph paragraph = new Paragraph(text);
        
        // Apply text formatting
        applyTextFormatting(paragraph, properties);
        
        document.add(paragraph);
    }
    
    /**
     * Render table element
     */
    private void renderTableElement(Document document, Map<String, Object> element, Map<String, Object> data) {
        Map<String, Object> properties = getElementProperties(element);
        
        // Get table configuration
        List<Map<String, Object>> columns = getTableColumns(properties);
        List<Map<String, Object>> rows = getTableRows(properties);
        
        if (columns.isEmpty()) {
            logger.warn("Table element has no columns defined");
            return;
        }
        
        // Create table with column widths
        float[] columnWidths = new float[columns.size()];
        for (int i = 0; i < columns.size(); i++) {
            columnWidths[i] = getIntValue(columns.get(i), "width", 100);
        }
        
        Table table = new Table(UnitValue.createPercentArray(columnWidths))
            .setWidth(UnitValue.createPercentValue(100));
        
        // Add headers
        for (Map<String, Object> column : columns) {
            String header = (String) column.getOrDefault("header", "");
            Cell headerCell = new Cell().add(new Paragraph(header).setBold());
            table.addHeaderCell(headerCell);
        }
        
        // Add data rows
        for (Map<String, Object> row : rows) {
            for (Map<String, Object> column : columns) {
                String dataKey = (String) column.get("dataKey");
                String value = "";
                
                if (dataKey != null) {
                    Object dataValue = data.get(dataKey);
                    value = dataValue != null ? dataValue.toString() : "";
                } else {
                    String label = (String) row.get("label");
                    String valueKey = (String) row.get("value");
                    if (valueKey != null) {
                        value = replacePlaceholders(valueKey, data);
                    }
                }
                
                table.addCell(new Cell().add(new Paragraph(value)));
            }
        }
        
        document.add(table);
    }
    
    /**
     * Render image element
     */
    private void renderImageElement(Document document, Map<String, Object> element, Map<String, Object> data) {
        Map<String, Object> properties = getElementProperties(element);
        
        String src = (String) properties.get("src");
        if (src == null) {
            logger.warn("Image element has no src property");
            return;
        }
        
        src = replacePlaceholders(src, data);
        
        try {
            // For now, we'll skip actual image rendering as it requires file handling
            // This will be enhanced in later tasks
            Paragraph imagePlaceholder = new Paragraph("[IMAGE: " + src + "]")
                .setTextAlignment(TextAlignment.CENTER)
                .setItalic();
            document.add(imagePlaceholder);
            
        } catch (Exception e) {
            logger.error("Failed to render image: {}", src, e);
            // Add placeholder text instead
            Paragraph errorPlaceholder = new Paragraph("[IMAGE ERROR: " + src + "]")
                .setTextAlignment(TextAlignment.CENTER);
            document.add(errorPlaceholder);
        }
    }
    
    /**
     * Render container element (contains child elements)
     */
    private void renderContainerElement(Document document, Map<String, Object> element, Map<String, Object> data) {
        List<Map<String, Object>> children = getContainerChildren(element);
        
        for (Map<String, Object> child : children) {
            renderElement(document, child, data);
        }
    }
    
    /**
     * Get element properties
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> getElementProperties(Map<String, Object> element) {
        Object properties = element.get("properties");
        if (properties instanceof Map) {
            return (Map<String, Object>) properties;
        }
        return Map.of();
    }
    
    /**
     * Get table columns from properties
     */
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> getTableColumns(Map<String, Object> properties) {
        Object columns = properties.get("columns");
        if (columns instanceof List) {
            return (List<Map<String, Object>>) columns;
        }
        return List.of();
    }
    
    /**
     * Get table rows from properties
     */
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> getTableRows(Map<String, Object> properties) {
        Object rows = properties.get("rows");
        if (rows instanceof List) {
            return (List<Map<String, Object>>) rows;
        }
        return List.of();
    }
    
    /**
     * Get container children
     */
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> getContainerChildren(Map<String, Object> element) {
        Object children = element.get("children");
        if (children instanceof List) {
            return (List<Map<String, Object>>) children;
        }
        return List.of();
    }
    
    /**
     * Get integer value from map with default
     */
    private int getIntValue(Map<String, Object> map, String key, int defaultValue) {
        Object value = map.get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return defaultValue;
    }
    
    /**
     * Apply text formatting to paragraph
     */
    private void applyTextFormatting(Paragraph paragraph, Map<String, Object> properties) {
        // Font size
        Object fontSize = properties.get("fontSize");
        if (fontSize instanceof Number) {
            paragraph.setFontSize(((Number) fontSize).floatValue());
        }
        
        // Font weight
        String fontWeight = (String) properties.get("fontWeight");
        if ("bold".equalsIgnoreCase(fontWeight)) {
            paragraph.setBold();
        }
        
        // Text alignment
        String textAlign = (String) properties.get("textAlign");
        if (textAlign != null) {
            switch (textAlign.toLowerCase()) {
                case "center":
                    paragraph.setTextAlignment(TextAlignment.CENTER);
                    break;
                case "right":
                    paragraph.setTextAlignment(TextAlignment.RIGHT);
                    break;
                case "justify":
                    paragraph.setTextAlignment(TextAlignment.JUSTIFIED);
                    break;
                default:
                    paragraph.setTextAlignment(TextAlignment.LEFT);
                    break;
            }
        }
    }
    
    /**
     * Replace placeholders in text with actual data
     */
    private String replacePlaceholders(String text, Map<String, Object> data) {
        if (text == null || data == null) {
            return text;
        }
        
        // Pattern to match {{field_name}} placeholders
        Pattern pattern = Pattern.compile("\\{\\{([^}]+)\\}\\}");
        Matcher matcher = pattern.matcher(text);
        
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            String fieldName = matcher.group(1).trim();
            Object value = data.get(fieldName);
            String replacement = value != null ? value.toString() : "";
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(result);
        
        return result.toString();
    }
    
    /**
     * Generate basic content when schema parsing fails
     */
    private void generateBasicContent(Document document, Map<String, Object> data) {
        logger.debug("Generating basic fallback content");
        
        // Add title
        String title = (String) data.getOrDefault("title", "Generated Document");
        Paragraph titleParagraph = new Paragraph(title)
            .setTextAlignment(TextAlignment.CENTER)
            .setFontSize(18)
            .setBold();
        document.add(titleParagraph);
        
        // Add data as simple table
        if (!data.isEmpty()) {
            Table dataTable = new Table(UnitValue.createPercentArray(new float[]{30, 70}))
                .setWidth(UnitValue.createPercentValue(100));
            
            dataTable.addHeaderCell(new Cell().add(new Paragraph("Alan").setBold()));
            dataTable.addHeaderCell(new Cell().add(new Paragraph("Değer").setBold()));
            
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                if (!"title".equals(entry.getKey())) { // Skip title as it's already added
                    addTableRow(dataTable, entry.getKey(), entry.getValue());
                }
            }
            
            document.add(dataTable);
        }
    }
}