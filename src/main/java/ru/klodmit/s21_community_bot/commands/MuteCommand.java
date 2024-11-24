package ru.klodmit.s21_community_bot.commands;

import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.groupadministration.RestrictChatMember;
import org.telegram.telegrambots.meta.api.objects.ChatPermissions;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import ru.klodmit.s21_community_bot.services.GetChatMembersService;
import ru.klodmit.s21_community_bot.services.SendMessageToThreadService;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class MuteCommand implements Command {
    private final TelegramLongPollingBot bot;
    private final SendMessageToThreadService sendMessageToThreadService;
    private final GetChatMembersService getChatMembersService;

    public MuteCommand(SendMessageToThreadService sendMessageToThreadService,
                       GetChatMembersService getChatMembersService,
                       TelegramLongPollingBot bot) {
        this.sendMessageToThreadService = sendMessageToThreadService;
        this.getChatMembersService = getChatMembersService;
        this.bot = bot;
    }

    @SneakyThrows
    @Override
    public void execute(Update update, String args) {
        Long chatId = update.getMessage().getChatId();
        Long userId = update.getMessage().getFrom().getId();
        ChatMember userChatMember = getChatMembersService.getChatMember(chatId, userId);
        String status = userChatMember.getStatus();
        String cause = "Спам";
        if (status.equals("administrator") || status.equals("creator")) {
            if (args == null || args.isEmpty()) {
                int defaultDuration = 1440;
                Long targetUserId = update.getMessage().getReplyToMessage().getFrom().getId();

                muteUser(chatId.toString(), targetUserId, defaultDuration);
                sendMessageToThreadService.sendMessage(chatId.toString(), update.getMessage().getMessageThreadId(), "Пользователь заглушен на 1 день, причина: " + cause, "MarkdownV2");
            } else {
                String[] parts = args.split(" ");
                if (parts.length == 1) {
                    String timeStr = parts[0];
                    String durationStr = "null";
                    timeProcessing(update, timeStr, durationStr, chatId, cause);
                } else if (parts.length == 2) {
                    String timeStr = parts[0];
                    String durationStr = parts[1];
                    timeProcessing(update, timeStr, durationStr, chatId, cause);
                } else if (parts.length == 3) {
                    String timeStr = parts[0];
                    String durationStr = parts[1];
                    cause = parts[2];
                    timeProcessing(update, timeStr, durationStr, chatId, cause);
                }
            }
        } else {
            int defaultDuration = 1440;
            muteUser(chatId.toString(), userId, defaultDuration);
            sendMessageToThreadService.sendMessage(chatId.toString(), update.getMessage().getMessageThreadId(), "Ты заглушил сам себя на день, молодец.", "MarkdownV2");
        }
    }

    private void timeProcessing(Update update, String timeStr, String durationStr, Long chatId, String cause) {
        int durationInMinutes = parseDuration(timeStr, durationStr);
        Long targetUserId = update.getMessage().getReplyToMessage().getFrom().getId();
        if (durationInMinutes < 0) {
            sendMessageToThreadService.sendMessage(chatId.toString(), update.getMessage().getMessageThreadId(), "Неверный формат времени. Пример: /mute 5 часов спам", "MarkdownV2");
        } else {
            muteUser(chatId.toString(), targetUserId, durationInMinutes);
            sendMessageToThreadService.sendMessage(chatId.toString(), update.getMessage().getMessageThreadId(), "Пользователь заглушен на " + timeStr + " " + durationStr + ", причина: "+ cause, "MarkdownV2");
        }
    }

    private int parseDuration(String time, String duration) {
        int multiplier;
        switch (duration) {
            case "день", "дня", "дней" -> multiplier = 1440;
            case "час", "часа", "часов" -> multiplier = 60;
            case "минута", "минуты", "минут", "мин" -> multiplier = 1;
            case "lol" -> multiplier = 0;
            default -> {
                return -1;
            }
        }
        int timeDuration = Integer.parseInt(time);
        return timeDuration * multiplier;
    }

    @SneakyThrows
    public void muteUser(String chatId, Long userId, int duration) {
        ChatPermissions permissions = ChatPermissions.builder()
                .canSendAudios(false)
                .canSendDocuments(false)
                .canSendMessages(false)
                .canSendAudios(false)
                .canSendPolls(false)
                .canSendPhotos(false)
                .canSendOtherMessages(false)
                .canSendVideoNotes(false)
                .canSendVideos(false)
                .canSendVoiceNotes(false)
                .build();


        RestrictChatMember restrictChatMember = RestrictChatMember.builder()
                .chatId(chatId)
                .userId(userId)
                .permissions(permissions)
                .untilDate((int) (Instant.now().getEpochSecond() + TimeUnit.MINUTES.toSeconds(duration)))
                .build();
        bot.execute(restrictChatMember);
    }
}
