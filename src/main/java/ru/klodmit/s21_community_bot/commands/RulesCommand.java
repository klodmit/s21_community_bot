package ru.klodmit.s21_community_bot.commands;


import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.klodmit.s21_community_bot.services.SendMessageToThreadService;

import static ru.klodmit.s21_community_bot.util.Constants.RULES_MESSAGE;

public class RulesCommand implements Command {
    private final SendMessageToThreadService sendMessageToThreadService;


    public RulesCommand(SendMessageToThreadService sendMessageToThreadService) {
        this.sendMessageToThreadService = sendMessageToThreadService;
    }

    @SneakyThrows
    @Override
    public void execute(Update update, String args) {
        if (args == null || args.isEmpty()) {
            sendMessageToThreadService.sendMessage(update.getMessage().getChatId().toString(), update.getMessage().getMessageThreadId(), RULES_MESSAGE.formatted("[NOMETA](https://nometa.xyz/ru.html)"));
        }
    }
}
