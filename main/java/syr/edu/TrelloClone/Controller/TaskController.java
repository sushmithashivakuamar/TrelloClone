package syr.edu.TrelloClone.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import syr.edu.TrelloClone.Repository.ModifyRepository;
import syr.edu.TrelloClone.Repository.TaskObserver;
import syr.edu.TrelloClone.Repository.TaskRepository;
import syr.edu.TrelloClone.Repository.UserRepository;
import syr.edu.TrelloClone.model.Modify;
import syr.edu.TrelloClone.model.Task;
import syr.edu.TrelloClone.model.User;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskRepository repository;
    @Autowired
    private ModifyRepository modifyRepository;
    @Autowired
    private UserRepository userRepository; // Assuming you have a UserRepository
    private final List<TaskObserver> observers = new ArrayList<>();

    @Autowired
    public TaskController(TaskObserver taskCreatedObserver, TaskObserver taskModifiedObserver) {
        // Adding the observers to the controller
        addObserver(taskCreatedObserver);
        addObserver(taskModifiedObserver);
    }

    // Method to add an observer
    public void addObserver(TaskObserver observer) {
        observers.add(observer);
    }

    // Method to notify observers when a task is created or modified
    private void notifyObservers(Task task) {
        for (TaskObserver observer : observers) {
            observer.update(task);
        }
    }

