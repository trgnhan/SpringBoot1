package nhan.demo.dto.response;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import nhan.demo.util.PhoneNumber;

import java.io.Serializable;

@Getter
@Builder
public class UserDetailReponse implements Serializable {

    private String firstName;
    private String lastName;
    private String email;
    private String phone;
}
