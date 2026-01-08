package by.baykulbackend.database.repository.user;

import by.baykulbackend.database.dao.user.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface IProfileRepository extends JpaRepository<Profile, UUID> {
    List<Profile> findBySurnameContainingIgnoreCase(String surname);
    List<Profile> findByNameContainingIgnoreCase(String name);
}
