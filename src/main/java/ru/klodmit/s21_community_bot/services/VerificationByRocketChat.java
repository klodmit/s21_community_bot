package ru.klodmit.s21_community_bot.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.klodmit.s21_community_bot.dto.AuthByRocketChatDto;
import ru.klodmit.s21_community_bot.dto.SendMessageByRocketChat;
import ru.klodmit.s21_community_bot.dto.UserDto;

import java.util.Random;

@Service
public class VerificationByRocketChat {
    @Value("${school.username}")
    private String username = System.getenv("SCHOOL_USERNAME");
    private String password = System.getenv("ROCKET_CHAT_PASSWORD");
    private SendMessageByRocketChatService sendMessageByRocketChatService;
    private AuthRequestByRocketChatService authRequestByRocketChatService;

    public VerificationByRocketChat(SendMessageByRocketChatService sendMessageByRocketChatService, AuthRequestByRocketChatService authRequestByRocketChatService) {
        this.sendMessageByRocketChatService = sendMessageByRocketChatService;
        this.authRequestByRocketChatService = authRequestByRocketChatService;
    }

    public String getAuth(String nickName) {
        UserDto userDto = new UserDto(username,password);
        System.out.println(userDto);
        AuthByRocketChatDto authByRocketChatDto = authRequestByRocketChatService.getAccess(userDto);
        System.out.println(authByRocketChatDto);
        String generatedString = generateCode();
        SendMessageByRocketChat sendMessageByRocketChat = new SendMessageByRocketChat("@" + nickName,generatedString);
        System.out.println(sendMessageByRocketChat);
        sendMessageByRocketChatService.sendMessage(sendMessageByRocketChat,
                authByRocketChatDto.data().userId(),
                authByRocketChatDto.data().authToken());
        return generatedString;
    }

    private String generateCode() {
        int targetStringLength = 6;
        Random random = new Random();

        StringBuilder code = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int digit = random.nextInt(10);
            code.append(digit);
        }

        return code.toString();
    }

}
