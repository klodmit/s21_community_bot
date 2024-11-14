package ru.klodmit.s21_community_bot.services;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.klodmit.s21_community_bot.dto.AuthDto;
import ru.klodmit.s21_community_bot.dto.UserDto;


@FeignClient(
        url = "https://rocketchat-student.21-school.ru/api/v1",
        name = "getAuth"
)
public interface AuthRequest {
    @PostMapping(value = "/login")
    AuthDto getAccess(@RequestBody UserDto userDto);
}