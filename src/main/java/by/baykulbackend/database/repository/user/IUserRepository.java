package by.baykulbackend.database.repository.user;

import by.baykulbackend.database.dao.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IUserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByLogin(String login);
    Optional<User> findByEmail(String email);
    Optional<User> findByPhoneNumber(String phoneNumber);
    List<User> findByLoginContainingIgnoreCase(String login);
    List<User> findByEmailContainingIgnoreCase(String email);
    List<User> findByPhoneNumberContaining(String phoneNumber);
}
