package ru.klodmit.s21_community_bot.services;

import org.springframework.scheduling.annotation.Async;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.concurrent.CompletableFuture;

public interface SendMessageToThreadService {

    /**
     * Send message via telegram bot.
     *
     * @param chatId  provided chatId in which messages would be sent.
     * @param message provided message to be sent.
     */
    Integer sendMessage(String chatId, Integer threadId, String message, String parseMode);
    CompletableFuture<Integer> sendMessageAsync(String chatId, Integer threadId, String message, String parseMode);

    @Async
    CompletableFuture<Integer> sendMessageAsync(String chatId, Integer threadId, String message);
}
