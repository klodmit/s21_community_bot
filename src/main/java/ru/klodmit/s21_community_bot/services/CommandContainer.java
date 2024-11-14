package ru.klodmit.s21_community_bot.services;


import com.google.common.collect.ImmutableMap;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import ru.klodmit.s21_community_bot.commands.*;

import static ru.klodmit.s21_community_bot.commands.CommandName.*;


@Service
public class CommandContainer {
    private final ImmutableMap<String, Command> commandMap;

    public CommandContainer(SendMessageToThreadService sendMessageToThreadService, TelegramLongPollingBot bot) {
        getChatMembersService getChatAdministratorsService = new GetChatMembersServiceImpl(bot);
        commandMap = ImmutableMap.<String, Command>builder()
                .put(FAQ.getCommandName(), new FaqCommand(sendMessageToThreadService))
                .put(RULES.getCommandName(), new RulesCommand(sendMessageToThreadService))
                .put(BANAN.getCommandName(), new BananCommand(sendMessageToThreadService))
//                .put(FIND.getCommandName(), new FindCommand())
                .put(ADMIN.getCommandName(), new AdminCommand(sendMessageToThreadService, getChatAdministratorsService))
//                .put(MUTE.getCommandName(), new MuteCommand())
//                .put(SAVE.getCommandName(), new SaveCommand())
//                .put(VALIDATE.getCommandName(), new ValidateCommand())
//                .put(WARN.getCommandName(), new WarnCommand())
                .build();

    }
    public Command retrieveCommand(String commandIdentifier) {
        return commandMap.get(commandIdentifier);
    }


}
