package by.baykulbackend.database.repository.product;

import by.baykulbackend.database.dao.product.Part;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IPartRepository extends JpaRepository<Part, UUID> {
    Optional<Part> findByArticle(String article);
    boolean existsByArticle(String article);
    List<Part> findByName(String name);
    List<Part> findByBrand(String brand);
    List<Part> findByArticleContainingIgnoreCase(String article);
    List<Part> findByNameContainingIgnoreCase(String name);
    List<Part> findByBrandContainingIgnoreCase(String brand);
}
