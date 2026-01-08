package by.baykulbackend.database.repository.user;

import by.baykulbackend.database.dao.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface IUserRepository extends JpaRepository<User, UUID> {
    User findByLogin(String login);
    User findByEmail(String email);
    User findByPhoneNumber(String phoneNumber);
    List<User> findByLoginContainingIgnoreCase(String login);
    List<User> findByEmailContainingIgnoreCase(String email);
    List<User> findByPhoneNumberContaining(String phoneNumber);
}
