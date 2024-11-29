package ru.klodmit.s21_community_bot.bot;

import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.groupadministration.BanChatMember;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.klodmit.s21_community_bot.commands.Command;
import ru.klodmit.s21_community_bot.services.CheckSchoolAccount;
import ru.klodmit.s21_community_bot.services.CommandContainer;
import ru.klodmit.s21_community_bot.services.UserService;
import ru.klodmit.s21_community_bot.services.impl.SendMessageToThreadServiceImpl;

import java.util.concurrent.*;

import static ru.klodmit.s21_community_bot.util.Constants.DEFAULT_WELCOME_MESSAGE;


@Slf4j
@Component
public class BotMain extends TelegramLongPollingBot {
    public static final String COMMAND_PREFIX = "/";
    //    System.getenv("BOT_NAME");
//    System.getenv("BOT_TOKEN"); РАСКОММЕНТИРОВАТЬ КОГДА БУДЕШЬ ПУШИТЬ
    private static final String BOT_USERNAME = System.getenv("BOT_NAME");
    private static final String BOT_TOKEN = System.getenv("BOT_TOKEN");
    private final SendMessageToThreadServiceImpl sendMessageService;
    private final CheckSchoolAccount checkSchoolAccount;
    private final CommandContainer commandContainer;
    private final UserService userService;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
    private final ExecutorService executorService = Executors.newFixedThreadPool(100);


    public BotMain(
            CheckSchoolAccount checkSchoolAccount,
            UserService userService) {
        super(BOT_TOKEN);
        this.sendMessageService = new SendMessageToThreadServiceImpl(this);
        this.checkSchoolAccount = checkSchoolAccount;
        this.commandContainer = new CommandContainer(sendMessageService, this);
        this.userService = userService;
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
            if (messageHasNewChatMembers(message)) {
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
            if (userService.findUserById(userId)) {
                sendMessageService.sendMessageAsync(chatId.toString(), message.getMessageThreadId(), "С возвращением, " + mentionText)
                        .thenAccept(sendMessageId -> scheduleMessageDeletion(chatId, sendMessageId, 300)).exceptionally(ex -> {
                            log.error("Error sending welcome message asynchronously: {}", ex.getMessage(), ex);
                            return null;
                        });
            } else {
                sendMessageService.sendMessageAsync(chatId.toString(), message.getMessageThreadId(), DEFAULT_WELCOME_MESSAGE.formatted(mentionText))
                        .thenAccept(sendMessageId -> scheduleMessageDeletion(chatId, sendMessageId, 300)).exceptionally(ex -> {
                            log.error("Error sending welcome message asynchronously: {}", ex.getMessage(), ex);
                            return null;
                        });
            }

        } catch (Exception e) {
            log.error("Error in handleNewChatMember: {}", e.getMessage(), e);
        }
    }

