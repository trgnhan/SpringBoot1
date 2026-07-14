package nhan.demo.service;

import jakarta.validation.constraints.Min;
import nhan.demo.dto.request.UserRequestDTO;
import nhan.demo.dto.response.PageResponse;
import nhan.demo.dto.response.UserDetailResponse;
import nhan.demo.util.UserStatus;

public interface UserService {

    long saveUser(UserRequestDTO request);

    void updateUser(long userId, UserRequestDTO request);

    void changeStatus(long userId, UserStatus status);

    void deleteUser(long userId);

    UserDetailResponse getUser(long userId);

    PageResponse<?> getAllUsersWithSortBy(int pageNo, int pageSize, String sortBy);

    PageResponse<?> getAllUsersWithSortByMultipleColumns(int pageNo, int pageSize, String... sorts);

    PageResponse<?> getAllUsersWithSortByColumnsAndSearch(int pageNo, int pageSize, String search, String sorts);

    PageResponse<?> advanceSearchByCriteria(int pageNo, @Min(10) int pageSize, String sortBy, String address,String... search);
}
