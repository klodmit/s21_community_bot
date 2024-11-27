package ru.klodmit.s21_community_bot.util;

public final class Constants {
    public static final String RULES_MESSAGE = """
            Привет,
            
            Это первое сообщение, которое тебе надо прочитать для нахождения в этом чате.
            
            Вот некоторые правила, которые тебе нужно соблюдать:
            
            1. https://nometa.xyz/ru.html
            2. Никакой политики, религии и разжигания ненависти.
            3. Никаких серьезных оскорблений (в шутку с друзьями — пожалуйста, но не перебарщивайте).
            4. Мое сообщение поможет мне? Мое сообщение будет полезно адресату? Если "да" по двум вопросам, то пишите хоть хуй бобра.
            5. Не засоряйте тематические треды ненужной для них информацией. Хотите поспорить, что лучше — VSCode или Vim — шагайте во флудилку.
            6. По всем вопросам и спорам обращаться к админам.
            
            P.S. Иногда мы будем чистить чат, поэтому если ты с основы другого потока/выпустился/просто хочешь здесь сидеть и общаться, лучше предупреди об этом админов.
            
            Ко всему прочему, будьте добры к друг другу. Все можно решить.
            """;
    public static final String MUTE_DEFAULT = "Неверный формат времени. Пример: /mute 5 часов";
    public static final String MUTE_MESSAGE_TEMPLATE = """
            Пользователь заглушен на %s %s
            """;
    public static final String JUST_DUMB = """
            Ты заглушил сам себя на день, молодец.
            """;
    public final static String BANAN_MESSAGE = "Тут мог быть бан\nДержи банан 🍌";
    public final static String FAQ_MESSAGE = "https://telegra.ph/School-21-Core-Camp-FAQ-11-05";
    public final static String DEFAULT_WELCOME_MESSAGE = "Добро пожаловать, %s\nУ тебя есть 5 минут для того, чтобы указать свой школьный ник в топике [ID](https://t.me/c/1975595161/30147)";
    public final static String WELCOME_MESSAGE = "С возвращением, %s";
}
