package ru.klodmit.s21_community_bot.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.klodmit.s21_community_bot.dto.AuthDto;
import ru.klodmit.s21_community_bot.dto.SendMessageDto;
import ru.klodmit.s21_community_bot.dto.UserDto;

import java.util.Random;

@Service
public class VerificationByRocketChat {
    @Value("${school.username}")
    private String username;
    @Value("${school.password}")
    private String password;
    private SendMessage sendMessage;
    private AuthRequest authRequest;

    public VerificationByRocketChat(SendMessage sendMessage, AuthRequest authRequest) {
        this.sendMessage = sendMessage;
        this.authRequest = authRequest;
    }

    public String getAuth(String nickName) {
        UserDto userDto = UserDto.builder().username(username).password(password).build();
        System.out.println(userDto);
        AuthDto authDto = authRequest.getAccess(userDto);
        System.out.println(authDto);
        String generatedString = generateCode();
        SendMessageDto sendMessageDto = SendMessageDto.builder()
                .channel("@" + nickName)
                .text(generatedString)
                .build();
        System.out.println(sendMessageDto);
        sendMessage.sendMessage(sendMessageDto,
                authDto.getData().getUserId(),
                authDto.getData().getAuthToken());
        return generatedString;
    }

    private String generateCode() {
        int targetStringLength = 6;
        Random random = new Random();

        StringBuilder code = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int digit = random.nextInt(10);  // Генерация случайной цифры от 0 до 9
            code.append(digit);
        }

        return code.toString();
    }

}
