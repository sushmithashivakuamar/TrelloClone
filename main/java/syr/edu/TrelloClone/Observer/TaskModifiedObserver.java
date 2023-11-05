package syr.edu.TrelloClone.Observer;

import org.springframework.stereotype.Component;
import syr.edu.TrelloClone.Repository.TaskObserver;
import syr.edu.TrelloClone.model.Task;

@Component
public class TaskModifiedObserver implements TaskObserver {
    @Override
    public void update(Task task) {
        // Define the behavior when a task is modified
        System.out.println("Task Modified: " + task.getTask_id());
    }
}