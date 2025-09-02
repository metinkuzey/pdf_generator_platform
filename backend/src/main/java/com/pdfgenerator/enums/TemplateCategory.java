package com.pdfgenerator.enums;

public enum TemplateCategory {
    CREDIT_CARD_STATEMENT("Kredi Kartı Tahsilat Belgesi"),
    HEALTH_INSURANCE_POLICY("Sağlık Sigortası Poliçesi"),
    ACCOUNT_STATEMENT("Hesap Ekstresi"),
    PAYMENT_RECEIPT("Ödeme Makbuzu"),
    INVOICE("Fatura"),
    CONTRACT("Sözleşme"),
    REPORT("Rapor"),
    CERTIFICATE("Sertifika"),
    OTHER("Diğer");

    private final String displayName;

    TemplateCategory(String displayName) {
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