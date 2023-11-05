package syr.edu.TrelloClone.Exception;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGlobalException(Exception ex) {
        // Customize the response message and status code based on your requirements
        String errorMessage = "An error occurred: " + ex.getMessage();
        return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(org.springframework.web.server.ResponseStatusException.class)
    public ResponseEntity<String> handleResponseStatusException(org.springframework.web.server.ResponseStatusException ex) {
        String errorMessage = "Resource not found: " + ex.getReason();
        return new ResponseEntity<>(errorMessage, ex.getStatus());
    }
}
