package ru.klodmit.s21_community_bot.services;

import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.klodmit.s21_community_bot.bot.BotMain;

@Service
public class SendMessageToThreadServiceImpl implements SendMessageToThreadService {

    private final BotMain botMain;

    public SendMessageToThreadServiceImpl(BotMain botMain) {
        this.botMain = botMain;
    }
    @SneakyThrows
    @Override
    public void sendMessage(String chatId, Integer threadId, String message) {
        String text = escapeMarkdownV2(message);
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .messageThreadId(threadId)
                .text(text)
                .parseMode("MarkdownV2")
                .build();
        botMain.execute(sendMessage);
    }

    private String escapeMarkdownV2(String text) {
        if (text == null) {
            return "";
        }
        // Список символов, которые нужно экранировать
        String[] specialChars = {"\\", ".", "_", "*", "[", "]", "(", ")", "~", ">", "#", "+", "-", "=", "|", "{", "}", "!"};
        for (String specialChar : specialChars) {
            text = text.replace(specialChar, "\\" + specialChar);
        }
        return text;
    }

}
