package ru.klodmit.s21_community_bot.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.klodmit.s21_community_bot.model.WhiteList;


import java.util.Optional;

public interface WhitelistInterface extends JpaRepository<WhiteList, Integer> {
    Optional<WhiteList> findByUser_Id(Long tgId);
}
