package ru.klodmit.s21_community_bot.bot;

import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.klodmit.s21_community_bot.commands.Command;
import ru.klodmit.s21_community_bot.services.CommandContainer;
import ru.klodmit.s21_community_bot.services.SendMessageToThreadServiceImpl;

@Component
public class BotMain extends TelegramLongPollingBot {
    public static final String COMMAND_PREFIX = "/";


    @Value("${bot.username}")
    private String username;

    @Value("${bot.token}")
    private String token;

    private final CommandContainer commandContainer;


    public BotMain() {
        super("7001537895:AAFS8lJnIz8U1-WhOyf_BWW4htWA6XQnAOM");
        this.commandContainer = new CommandContainer(new SendMessageToThreadServiceImpl(this), this);
    }

    @PostConstruct
    public void init() {
        System.out.println("Bot initialized and running.");
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(@NotNull Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText().trim();
            if (message.startsWith(COMMAND_PREFIX)) {
                String[] splitMessage = message.split(" ", 2);
                String commandIdentifier = splitMessage[0].toLowerCase();
                String arguments = splitMessage.length > 1 ? splitMessage[1] : null; // Используем null, если аргументы отсутствуют
                Command command = commandContainer.retrieveCommand(commandIdentifier);

                if (command != null) {
                    command.execute(update, arguments); // Передаем аргументы или null
                }
            }

        }
    }

    @Override
    public String getBotUsername() {
        return "verification_school_account_bot";
    }

}
