package ru.klodmit.s21_community_bot.dto;

/**
 * @param schoolLogin Новое поле для хранения schoolLogin
 */
public record VerificationInfo(Integer messageId, String verificationCode, String schoolLogin) {
}