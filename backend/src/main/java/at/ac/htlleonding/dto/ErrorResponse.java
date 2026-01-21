package at.ac.htlleonding.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponse(
    int status,
    String message,
    LocalDateTime timestamp,
    String path,
    List<String> details
) {
    public static ErrorResponse of(int status, String message, String path) {
        return new ErrorResponse(status, message, LocalDateTime.now(), path, List.of());
    }

    public static ErrorResponse of(int status, String message, String path, List<String> details) {
        return new ErrorResponse(status, message, LocalDateTime.now(), path, details);
    }
}
