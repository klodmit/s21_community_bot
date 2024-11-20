package ru.klodmit.s21_community_bot.commands;

import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import ru.klodmit.s21_community_bot.services.GetChatMembersService;
import ru.klodmit.s21_community_bot.services.SendMessageToThreadService;

import java.util.List;

public class AdminCommand implements Command {
    private final SendMessageToThreadService sendMessageToThreadService;
    private final GetChatMembersService getChatMembersService;

    public AdminCommand(SendMessageToThreadService sendMessageToThreadService,
                        GetChatMembersService getChatMembersService) {
        this.sendMessageToThreadService = sendMessageToThreadService;
        this.getChatMembersService = getChatMembersService;
    }

    @SneakyThrows
    @Override
    public void execute(Update update, String args) {
        if (args == null || args.isEmpty()) {
            Long chatId = update.getMessage().getChatId();
            Long userId = update.getMessage().getFrom().getId();

            ChatMember userChatMember = getChatMembersService.getChatMember(chatId, userId);
            String status = userChatMember.getStatus();
            if (status.equals("administrator") || status.equals("creator")) {
                StringBuilder adminList = new StringBuilder("Список администраторов:\n");
                try {
                    List<ChatMember> admins = getChatMembersService.fetchAdmins(chatId);
                    admins.forEach(admin -> {
                        String firstName = admin.getUser().getFirstName();
                        String userName = admin.getUser().getUserName();
                        adminList.append(firstName)
                                .append(" (")
                                .append(userName != null ? userName : "без имени пользователя")
                                .append(")\n");
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    adminList.append("Не удалось получить список администраторов.");
                }
                sendMessageToThreadService.sendMessage(
                        chatId.toString(),
                        update.getMessage().getMessageThreadId(),
                        adminList.toString(),
                        "MarkdownV2"
                );
            }
        }
    }
}
