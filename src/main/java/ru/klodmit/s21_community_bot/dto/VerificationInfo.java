package ru.klodmit.s21_community_bot.dto;

public class VerificationInfo {
    private Integer messageId;
    private String verificationCode;
    private String schoolLogin;  // Новое поле для хранения schoolLogin

    public VerificationInfo(Integer messageId, String verificationCode, String schoolLogin) {
        this.messageId = messageId;
        this.verificationCode = verificationCode;
        this.schoolLogin = schoolLogin;
    }

    public Integer getMessageId() {
        return messageId;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public String getSchoolLogin() {
        return schoolLogin;
    }
}