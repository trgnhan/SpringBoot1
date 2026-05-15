package nhan.demo.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import nhan.demo.dto.request.UserRequestDTO;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/user")
public class UserController {
    @PostMapping(value = "/")
//    @RequestMapping(method = POST,path ="/", headers = "apiKey=v1.0")
    public String addUser(@Valid @RequestBody UserRequestDTO userDTO){
        return "User added";
    }

    @PutMapping("/{userId}")
    public String updateUser(@PathVariable("userId") int id, @Valid @RequestBody UserRequestDTO userDTO){
        System.out.println("Request update userId = "+ id);
        return "User updated";
    }

    @PatchMapping("/{userId}")
    public String changeStatus(@PathVariable @Min(1) int userId, @Min(1) @RequestParam(required = false) int status){
        System.out.println("Request change status userId = "+ userId);
        return "User changed status";
    }

    @DeleteMapping("/{userId}")
    public String deleteUser(@Min(1) @PathVariable int userId){
        System.out.println("Request delete userId = "+ userId);
        return "User deleted";
    }

    @GetMapping("/{userId}")
    public UserRequestDTO getUser(@PathVariable int userId){
        System.out.println("Request get user id = " + userId);
        return new UserRequestDTO("Truong","Nhan","phone@gmail","0123465789",new Date(2024,03,3),List.of("a"));
    }

    @GetMapping("/list")
    public List<UserRequestDTO> getUserList(
            @RequestParam(required = false) String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        System.out.println("Request get user list");
        return List.of(new UserRequestDTO("Truong","Nhan","nahn@gmail","0123456789",new Date(2000,05,15),List.of("a")),
                new UserRequestDTO("Truong1", "Nhan1","nhan@gmail","1234567890",new Date(2002,03,3),List.of("a")));
    }
}
