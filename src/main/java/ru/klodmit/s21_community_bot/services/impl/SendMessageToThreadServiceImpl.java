package ru.klodmit.s21_community_bot.services.impl;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.klodmit.s21_community_bot.bot.BotMain;
import ru.klodmit.s21_community_bot.services.SendMessageToThreadService;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class SendMessageToThreadServiceImpl implements SendMessageToThreadService {

    private final BotMain botMain;

    public SendMessageToThreadServiceImpl(BotMain botMain) {
        this.botMain = botMain;
    }

    @SneakyThrows
    @Override
    public Integer sendMessage(String chatId, Integer threadId, String message, String parseMode) {
        String text = escapeMarkdownV2(message);
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .messageThreadId(threadId)
                .text(text)
                .parseMode(parseMode)
                .build();
        return botMain.execute(sendMessage).getMessageId();
    }
    @SneakyThrows
    @Override
    public Integer sendMessage(String chatId, Integer threadId, String message) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .messageThreadId(threadId)
                .text(message)
                .parseMode("MarkdownV2")
                .build();
        return botMain.execute(sendMessage).getMessageId();
    }

    @Override
    @Async
    public CompletableFuture<Integer> sendMessageAsync(String chatId, Integer threadId, String message, String parseMode){
        try {
            String text = escapeMarkdownV2(message);
            SendMessage sendMessage = SendMessage.builder()
                    .chatId(chatId)
                    .messageThreadId(threadId)
                    .text(text)
                    .parseMode(parseMode)
                    .build();

            Integer messageId = botMain.execute(sendMessage).getMessageId();
            return CompletableFuture.completedFuture(messageId);
        } catch (TelegramApiException e) {
            log.error("Error sending message asynchronously: {}", e.getMessage(), e);
            return CompletableFuture.failedFuture(e);
        }
    }

    @Async
    @Override
    public CompletableFuture<Integer> sendMessageAsync(String chatId, Integer threadId, String message){
        try {

            SendMessage sendMessage = SendMessage.builder()
                    .chatId(chatId)
                    .messageThreadId(threadId)
                    .text(message)
                    .parseMode("MarkdownV2")
                    .build();

            Integer messageId = botMain.execute(sendMessage).getMessageId();
            return CompletableFuture.completedFuture(messageId);
        } catch (TelegramApiException e) {
            log.error("Error sending message asynchronously: {}", e.getMessage(), e);
            return CompletableFuture.failedFuture(e);
        }
    }

    private String escapeMarkdownV2(String text) {
        if (text == null) {
            return "";
        }
        return text.replaceAll("([\\\\.\\-_\\*\\[\\]()~>`#\\+\\-=|{}!])", "\\\\$1");
    }

}
