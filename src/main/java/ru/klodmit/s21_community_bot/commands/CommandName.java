package ru.klodmit.s21_community_bot.commands;

public enum CommandName {

    FIND("/find"),
    BAN("/ban"),
    FAQ("/faq"),
    RULES("/rules"),
    ADMIN("/admin"),
    SAVE("/save"),
    MUTE("/mute"),
    WARN("/warn"),
    VALIDATE("/validate"),
    BANAN("/banan"),
    CHECKCHAT("/checkchat"),
    BANUSERS("/banusers");


    private final String commandName;

    CommandName(String commandName) {
        this.commandName = commandName;
    }

    public String getCommandName() {
        return commandName;
    }

}
