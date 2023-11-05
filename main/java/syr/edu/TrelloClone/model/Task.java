package syr.edu.TrelloClone.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "Task")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long task_id; // Changed from String to Long
    private String state;
    private String assignedTo;


    @ManyToMany
    @JoinTable(
            name = "task_user_assignment",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )

    private List<User> assignedUsers = new ArrayList<>(); // Use a List to represent multiple users assigned to a task

    private String description;
    @ElementCollection
    private List<String> comments;
    private LocalDateTime createdTime;
    private LocalDateTime startedTime; // Track the start time when the task transitions to "Doing" state

    private LocalDateTime doneTime; // Track the time when the task is marked as "Completed"

    private Long durationInMinutes; // Calculate and store the duration in minutes
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
    private List<Modify> modifications;



}
