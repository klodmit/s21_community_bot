package ru.klodmit.s21_community_bot.commands;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.klodmit.s21_community_bot.services.SendMessageToThreadService;

public class FaqCommand implements Command {

    private final SendMessageToThreadService sendMessageToThreadService;

    public final static String FAQ_MESSAGE = "https://telegra.ph/School-21-Core-Camp-FAQ-11-05";

    public FaqCommand(SendMessageToThreadService sendMessageToThreadService) {
        this.sendMessageToThreadService = sendMessageToThreadService;
    }

    @Override
    public void execute(Update update, String args) throws TelegramApiException {
        if (args == null || args.isEmpty()) {
            sendMessageToThreadService.sendMessage(update.getMessage().getChatId().toString(),update.getMessage().getMessageThreadId(),FAQ_MESSAGE, "MarkdownV2");
        }
    }
}
