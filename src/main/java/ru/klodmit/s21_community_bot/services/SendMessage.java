package ru.klodmit.s21_community_bot.services;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import ru.klodmit.s21_community_bot.dto.SendMessageDto;


@FeignClient(
        url = "https://rocketchat-student.21-school.ru/api/v1",
        name = "sendMessage"
)
public interface SendMessage {
    @PostMapping(value = "/chat.postMessage")
    void sendMessage(@RequestBody SendMessageDto sendMessageDto,
                     @RequestHeader(name = "X-User-Id") String xUserId,
                     @RequestHeader(name = "X-Auth-Token") String xAuthToken);
}