package syr.edu.TrelloClone.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true,name = "user_id")
    private long user_id;


    @Column(unique = true)
    private String email; // Ensure email addresses are unique

    private String phoneNumber;

    private String firstName;

    private String lastName;

    private int taskLimit;

    @Enumerated(EnumType.STRING)
    private ExperienceLevel experienceLevel;

    public enum ExperienceLevel {
        JUNIOR, // Example experience levels
        MID_LEVEL,
        SENIOR
    }

}
