package ru.klodmit.s21_community_bot.bot;

import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.klodmit.s21_community_bot.commands.Command;
import ru.klodmit.s21_community_bot.services.CheckSchoolAccount;
import ru.klodmit.s21_community_bot.services.CommandContainer;
import ru.klodmit.s21_community_bot.services.impl.SendMessageToThreadServiceImpl;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class BotMain extends TelegramLongPollingBot {
    public static final String COMMAND_PREFIX = "/";
    private static final String BOT_USERNAME = System.getenv("BOT_NAME");
    private static final String BOT_TOKEN = System.getenv("BOT_TOKEN");

    private final SendMessageToThreadServiceImpl sendMessageService;
    private final CheckSchoolAccount checkSchoolAccount;
    private final CommandContainer commandContainer;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());

    public BotMain() {
        super(BOT_TOKEN);
        this.commandContainer = new CommandContainer(new SendMessageToThreadServiceImpl(this), this);
        this.sendMessageService = new SendMessageToThreadServiceImpl(this);
        this.checkSchoolAccount = new CheckSchoolAccount();
    }

    @PostConstruct
    public void init() {
        log.info("Bot initialized and running.");
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(@NotNull Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            if(messageHasNewChatMembers(message)) {
                handleNewChatMembersAsync(message);
            } else if (message.hasText()) {
                handleMessage(update);
            }
        }
    }

    private boolean messageHasNewChatMembers(Message message) {
        return message.getNewChatMembers() != null && !message.getNewChatMembers().isEmpty();
    }

    private void handleNewChatMembersAsync(Message message) {
        Long chatId = message.getChatId();

        message.getNewChatMembers().forEach(newUser -> CompletableFuture.runAsync(() -> handleNewChatMember(message, newUser.getId(), newUser.getFirstName(), chatId)).exceptionally(ex -> {
            log.error("Error handling new chat member asynchronously: {}", ex.getMessage(), ex);
            return null;
        }));
    }

    private void handleNewChatMember(Message message, Long userId, String userFirstName, Long chatId) {
        try {
            deleteMessage(chatId.toString(), message.getMessageId());

            String mentionText = mentionUser(userFirstName, userId);
            String text = String.format(
                    "Добро пожаловать, %s\nУ тебя есть 5 минут для того, чтобы указать свой школьный ник в топике [ID](https://t.me/c/1975595161/30147)",
                    mentionText
            );

            sendMessageService.sendMessageAsync(chatId.toString(), message.getMessageThreadId(), text, "MarkdownV2")
                    .thenAccept(sendMessageId -> scheduleMessageDeletion(chatId, sendMessageId)).exceptionally(ex -> {
                        log.error("Error sending welcome message asynchronously: {}", ex.getMessage(), ex);
                        return null;
                    });
        } catch (Exception e) {
            log.error("Error in handleNewChatMember: {}", e.getMessage(), e);
        }
    }

    private void handleMessage(Update update) throws TelegramApiException {
        Message message = update.getMessage();
        String text = message.getText().trim();

        if (isThreadMessage(message)) {
            handleThreadMessage(update);
        } else if (text.startsWith(COMMAND_PREFIX)) {
            handleCommand(update);
        }
        
    }

    private void handleThreadMessage(Update update) {
        Message message = update.getMessage();
        String text = message.getText().trim().toLowerCase();

        String schoolStatus = checkSchoolAccount.getUserStatus(text);
        String schoolProgram = checkSchoolAccount.getUserProgram(text);

        //TODO: add school account check
    }

    private void handleCommand(Update update) throws TelegramApiException {
        Message message = update.getMessage();
        String text = message.getText().trim();
        String[] splitMessage = text.split(" ", 2);

        String commandIdentifier = splitMessage[0].toLowerCase();
        String arguments = splitMessage.length > 1 ? splitMessage[1] : null;

        Command command = commandContainer.retrieveCommand(commandIdentifier);
        if (command != null) {
            command.execute(update, arguments);
        } else {
            log.warn("Unknown command: {}", commandIdentifier);
        }
    }

    private boolean isThreadMessage(Message message) {
        Integer messageThreadId = message.getMessageThreadId();
        return messageThreadId != null && messageThreadId.equals(message.getMessageThreadId()) && !message.isCommand();
    }

    private void deleteMessage(String chatId, Integer messageId) throws TelegramApiException {
        execute(new DeleteMessage(chatId, messageId));
    }

    private void scheduleMessageDeletion(Long chatId, Integer messageId) {
        scheduler.schedule(() -> {
            // Удаляем сообщение асинхронно
            CompletableFuture.runAsync(() -> {
                try {
                    deleteMessage(chatId.toString(), messageId);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            });
        }, 5, TimeUnit.MINUTES);
    }

    //TODO: MAKE HANDLE USER REPLY


    @Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }

    private String mentionUser(String userFirstName, Long userId) {
        String escapedFirstName = escapeMarkdownV2(userFirstName);
        return "[" + escapedFirstName + "](tg://user?id=" + userId.toString() + ")";
    }

    private String escapeMarkdownV2(String text) {
        if (text == null) {
            return "";
        }
        return text.replaceAll("([\\\\.\\-_\\*\\[\\]()~>`#\\+\\-=|{}!])", "\\\\$1");
    }


}
