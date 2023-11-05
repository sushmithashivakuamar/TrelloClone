package syr.edu.TrelloClone.Observer;

import org.springframework.stereotype.Component;
import syr.edu.TrelloClone.Repository.TaskObserver;
import syr.edu.TrelloClone.model.Task;

@Component
public class TaskCreatedObserver implements TaskObserver {
    @Override
    public void update(Task task) {
        // Defining the behavior when a task is created
        System.out.println("Task Created: " + task.getTask_id());
    }
}