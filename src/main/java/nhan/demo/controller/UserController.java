package nhan.demo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import nhan.demo.dto.request.UserRequestDTO;
import nhan.demo.dto.response.ResponseData;
import nhan.demo.dto.response.ResponseError;
import nhan.demo.dto.response.ResponseSuccess;
import nhan.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/user")
@Validated
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping(value = "/")
//    @RequestMapping(method = POST,path ="/", headers = "apiKey=v1.0")
    public ResponseData<Integer> addUser(@Valid @RequestBody UserRequestDTO userDTO){
        try{
            userService.addUser(userDTO);
            return new ResponseData<>(HttpStatus.CREATED.value(), "User added successfully",1);
        }catch (Exception e){
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }

    }


    @PutMapping("/{userId}")
    public ResponseData<?> updateUser(@Min(1) @PathVariable("userId") int id, @Valid @RequestBody UserRequestDTO userDTO){
        System.out.println("Request update userId = "+ id);
        return new ResponseData<>(HttpStatus.ACCEPTED.value(), "User updated successfully");
    }

    @PatchMapping("/{userId}")
    public ResponseData<?> changeStatus(@PathVariable("userId") @Min(1) int userId,@RequestParam(value = "status",required = false) boolean status){
        System.out.println("Request change status userId = "+ userId);
        return new ResponseData<>(HttpStatus.ACCEPTED.value(),"User changed status");
    }

    @DeleteMapping("/{userId}")
    public ResponseData<?> deleteUser(@Min(1) @PathVariable("userId") int userId){
        System.out.println("Request delete userId = "+ userId);
        return new ResponseData<>(HttpStatus.NO_CONTENT.value(),"User deleted");
    }

    @GetMapping("/{userId}")
    public ResponseData<UserRequestDTO> getUser(@PathVariable("userId") int userId){
        System.out.println("Request get user id = " + userId);
        return new ResponseData<>(HttpStatus.OK.value(),"user",new UserRequestDTO("Truong","Nhan","phone@gmail","0123465789"));
    }

    @GetMapping("/list")
    public ResponseData<List<UserRequestDTO>> getUserList(
             @RequestParam(value = "pageNo",defaultValue = "0", required = false) int pageNo,
            @Min(10) @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize){
        System.out.println("Request get user list");
        return new ResponseData<>(HttpStatus.OK.value(),"list user",List.of(new UserRequestDTO("Truong","Nhan","nahn@gmail","0123456789"),
                new UserRequestDTO("Truong1", "Nhan1","nhan@gmail","1234567890")));
    }
}
