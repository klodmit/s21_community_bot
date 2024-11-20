package ru.klodmit.s21_community_bot.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.klodmit.s21_community_bot.model.User;
import ru.klodmit.s21_community_bot.model.WhiteList;
import ru.klodmit.s21_community_bot.repos.WhitelistInterface;


@Service
public class WhiteListService {
    private final WhitelistInterface whitelistInterface;

    @Autowired
    public WhiteListService(WhitelistInterface whiteListInterface) {
        this.whitelistInterface = whiteListInterface;
    }

    public void saveUser(Long tgId) {
        WhiteList whitelist = new WhiteList();
        User user = new User();
        user.setId(tgId);
        whitelist.setUser(user);
        whitelistInterface.save(whitelist);
    }
    public boolean findByTgId(Long tgId) {
        return whitelistInterface.findByUser_Id(tgId).map(WhiteList::getUser).isPresent();
    }

}