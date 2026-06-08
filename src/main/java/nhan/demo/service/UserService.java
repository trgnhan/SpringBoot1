package nhan.demo.service;

import nhan.demo.dto.request.UserRequestDTO;
import nhan.demo.dto.response.UserDetailReponse;

import java.util.List;

public interface UserService {

    long saveUser(UserRequestDTO request);

    void updateUser(long userId, UserRequestDTO request);

    void changeStatus(long userId, UserRequestDTO request);

    void deleteUser(long userId);

    UserDetailReponse getUser(long userId);

    List<UserDetailReponse> getAllUsers(int pageNo, int pageSize);
}
