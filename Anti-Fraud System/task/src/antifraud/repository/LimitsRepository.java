package antifraud.repository;

import antifraud.domain.Limits;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@EnableJpaRepositories
public interface LimitsRepository extends JpaRepository<Limits, String> {
    Optional<Limits> findByNumberOfCard(String id);
}
