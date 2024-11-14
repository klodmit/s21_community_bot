package ru.klodmit.s21_community_bot.repos;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.klodmit.s21_community_bot.model.User;


public interface UserRepository extends JpaRepository<User, Long> {
    User findBySchoolName(String schoolName);
}
