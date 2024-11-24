package ru.klodmit.s21_community_bot;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.klodmit.s21_community_bot.bot.BotMain;

@SpringBootApplication
@EnableScheduling
@EnableAsync
@EnableFeignClients(basePackages = "ru.klodmit.s21_community_bot.services")
public class S21CommunityBotApplication {

    @SneakyThrows
    public static void main(String[] args) {
        var applicationContext = SpringApplication.run(S21CommunityBotApplication.class, args);

        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);

        // Получаем экземпляр BotMain из Spring Context
        BotMain botMain = applicationContext.getBean(BotMain.class);

        botsApi.registerBot(botMain);
    }

}

