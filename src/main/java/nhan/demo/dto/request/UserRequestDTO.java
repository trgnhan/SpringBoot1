package nhan.demo.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import nhan.demo.util.PhoneNumber;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class UserRequestDTO implements Serializable {

    @NotBlank(message = "firstName must be not blank")
    private String firstName;

    @NotNull(message = "lastName must be not null")
    private String lastName;

    @Email(message = "email invalid format")
    private String email;

//    @Pattern(regexp = "^\\d{10}$", message = "phone invalid format")
    @PhoneNumber
    private String phone;

    @NotNull(message = "dateOfBirth must be not null")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "MM/dd/yyyy")
    private Date dateOfBirth;

    @NotEmpty
    List<String> permissions;

    public UserRequestDTO(String firstName, String lastName, String email, String phone, Date dateOfBirth, List<String> permissions) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.dateOfBirth = dateOfBirth;
        this.permissions = permissions;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public List<String> getPermissions() {
        return permissions;
    }
}
