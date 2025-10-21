ackage com.hotel.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

// ============= AUTHENTICATION DTOs =============
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @NotBlank(message = "Username is required")
    private String username;
    
    @NotBlank(message = "Password is required")
    private String password;
}