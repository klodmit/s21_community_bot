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

public class MuteCommand implements Command{
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
    public void execute(Update update, String args){
        Long chatId = update.getMessage().getChatId();
        Long userId = update.getMessage().getFrom().getId();
        ChatMember userChatMember = getChatMembersService.getChatMember(chatId, userId);
        String status = userChatMember.getStatus();
        if (status.equals("administrator") || status.equals("creator")){
            if (args == null || args.isEmpty()){
                int defaultDuration = 1440;
                Long targetUserId = update.getMessage().getReplyToMessage().getFrom().getId();
                muteUser(chatId.toString(), targetUserId, defaultDuration);
                sendMessageToThreadService.sendMessage(chatId.toString(), update.getMessage().getMessageThreadId(), "Пользователь заглушен на 1 день");
            } else if (args !=null || args.startsWith("@")) {
                String[] parts = args.split(" ");
                if(parts.length == 2){
                    String timeStr = parts[0];
                    String durationStr = parts[1];
                    int durationInMinutes = parseDuration(timeStr, durationStr);
                    Long targetUserId = update.getMessage().getReplyToMessage().getFrom().getId();
                    muteUser(chatId.toString(), targetUserId, durationInMinutes);
                    sendMessageToThreadService.sendMessage(chatId.toString(), update.getMessage().getMessageThreadId(), "Пользователь заглушен на " + timeStr + " " + durationStr);
                }
            }
        } else {
            int defaultDuration = 1440;
            muteUser(chatId.toString(), userId, defaultDuration);
            sendMessageToThreadService.sendMessage(chatId.toString(), update.getMessage().getMessageThreadId(), "Ты заглушил сам себя на день, молодец.");
        }
    }

    private int parseDuration(String time, String duration) {
        int multiplier = 0;
        if (duration.equals("d")) {
            multiplier = 1440;
        } else if (duration.equals("h")) {
            multiplier = 60;
        } else if (duration.equals("m")) {
            multiplier = 1;
        } else if (duration.equals("lol")) {
            multiplier = 0;
        } else {
            return -1;
        }
        Integer timeDuration = Integer.valueOf(time);
        return timeDuration * multiplier;
    }

    @SneakyThrows
    public void muteUser(String chatId, Long userId, int duration){
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
