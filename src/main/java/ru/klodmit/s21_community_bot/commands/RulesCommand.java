package ru.klodmit.s21_community_bot.commands;


import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.klodmit.s21_community_bot.services.SendMessageToThreadService;

public class RulesCommand implements Command {
    private final SendMessageToThreadService sendMessageToThreadService;
    public final static String RULES_MESSAGE = "Привет,\n" +
            "\n" +
            "Это первое сообщение, которое тебе надо прочитать для нахождения в этом чате.\n" +
            "\n" +
            "Вот некоторые правила, которые тебе нужно соблюдать:\n" +
            "\n" +
            "1. https://nometa.xyz/ru.html\n" +
            "2. Никакой политики, религии и разжигания ненависти. \n" +
            "3. Никаких серьезных оскорблений (в шутку с друзьями - пожалуйста, но не перебарщивайте)\n" +
            "4. Мое сообщение поможет мне ? Мое сообщение будет полезно адресату? Если \"да\" по двум вопросам, то пишите хоть хуй бобра\n" +
            "5. Не засоряйте тематические треды ненужной для них информацией. Хотите поспорить, что лучше - VSCode или Vim - шагайте во флудилку\n" +
            "6. По всем вопросам, спорам, обращаться к админам\n" +
            "\n" +
            "P.S. Иногда мы будем чистить чат, поэтому если ты с основы другого потока/выпустился/просто хочешь здесь сидеть и общаться, лучше предупреди об этом админов\n" +
            "\n" +
            "Ко всему прочему. Будьте добры к друг другу. Все можно решить.";

    public RulesCommand(SendMessageToThreadService sendMessageToThreadService) {
        this.sendMessageToThreadService = sendMessageToThreadService;
    }

    @SneakyThrows
    @Override
    public void execute(Update update, String args) {
        if (args == null || args.isEmpty()) {
            sendMessageToThreadService.sendMessage(update.getMessage().getChatId().toString(), update.getMessage().getMessageThreadId(), RULES_MESSAGE);
        }
    }
}