    private void handleMessage(Update update) throws TelegramApiException {
        Message message = update.getMessage();
        String text = message.getText().trim();

        if (isThreadMessage(message) && (message.getMessageThreadId() == 3 || message.getMessageThreadId() == 30147)) {
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
        checkSchoolAccountAsync(schoolProgram, schoolStatus, message.getFrom().getId(),
                text, message.getChatId(),
                message.getMessageThreadId(),
                message.getFrom().getFirstName());
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

    @SneakyThrows
    private void banChatMember(String chatId, Long userId) {
        execute(new BanChatMember(chatId, userId));
    }

    private void scheduleMessageDeletion(Long chatId, Integer messageId, int seconds) {
        scheduler.schedule(() -> {
            CompletableFuture.runAsync(() -> {
                try {
                    deleteMessage(chatId.toString(), messageId);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            });
        }, seconds, TimeUnit.SECONDS);
    }

    private void scheduleBanChatMember(Long chatId, Long userId, int seconds) {
        scheduler.schedule(() -> {
            CompletableFuture.runAsync(() -> {
                banChatMember(chatId.toString(), userId);
            });
        }, seconds, TimeUnit.SECONDS);
    }

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
        String[] specialChars = {"\\", ".", "_", "*", "[", "]", "(", ")", "~", ">", "#", "+", "-", "=", "|", "{", "}", "!"};
        for (String specialChar : specialChars) {
            text = text.replace(specialChar, "\\" + specialChar);
        }
        return text;
    }

    @SneakyThrows
    private CompletableFuture<Void> checkSchoolAccountAsync(String schoolProgram, String schoolStatus,
                                                            Long userId, String messageText, Long chatId,
                                                            Integer threadId, String userFirstName) {
        return CompletableFuture.runAsync(() -> {
            // Проверка статуса школы
            if ("ACTIVE".equals(schoolStatus) || "TEMPORARY_BLOCKING".equals(schoolStatus) || "FROZEN".equals(schoolStatus)) {
                handleActiveUser(schoolProgram, userId, messageText, chatId, threadId, userFirstName);
            } else if ("EXPELLED".equals(schoolStatus) || "BLOCKED".equals(schoolStatus)) {
                handleBlockedUser(userId, chatId, threadId, userFirstName);
            } else if ("NOT_FOUND".equals(schoolStatus)) {
                handleNotFoundUser(userId, chatId, threadId, userFirstName);
            }
        }, executorService); // Используем пул потоков для асинхронного выполнения
    }

    @SneakyThrows
    private void handleActiveUser(String schoolProgram, Long userId, String messageText, Long chatId, Integer threadId, String userFirstName) {
        if ("Core program".equals(schoolProgram)) {
            System.out.println(userId + " " + messageText.toLowerCase());
            userService.saveUser(userId, messageText.toLowerCase());
            String text = "Супер, твой ник есть на платформе";
            int sentMessage = sendMessageService.sendMessage(chatId.toString(), threadId, text, "MarkdownV2");
            scheduleMessageDeletion(chatId, sentMessage, 10);
            System.out.println(text);
        } else {
            String mentionText = mentionUser(userFirstName, userId);
            String text = mentionText + " Ты с интенсива и пока не являешься участником основного обучения, заходи как поступишь";
            System.out.println(text);
            int sentMessage = sendMessageService.sendMessage(chatId.toString(), threadId, text);
            scheduleMessageDeletion(chatId, sentMessage, 10);
            scheduleBanChatMember(chatId,userId,30);
        }
    }

    @SneakyThrows
    private void handleBlockedUser(Long userId, Long chatId, Integer threadId, String userFirstName) {
        String mentionText = mentionUser(userFirstName, userId);
        String text = mentionText + " Ты заблокирован на платформе, поэтому не можешь присоединиться к чату\\.\n" +
                "Если все-таки хочешь остаться в чате, напиши администрации";
        int sentMessage = sendMessageService.sendMessage(chatId.toString(), threadId, text);
        scheduleMessageDeletion(chatId, sentMessage, 10);
        scheduleBanChatMember(chatId,userId,30);
    }

    @SneakyThrows
    private void handleNotFoundUser(Long userId, Long chatId, Integer threadId, String userFirstName) {
        String mentionText = mentionUser(userFirstName, userId);
        String text = mentionText + "Мы не смогли найти твои данные на платформе\\.\nВведи корректные данные, иначе будешь заблокирован";
        int sentId = sendMessageService.sendMessage(chatId.toString(), threadId, text);
        deleteMessage(chatId.toString(), sentId);
        System.out.println(text);

        CompletableFuture.runAsync(() -> {
            System.out.println("Проверка и бан пользователя через 30 секунд...");
            // if (hasSendMessageToTopic(userId)) {
            //     deleteMessage(chatId, sentMessageId);
            // } else {
            //     banChatMember(chatId, userId);
            //     deleteMessage(chatId, sentMessageId);
            // }
        }, CompletableFuture.delayedExecutor(30, TimeUnit.SECONDS));
    }


}
