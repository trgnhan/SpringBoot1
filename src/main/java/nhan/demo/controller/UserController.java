package nhan.demo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nhan.demo.configuration.Translator;
import nhan.demo.dto.request.UserRequestDTO;
import nhan.demo.dto.response.ResponseData;
import nhan.demo.dto.response.ResponseError;
import nhan.demo.service.UserService;
import nhan.demo.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@Validated
@Slf4j
@Tag(name = "User Controller")
@RequiredArgsConstructor()
public class UserController {

    private final UserService userService;


    @Operation(summary = "Add user", description = "API create new user")
    @PostMapping(value = "/")
//    @RequestMapping(method = POST,path ="/", headers = "apiKey=v1.0")
    public ResponseData<Long> addUser(@Valid @RequestBody UserRequestDTO userDTO){
        log.info("Request add user = {} {}" , userDTO.getFirstName(), userDTO.getLastName());
        try{
            long userId = userService.saveUser(userDTO);
            return new ResponseData<>(HttpStatus.CREATED.value(), Translator.toLocale("user.add.success"),userId);
        }catch (Exception e){
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Add user failed");
        }
//        return new ResponseData<>(HttpStatus.CREATED.value(), Translator.toLocale("user.add.success"),1);
    }

    @Operation(summary = "Update user", description = "API update user")
    @PutMapping("/{userId}")
    public ResponseData<?> updateUser(@Min(1) @PathVariable("userId") int id, @Valid @RequestBody UserRequestDTO userDTO){
        log.info("Request update userId = {} ", id);
        return new ResponseData<>(HttpStatus.ACCEPTED.value(), Translator.toLocale("user.update.success"));
    }

    @Operation(summary = "Change user", description = "API changed user")
    @PatchMapping("/{userId}")
    public ResponseData<?> changeStatus(@PathVariable("userId") @Min(1) int userId,@RequestParam(value = "status",required = false) boolean status){
        log.info("Request change status userId = {}", userId);
        return new ResponseData<>(HttpStatus.ACCEPTED.value(),"User changed status");
    }

    @Operation(summary = "Delete user", description = "API delete user")
    @DeleteMapping("/{userId}")
    public ResponseData<?> deleteUser(@Min(1) @PathVariable("userId") int userId){
        log.info("Request delete userId = {}", userId);
        return new ResponseData<>(HttpStatus.NO_CONTENT.value(),"User deleted");
    }

    @Operation(summary = "Get user", description = "API get user")
    @GetMapping("/{userId}")
    public ResponseData<UserRequestDTO> getUser(@PathVariable("userId") int userId){
        log.info("Request get user id = {}" , userId);
        return new ResponseData<>(HttpStatus.OK.value(),"user",new UserRequestDTO("Truong","Nhan","phone@gmail","0123465789"));
    }

    @Operation(summary = "Get list user per page", description = "Return user by pageNo and pageSize")
    @GetMapping("/list")
    public ResponseData<List<UserRequestDTO>> getUserList(
             @RequestParam(value = "pageNo",defaultValue = "0", required = false) int pageNo,
            @Min(10) @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize){
        log.info("Request get user list");
        return new ResponseData<>(HttpStatus.OK.value(),"list user",List.of(new UserRequestDTO("Truong","Nhan","nahn@gmail","0123456789"),
                new UserRequestDTO("Truong1", "Nhan1","nhan@gmail","1234567890")));
    }
}
