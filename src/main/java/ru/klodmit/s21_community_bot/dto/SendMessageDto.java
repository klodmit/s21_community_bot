package ru.klodmit.s21_community_bot.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SendMessageDto {
    private String channel;
    private String text;
}
