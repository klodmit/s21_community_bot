package ru.klodmit.s21_community_bot.services;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public interface SendMessageToThreadService {

    /**
     * Send message via telegram bot.
     *
     * @param chatId  provided chatId in which messages would be sent.
     * @param message provided message to be sent.
     */
    Integer sendMessage(String chatId,Integer ThreadId, String message) throws TelegramApiException;

}
