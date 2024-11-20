package ru.klodmit.s21_community_bot.bot;

import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.klodmit.s21_community_bot.commands.Command;
import ru.klodmit.s21_community_bot.services.CheckSchoolAccount;
import ru.klodmit.s21_community_bot.services.CommandContainer;
import ru.klodmit.s21_community_bot.services.SendMessageToThreadServiceImpl;
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

    public BotMain(SendMessageToThreadServiceImpl sendMessageService,
                   CheckSchoolAccount checkSchoolAccount,
                   CommandContainer commandContainer) {
        super(BOT_TOKEN);
        this.sendMessageService = sendMessageService;
        this.checkSchoolAccount = checkSchoolAccount;
        this.commandContainer = commandContainer;
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

        }


        if (update.hasMessage() && update.getMessage().hasText()) {
            Long chatId = update.getMessage().getChatId();
            String message = update.getMessage().getText().trim();
            Integer threadId = update.getMessage().getMessageThreadId();
            Long userId = update.getMessage().getFrom().getId();

            //TODO: Check verifyService remake it and refactor this sample
            if (threadId != null && threadId == 30147 && !update.getMessage().isCommand()) {
                // обработка запроса через школьный апи
                String schoolStatus = checkSchoolAccount.getUserStatus(message.toLowerCase());
                String schoolProgram = checkSchoolAccount.getUserProgram(message.toLowerCase());

//                    checkSchoolAccount(schoolProgram, schoolStatus, userId, message, chatId, threadId);


            }

            if (message.startsWith(COMMAND_PREFIX)) {
                String[] splitMessage = message.split(" ", 2);
                String commandIdentifier = splitMessage[0].toLowerCase();
                String arguments = splitMessage.length > 1 ? splitMessage[1] : null; // Используем null, если аргументы отсутствуют
                Command command = commandContainer.retrieveCommand(commandIdentifier);

                if (command != null) {
                    command.execute(update, arguments); // Передаем аргументы или null
                }
            }
//            handleUserReply(update);

        }
    }

    private void handleNewChatMember(Message message) {
        Long chatId = message.getChatId();
        message.getNewChatMembers().forEach(newUser -> {
            CompletableFuture.runAsync(() -> {

            });
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText(text);
            sendMessage.setParseMode("MarkdownV2");
            CompletableFuture.runAsync(() -> {
                try {
                    Integer sentMessageId = execute(sendMessage).getMessageId();
                    scheduleMessageDeletion(chatId, sentMessageId);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            });
        });
    }

    private void handleNewChatMember(Message message, Long userId, String userFirstName, Long chatId) {
        try {
            deleteMessage(chatId.toString(), message.getMessageId());

            String mentionText = mentionUser(userFirstName, userId);
            String text = String.format(
                    "Добро пожаловать, %s\nУ тебя есть 5 минут для того, чтобы указать свой школьный ник в топике [ID](https://t.me/c/1975595161/30147)",
                    mentionText
            );

            Integer sentMessageId = sendMessageService.sendMessage(chatId.toString(), text, "MarkdownV2");
            scheduleMessageDeletion(chatId, sentMessageId);
        } catch (Exception e) {
            log.error("Error in handleNewChatMember: {}", e.getMessage(), e);
        }
    }

    private void deleteMessage(String chatId, Integer messageId) throws TelegramApiException {
        execute(new DeleteMessage(chatId, messageId));
    }

    private void scheduleMessageDeletion(Long chatId, Integer messageId) {
        scheduler.schedule(() -> {
            // Удаляем сообщение асинхронно
            CompletableFuture.runAsync(() -> {
                DeleteMessage deleteMessage = new DeleteMessage(chatId.toString(), messageId);
                try {
                    execute(deleteMessage);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            });
        }, 5, TimeUnit.MINUTES);
    }

//    public void handleUserReply(Update update) {
//        if (update.hasMessage() && update.getMessage().getReplyToMessage() != null) {
//            Long userId = update.getMessage().getFrom().getId();
//            String userMessage = update.getMessage().getText();
//            Integer replyMessageId = update.getMessage().getReplyToMessage().getMessageId();
//
//            // Проверяем, что это ответ на наше сообщение с кодом
//            VerificationInfo verificationInfo;
//            if (verificationInfo != null && verificationInfo.getMessageId().equals(replyMessageId)) {
//                // Сравниваем код
//                if (userMessage.equals(verificationInfo.getVerificationCode())) {
//                    // Успешная верификация
//                    try {
//                        System.out.println("g");
//                        Integer sentId = sendMessageToThreadServiceImpl.sendMessage(update.getMessage().getChatId().toString(), update.getMessage().getMessageThreadId(), "Успешная верификация\\!");
//                        userService.saveUser(userId, verificationInfo.getSchoolLogin(), true);
//                        deleteMessage(update.getMessage().getChatId(), replyMessageId);
//                        deleteMessage(update.getMessage().getChatId(), update.getMessage().getMessageId());
//                        deleteMessage(update.getMessage().getChatId(), sentId);
//
//                    } catch (TelegramApiException e) {
//                        throw new RuntimeException(e);
//                    }
//                } else {
//                    // Неверный код
//                    try {
//                        System.out.println("b");
//                        sendMessage(update.getMessage().getChatId(), update.getMessage().getMessageThreadId(), "Неверный код\\. Попробуй еще раз\\.");
//                    } catch (TelegramApiException e) {
//                        throw new RuntimeException(e);
//                    }
//                }
//            }
//        }
//    }


    @Override
    public String getBotUsername() {
        return "verification_school_account_bot";
    }

    private String mentionUser(String userFirstName, Long userId) {
        String escapedFirstName = escapeMarkdownV2(userFirstName);
        return "[" + escapedFirstName + "](tg://user?id=" + userId.toString() + ")";
    }

    private String escapeMarkdownV2(String text) {
        String result = "";
        if (text != null) {// Список символов, которые нужно экранировать
            String[] specialChars = {"\\", ".", "_", "*", "[", "]", "(", ")", "~", ">", "#", "+", "-", "=", "|", "{", "}", "!"};
            for (String specialChar : specialChars) {
                text = text.replace(specialChar, "\\" + specialChar);
            }
            result = text;
        }
        return result;
    }

//    private void checkSchoolAccount(String schoolProgram, String schoolStatus, Long userId, String messageText, Long chatId, Integer threadId) throws TelegramApiException, InterruptedException {
//
//        if (schoolStatus.equals("ACTIVE") || schoolStatus.equals("TEMPORARY_BLOCKING") || schoolStatus.equals("FROZEN")) {
//            if ("Core program".equals(schoolProgram)) {
//                System.out.println(userId + " " + messageText.toLowerCase());
//                userService.saveUser(userId, messageText.toLowerCase());
//                String text = "Супер, твой ник есть на платформе";
//                Integer sentId = sendMessage(chatId, threadId, text);
//                sleep(10000);
//                deleteMessage(chatId, sentId);
//            } else {
//                String text = "Ты с интенсива и пока не являешься участником основного обучения, заходи как поступишь";
//                sendMessageAndBlock(userId, chatId, threadId, text);
//            }
//        } else if (schoolStatus.equals("EXPELLED") || "BLOCKED".equals(schoolStatus)) {
//            String text = "Ты заблокирован на платформе, поэтому не можешь присоединиться к чату\\.\nЕсли все\\-таки хочешь остаться в чате, напиши администрации";
//            sleep(10000);
//            sendMessageAndBlock(userId, chatId, threadId, text);
//        } else if (schoolStatus.equals("NOT_FOUND")) {
//            String text = "Мы не смогли найти твои данные на платформе\\.\nВведи корректные данные, иначе будешь заблокирован";
//            Integer sentMessageId = sendMessage(chatId, threadId, text);
//            scheduler.schedule(() -> {
//                try {
//                    if (hasSendMessageToTopic(newUserId)) {
//                        deleteMessage(chatId, sentMessageId);
//                    } else {
//                        banChatMember(chatId, userId);
//                        deleteMessage(chatId, sentMessageId);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }, 30, TimeUnit.SECONDS);
//        }
//
//    }

}
