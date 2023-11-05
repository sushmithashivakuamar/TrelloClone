package syr.edu.TrelloClone.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import syr.edu.TrelloClone.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

        @Query("SELECT u FROM User u WHERE u.user_id = ?1")
        User findByUser_id(Long user_id);

        User findByFirstName(String firstName);

       // @Query("SELECT u FROM User u WHERE u.firstName = ?1")
      //  User findByFirstName(String firstName);

}
