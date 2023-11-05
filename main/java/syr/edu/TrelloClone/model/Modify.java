package syr.edu.TrelloClone.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Modify {
    @Id
    //private String id;

    @GeneratedValue(strategy = GenerationType.AUTO) // Use AUTO to generate UUIDs
    @Type(type = "uuid-char")
    private UUID id; // Change the data type to UUID


    @Transient
    private String taskId;

    @ManyToOne
    @JoinColumn(name="task_id")
    private Task task;
}
