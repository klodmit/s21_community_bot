package ru.klodmit.s21_community_bot.commands;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Command interface for handling telegram-bot commands.
 */
public interface Command {

    /**
     * Main method, which is executing command logic.
     *
     * @param update provided {@link Update} object with all the needed data for command.
     * @param args arguments to commands like @username
     */
    void execute(Update update, String args) throws TelegramApiException;
}
