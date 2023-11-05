
package syr.edu.TrelloClone.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import syr.edu.TrelloClone.model.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {
    // other custom methods if any
}