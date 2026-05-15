package nhan.demo.controller;

import nhan.demo.dto.request.UserRequestDTO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/user")
public class UserController {
//    @PostMapping(value = "/", headers = "apiKey=v1.0")
    @RequestMapping(method = POST,path ="/", headers = "apiKey=v1.0")
    public String addUser(@RequestBody UserRequestDTO userDTO){
        return "User added";
    }

    @PutMapping("/{userId}")
    public String updateUser(@PathVariable("userId") int id, @RequestBody UserRequestDTO userDTO){
        System.out.println("Request update userId = "+ id);
        return "User updated";
    }

    @PatchMapping("/{userId}")
    public String changeStatus(@PathVariable int userId, @RequestParam(required = false) boolean status){
        System.out.println("Request change status userId = "+ userId);
        return "User changed status";
    }

    @DeleteMapping("/{userId}")
    public String deleteUser(@PathVariable int userId){
        System.out.println("Request delete userId = "+ userId);
        return "User deleted";
    }

    @GetMapping("/{userId}")
    public UserRequestDTO getUser(@PathVariable int userId){
        System.out.println("Request get user id = " + userId);
        return new UserRequestDTO("Truong","Nhan","phone","email");
    }

    @GetMapping("/list")
    public List<UserRequestDTO> getUserList(
            @RequestParam(required = false) String email,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        System.out.println("Request get user list");
        return List.of(new UserRequestDTO("Truong","Nhan","phone","email"),
                new UserRequestDTO("Truong1", "Nhan1","phone1","email1"));
    }
}
