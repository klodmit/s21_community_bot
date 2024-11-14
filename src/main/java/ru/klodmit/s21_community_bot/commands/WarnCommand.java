package ru.klodmit.s21_community_bot.commands;


import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import ru.klodmit.s21_community_bot.services.SendMessageToThreadService;
import ru.klodmit.s21_community_bot.services.GetChatMembersService;

public class WarnCommand implements Command{
    private final SendMessageToThreadService sendMessageToThreadService;
    private final GetChatMembersService getChatMembersService;
    public WarnCommand(SendMessageToThreadService sendMessageToThreadService,
                       GetChatMembersService getChatMembersService) {
        this.sendMessageToThreadService = sendMessageToThreadService;
        this.getChatMembersService = getChatMembersService;
    }
    private final static String WARN_MESSAGE = "Не нарушай правила";

    @SneakyThrows
    @Override
    public void execute(Update update, String args) {
        Long chatId = update.getMessage().getChatId();
        Long userId = update.getMessage().getFrom().getId();
        ChatMember userChatMember = getChatMembersService.getChatMember(chatId, userId);
        String status = userChatMember.getStatus();
        if (status.equals("administrator") || status.equals("creator")){
            if (args == null || args.isEmpty()){
                Long targetUserId = update.getMessage().getReplyToMessage().getFrom().getId();
                sendMessageToThreadService.sendMessage(chatId.toString(), update.getMessage().getMessageThreadId(), targetUserId.toString());
            } else if(args.startsWith("@")){
                Long targetUserId = getChatMembersService.getUserIdByUsername(chatId,args.substring(1));
                sendMessageToThreadService.sendMessage(chatId.toString(), update.getMessage().getMessageThreadId(), targetUserId.toString());
            }
        }

    }
}
