package ru.klodmit.s21_community_bot.commands;

import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import ru.klodmit.s21_community_bot.services.GetChatMembersService;
import ru.klodmit.s21_community_bot.services.SendMessageToThreadService;

public class SaveCommand implements Command{
    private final TelegramLongPollingBot bot;
    private final SendMessageToThreadService sendMessageToThreadService;
    private final GetChatMembersService getChatMembersService;

    public SaveCommand(SendMessageToThreadService sendMessageToThreadService,
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
                Long targetUserId = update.getMessage().getReplyToMessage().getFrom().getId();
                sendMessageToThreadService.sendMessage(chatId.toString(), update.getMessage().getMessageThreadId(), "Пользователь сохранен");
            }
        }
    }
}
