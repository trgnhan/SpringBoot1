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
import nhan.demo.dto.response.UserDetailResponse;
import nhan.demo.exception.ResourceNotFoundException;
import nhan.demo.service.UserService;
import nhan.demo.util.UserStatus;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    public ResponseData<?> updateUser(@Min(1) @PathVariable("userId") long id, @Valid @RequestBody UserRequestDTO userDTO){
        log.info("Request update userId = {} ", id);
        try{
            userService.updateUser(id, userDTO);
            return new ResponseData<>(HttpStatus.ACCEPTED.value(), Translator.toLocale("user.update.success"));
        }catch(Exception e){
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(),"Update user failed");
        }

    }

    @Operation(summary = "Change user", description = "API changed user")
    @PatchMapping("/{userId}")
    public ResponseData<?> changeStatus(@PathVariable("userId") @Min(1) int userId,@RequestParam UserStatus status){
        log.info("Request change status userId = {}", userId);
        try{
            userService.changeStatus(userId,status);
            return new ResponseData<>(HttpStatus.ACCEPTED.value(),"User changed status");
        }catch(Exception e){
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(),"Change user failed");
        }
    }

    @Operation(summary = "Delete user", description = "API delete user")
    @DeleteMapping("/{userId}")
    public ResponseData<?> deleteUser(@Min(1) @PathVariable("userId") int userId){
        log.info("Request delete userId = {}", userId);
        try{
            userService.deleteUser(userId);
            return new ResponseData<>(HttpStatus.NO_CONTENT.value(),"User deleted");

        }catch(Exception e){
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(),"Delete user failed");
        }
    }

    @Operation(summary = "Get user", description = "API get user")
    @GetMapping("/{userId}")
    public ResponseData<UserDetailResponse> getUser(@PathVariable("userId") int userId){
        log.info("Request get user id = {}" , userId);
        try{
            return new ResponseData<>(HttpStatus.OK.value(),"user",userService.getUser(userId));
        }catch(ResourceNotFoundException e){
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(),e.getMessage());
        }
    }

    @Operation(summary = "Get list user per page", description = "Return user by pageNo and pageSize")
    @GetMapping("/list")
    public ResponseData<?> getUserList(
             @RequestParam(value = "pageNo",defaultValue = "0", required = false) int pageNo,
             @Min(10) @RequestParam(value = "pageSize", defaultValue = "20", required = false) int pageSize,
             @RequestParam(required = false) String sortBy){
        log.info("Request get all user list");
        return new ResponseData<>(HttpStatus.OK.value(),"list user",userService.getAllUsersWithSortBy(pageNo, pageSize,sortBy));
    }

    @Operation(summary = "Get list user per page with sort by multiple columns", description = "Return user by pageNo, pageSize and sort by multiple columns ")
    @GetMapping("/list-with-sort-by-multiple-columns")
    public ResponseData<?> getAllUsersWithSortByMutipleColumns(
            @RequestParam(value = "pageNo",defaultValue = "0", required = false) int pageNo,
            @Min(10) @RequestParam(value = "pageSize", defaultValue = "20", required = false) int pageSize,
            @RequestParam(required = false) String... sorts){
        log.info("Request get all user list with sort by multiple columns");
        return new ResponseData<>(HttpStatus.OK.value(),"list user",userService.getAllUsersWithSortByMultipleColumns(pageNo, pageSize,sorts));
    }

    @Operation(summary = "Get list user per page with sort by columns and search", description = "Return user by pageNo, pageSize and sort by multiple columns and search")
    @GetMapping("/list-with-sort-by-columns-and-search")
    public ResponseData<?> getAllUsersWithSearch(
            @RequestParam(value = "pageNo",defaultValue = "0", required = false) int pageNo,
            @Min(10) @RequestParam(value = "pageSize", defaultValue = "20", required = false) int pageSize,
            @RequestParam(defaultValue = "20", required = false) String search,
            @RequestParam(required = false) String sorts){
        log.info("Request get all user list with sort by columns and search");
        return new ResponseData<>(HttpStatus.OK.value(),"list user",userService.getAllUsersWithSortByColumnsAndSearch(pageNo, pageSize,search,sorts));
    }
}
