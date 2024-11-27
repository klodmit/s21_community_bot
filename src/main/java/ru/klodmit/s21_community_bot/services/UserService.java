package ru.klodmit.s21_community_bot.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.klodmit.s21_community_bot.model.User;
import ru.klodmit.s21_community_bot.repos.UserRepository;

import java.util.Optional;


@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void saveUser(Long tgId, String schoolLogin) {
        User user = new User();
        user.setId(tgId);
        user.setSchoolName(schoolLogin);
        user.setIsAdmin(false);
        user.setIsValidated(false);

        userRepository.save(user);
    }

    public void saveUser(Long tgId, String schoolLogin, Boolean Validated) {
        User user = new User();
        user.setId(tgId);
        user.setSchoolName(schoolLogin);
        user.setIsAdmin(false);
        user.setIsValidated(true);

        userRepository.save(user);
    }
    @Deprecated
    public Long findUserBySchoolName(String schoolName) {
        User user = userRepository.findBySchoolName(schoolName);
        if (user != null) {
            return user.getId();
        } else {
            return null; // или бросить исключение, если пользователь не найден
        }
    }

    public boolean findUserById(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.isPresent();
    }
}