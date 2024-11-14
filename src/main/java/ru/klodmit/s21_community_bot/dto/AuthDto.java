package ru.klodmit.s21_community_bot.dto;

import lombok.Data;

//TODO: Remake this shit and others dto
@Data
public class AuthDto {
    private String status;
    private AuthData data;
}