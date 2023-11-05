package syr.edu.TrelloClone.Repository;
import syr.edu.TrelloClone.model.Task;

public interface TaskObserver {
    void update(Task task);
}