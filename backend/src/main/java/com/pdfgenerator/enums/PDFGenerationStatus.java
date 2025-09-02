package com.pdfgenerator.enums;

public enum PDFGenerationStatus {
    PENDING("Beklemede"),
    PROCESSING("İşleniyor"),
    COMPLETED("Tamamlandı"),
    FAILED("Başarısız"),
    CANCELLED("İptal Edildi");

    private final String displayName;

    PDFGenerationStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}