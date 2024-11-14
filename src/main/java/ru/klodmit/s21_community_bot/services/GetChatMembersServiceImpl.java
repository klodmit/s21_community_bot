package ru.klodmit.s21_community_bot.services;

import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatAdministrators;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

public class GetChatMembersServiceImpl implements GetChatMembersService {
    private final TelegramLongPollingBot bot;
    public GetChatMembersServiceImpl(TelegramLongPollingBot bot) {
        this.bot = bot;
    }

    @Override
    public List<ChatMember> fetchAdmins(Long chatId) throws TelegramApiException {
        GetChatAdministrators getChatAdministrators = new GetChatAdministrators(chatId.toString());
        return bot.execute(getChatAdministrators);
    }

    @SneakyThrows
    @Override
    public ChatMember getChatMember(Long chatId, Long userId) {
        GetChatMember getChatMember = new GetChatMember(chatId.toString(), userId);
        return  bot.execute(getChatMember);
    }
    @SneakyThrows
    @Override
    @Deprecated
    public Long getUserIdByUsername(Long chatId, String username) {
        List<ChatMember> members = bot.execute(new GetChatAdministrators(chatId.toString()));
        for (ChatMember member : members) {
            if (member.getUser().getUserName().equalsIgnoreCase(username)) {
                return member.getUser().getId();
            }
        }
        return null;
    }
}
