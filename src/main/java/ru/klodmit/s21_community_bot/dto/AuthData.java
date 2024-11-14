package ru.klodmit.s21_community_bot.dto;

import lombok.Data;

@Data
public class AuthData {
    private String userId;
    private String authToken;
}