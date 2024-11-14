package ru.klodmit.s21_community_bot;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.klodmit.s21_community_bot.bot.BotMain;

@SpringBootApplication
public class S21CommunityBotApplication {
    @SneakyThrows
    public static void main(String[] args) {
        SpringApplication.run(S21CommunityBotApplication.class, args);
        TelegramBotsApi botsApi = null;
        botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(new BotMain());
    }

}

