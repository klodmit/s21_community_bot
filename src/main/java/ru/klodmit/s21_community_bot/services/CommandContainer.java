package ru.klodmit.s21_community_bot.services;


import com.google.common.collect.ImmutableMap;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import ru.klodmit.s21_community_bot.commands.*;
import ru.klodmit.s21_community_bot.services.impl.GetChatMembersServiceImpl;

import java.util.function.Function;

import static ru.klodmit.s21_community_bot.commands.CommandName.*;


@Service
public class CommandContainer {
    private final ImmutableMap<String, Command> commandMap;

//    private final ImmutableMap<String, Function<Command, Boolean>> commandMapv2;


    public CommandContainer(SendMessageToThreadService sendMessageToThreadService, TelegramLongPollingBot bot) {
        GetChatMembersService getChatMembersService = new GetChatMembersServiceImpl(bot);

        commandMap = ImmutableMap.<String, Command>builder()
                .put(FAQ.getCommandName(), new FaqCommand(sendMessageToThreadService))
                .put(RULES.getCommandName(), new RulesCommand(sendMessageToThreadService))
                .put(BANAN.getCommandName(), new BananCommand(sendMessageToThreadService))
                .put(BAN.getCommandName(), new BanCommand(sendMessageToThreadService,getChatMembersService,bot))
//                .put(FIND.getCommandName(), new FindCommand())
                .put(ADMIN.getCommandName(), new AdminCommand(sendMessageToThreadService, getChatMembersService))
                .put(MUTE.getCommandName(), new MuteCommand(sendMessageToThreadService,getChatMembersService,bot))
                .put(SAVE.getCommandName(), new SaveCommand(sendMessageToThreadService,getChatMembersService,bot))
//                .put(VALIDATE.getCommandName(), new ValidateCommand(sendMessageToThreadService,getChatMembersService,bot,verificationByRocketChat))
                .put(WARN.getCommandName(), new WarnCommand(sendMessageToThreadService,getChatMembersService))
                .build();

    }
    public Command retrieveCommand(String commandIdentifier) {
        return commandMap.get(commandIdentifier);
    }


}
