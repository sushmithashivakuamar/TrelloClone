package syr.edu.TrelloClone.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import syr.edu.TrelloClone.model.Modify;

import java.util.Optional;

public interface ModifyRepository extends JpaRepository<Modify, String> {
    Optional<Modify> findById(String id);
}