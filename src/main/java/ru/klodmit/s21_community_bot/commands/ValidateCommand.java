package ru.klodmit.s21_community_bot.commands;

import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import ru.klodmit.s21_community_bot.dto.VerificationInfo;
import ru.klodmit.s21_community_bot.services.GetChatMembersService;
import ru.klodmit.s21_community_bot.services.SendMessageToThreadService;
import ru.klodmit.s21_community_bot.services.VerificationByRocketChat;

import java.util.HashMap;

public class ValidateCommand implements Command{
    private final TelegramLongPollingBot bot;
    private final SendMessageToThreadService sendMessageToThreadService;
    private final GetChatMembersService getChatMembersService;
    private final VerificationByRocketChat verificationByRocketChat;
    private HashMap<Long, VerificationInfo> verificationStore = new HashMap<>();

    public ValidateCommand(SendMessageToThreadService sendMessageToThreadService,
                           GetChatMembersService getChatMembersService,
                           TelegramLongPollingBot bot, VerificationByRocketChat verificationByRocketChat) {
        this.sendMessageToThreadService = sendMessageToThreadService;
        this.getChatMembersService = getChatMembersService;
        this.bot = bot;
        this.verificationByRocketChat = verificationByRocketChat;
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
                String text = "Для валидации команды используй /validate ник на платформе";
                sendMessageToThreadService.sendMessage(chatId.toString(), update.getMessage().getMessageThreadId(), text);
            } else{
                String text = "Тебе в рокет чат был отправлен код. Пришли мне его ответом на это сообщение";
                Integer sentId = sendMessageToThreadService.sendMessage(chatId.toString(), update.getMessage().getMessageThreadId(), text);
                String verificationCode = verificationByRocketChat.getAuth(args);
                storeVerificationInfo(userId, sentId, verificationCode, args);
            }
        }
    }

    private void storeVerificationInfo(Long userId, Integer messageId, String verificationCode, String schoolLogin) {
        verificationStore.put(userId, new VerificationInfo(messageId, verificationCode, schoolLogin));
    }
}