@PostMapping("/createTask")
  public ResponseEntity<String> createTask(@RequestBody Task task) {
      System.out.println("User Received task: " + task);

      // Extract the assignedTo field from the task
      String assignedToName = task.getAssignedTo();

      // Check if there is a user in the database with a matching first name
      User assignedUser = userRepository.findByFirstName(assignedToName);

      if (assignedUser != null) {
          // Check if the assigned user has reached their task limit
          if (assignedUser.getTaskLimit() <= 0) {
              return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User has reached their task limit.");
          }

          // Set the created time
          task.setCreatedTime(LocalDateTime.now());

          // Add the user to the list of assigned users
          task.getAssignedUsers().add(assignedUser);

          try {
              repository.save(task);
              // Decrease the task limit of the assigned user by 1
              assignedUser.setTaskLimit(assignedUser.getTaskLimit() - 1);
              userRepository.save(assignedUser);

              // notifyObservers(task); // Notify observers when a task is created
              return ResponseEntity.status(HttpStatus.CREATED).body(task.getTask_id().toString());
          } catch (Exception e) {
              e.printStackTrace();
              return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failure");
          }
      } else {
          return ResponseEntity.status(HttpStatus.NOT_FOUND)
                  .body("User not found in the project. Task creation and assignment for new user is not allowed.");
      }
  }

    @PutMapping("/modifyTask/{id}")
    public ResponseEntity<String> modifyTask(@PathVariable String id, @RequestBody Task task) {
        Long taskId;
        try {
            taskId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid task ID");
        }

        Optional<Task> optionalTask = repository.findById(taskId);
        if (!optionalTask.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found");
        }

        Task existingTask = optionalTask.get();

        // Check if the assigned user exists in the user database
        String assignedTo = task.getAssignedTo();
        User assignedUser = userRepository.findByFirstName(assignedTo);

        if (assignedUser == null) {
            // User doesn't exist, retrieve the actual assigned user from the existing task
            List<User> assignedUsers = existingTask.getAssignedUsers();
            if (assignedUsers != null && !assignedUsers.isEmpty()) {
                User actualAssignedUser = assignedUsers.get(0); // Assuming only one user can be assigned
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Task cannot be assigned to a different user who is not part of the project while modifying. Assigned to: " + actualAssignedUser.getFirstName());
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Task cannot be assigned to a different user who is not part of the project while modifying.");
            }
        }

        // Check if the user is already in the specified state
        if (task.getState() != null && existingTask.getState() != null && existingTask.getState().equalsIgnoreCase(task.getState()) && existingTask.getAssignedTo().equalsIgnoreCase(assignedTo)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User is already in the specified state: " + task.getState());
        }

        // Check if the user's task limit is reached
        if (assignedUser.getTaskLimit() <= 0 && !assignedTo.equalsIgnoreCase(existingTask.getAssignedTo())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User's task limit is reached. Cannot modify task.");
        }

        // Update the assignedTo field with the new user
        existingTask.setAssignedTo(assignedTo);

        // Update the task's state and timestamps when transitioning to "Doing" or "Completed"
        if (task.getState() != null) {
            if (task.getState().equalsIgnoreCase("Doing") && !existingTask.getState().equalsIgnoreCase("Doing")) {
                // Transitioning to "Doing" state
                existingTask.setState("Doing");
                existingTask.setStartedTime(LocalDateTime.now()); // Set the start time
            } else if (task.getState().equalsIgnoreCase("Completed") && !existingTask.getState().equalsIgnoreCase("Completed")) {
                // Transitioning to "Completed" state
                existingTask.setState("Completed");
                existingTask.setDoneTime(LocalDateTime.now()); // Set the completion time
                // Calculate and set the duration in minutes
                long durationInMinutes = Duration.between(existingTask.getStartedTime(), existingTask.getDoneTime()).toMinutes();
                existingTask.setDurationInMinutes(durationInMinutes);
            } else {
                // Other state transitions
                existingTask.setState(task.getState());
            }
        }

        // Update other task properties if they are not null
        if (task.getDescription() != null) {
            existingTask.setDescription(task.getDescription());
        }
        if (task.getComments() != null) {
            List<String> commentsList = existingTask.getComments();
            if (commentsList == null) {
                commentsList = new ArrayList<>();
            }
            commentsList.addAll(task.getComments());
            existingTask.setComments(commentsList);
        }

        try {
            // Save the updated task
            Task updatedTask = repository.save(existingTask);

            // Update the task's assigned users
            updatedTask.getAssignedUsers().clear(); // Remove existing assignments
            User newAssignedUser = userRepository.findByFirstName(assignedTo); // Get the new assigned user
            updatedTask.getAssignedUsers().add(newAssignedUser); // Add the new user to the assignment list
            repository.save(updatedTask); // Save the updated task with the new assignment

            // Decrement the user's task limit if it's a different user
            if (!assignedTo.equalsIgnoreCase(existingTask.getAssignedTo())) {
                assignedUser.setTaskLimit(assignedUser.getTaskLimit() - 1);
                userRepository.save(assignedUser);
            }

            modifiedList(id, updatedTask);
            notifyObservers(updatedTask); // Notify observers when a task is modified

            return ResponseEntity.status(HttpStatus.OK).body("Task modified successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failure");
        }
    }

    public String modifiedList(String taskId, Task task) {
        Modify modify = new Modify();
        UUID id = UUID.randomUUID(); // Generate a random UUID
        modify.setId(id);
        modify.setTaskId(taskId);
        modify.setTask(task);
        try {
            modifyRepository.save(modify);
            return modify.getId().toString(); // Convert UUID to string for return
        } catch (Exception e) {
            return "Failure";
        }
    }
    @DeleteMapping("/deleteTask/{id}")
    public ResponseEntity<String> deleteTask(@PathVariable String id) {
        Long taskId;
        try {
            taskId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid task ID");
        }

        Optional<Task> optionalTask = repository.findById(taskId);
        if (!optionalTask.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found");
        }

        Task taskToDelete = optionalTask.get();

        try {
            // Get the assigned users of the task
            List<User> assignedUsers = taskToDelete.getAssignedUsers();

            // Increase the task limit of the assigned users by 1 for each user
            if (assignedUsers != null && !assignedUsers.isEmpty()) {
                for (User user : assignedUsers) {
                    user.setTaskLimit(user.getTaskLimit() + 1);
                    userRepository.save(user);
                }
            }

            repository.delete(taskToDelete);
            return ResponseEntity.status(HttpStatus.OK).body("Task deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failure");
        }
    }

    @GetMapping("/showBoard")
    public List<Task> getTasks() {
        return repository.findAll();
    }


}
