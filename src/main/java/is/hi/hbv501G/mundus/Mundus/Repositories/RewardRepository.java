package is.hi.hbv501G.mundus.Mundus.Repositories;

import is.hi.hbv501G.mundus.Mundus.Entities.Reward;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RewardRepository extends JpaRepository<Reward, Long> {

    Reward save(Reward quest);
    void delete(Reward quest);
    List<Reward> findAll();
    Reward findById(long id);
}
