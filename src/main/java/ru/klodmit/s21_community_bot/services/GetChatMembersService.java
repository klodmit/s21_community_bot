package ru.klodmit.s21_community_bot.services;

import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;

import java.util.List;

public interface GetChatMembersService {
    List<ChatMember> fetchAdmins(Long chatId) throws Exception;
    ChatMember getChatMember(Long chatId, Long userId) throws Exception;
    @Deprecated
    Long getUserIdByUsername(Long chatId, String username) throws Exception;
}
