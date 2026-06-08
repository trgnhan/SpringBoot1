package nhan.demo.service.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nhan.demo.dto.request.AddressDTO;
import nhan.demo.dto.request.UserRequestDTO;
import nhan.demo.dto.response.UserDetailReponse;
import nhan.demo.exception.ResourceNotFoundException;
import nhan.demo.model.Address;
import nhan.demo.model.User;
import nhan.demo.repository.UserRepository;
import nhan.demo.service.UserService;
import nhan.demo.util.UserType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;


    @Override
    public long saveUser(UserRequestDTO request) {
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .phone(request.getPhone())
                .email(request.getEmail())
                .username(request.getUsername())
                .password(request.getPassword())
                .status(request.getStatus())
                .type(UserType.valueOf(request.getType().toUpperCase()))
                .build();
        request.getAddresses().forEach(a ->
                user.saveAddress(Address.builder()
                        .apartmentNumber(a.getApartmentNumber())
                        .floor(a.getFloor())
                        .building(a.getBuilding())
                        .streetNumber(a.getStreetNumber())
                        .street(a.getStreet())
                        .city(a.getCity())
                        .country(a.getCountry())
                        .addressType(a.getAddressType())
                        .build()));
        userRepository.save(user);

        log.info("User has added successfully, userId={}", user.getId());

        return user.getId();
    }

//    private Set<Address> convertToAddress(Set<AddressDTO> addresses) {
//        Set<Address> result = new HashSet<>();
//        addresses.forEach(a ->
//                result.add(Address.builder()
//                        .apartmentNumber(a.getApartmentNumber())
//                        .floor(a.getFloor())
//                        .building(a.getBuilding())
//                        .streetNumber(a.getStreetNumber())
//                        .street(a.getStreet())
//                        .city(a.getCity())
//                        .country(a.getCountry())
//                        .addressType(a.getAddressType())
//                        .build())
//        );
//        return result;
//    }

    @Override
    public void updateUser(long userId, UserRequestDTO request) {
        User user = getUserById(userId);
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setGender(request.getGender());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());

        if(StringUtils.hasLength(request.getUsername())) {
            user.setUsername(request.getUsername());
        }
        if(StringUtils.hasLength(request.getPhone())) {
            //check
            //check trung sdt da co trong ht chua
            // ....

        }
        user.setPassword(request.getPassword());
        user.setStatus(request.getStatus());
        user.setType(UserType.valueOf(request.getType().toUpperCase()));
        user.setAddresses(user.getAddresses());
        userRepository.save(user);
        log.info("User has updated successfully, userId={}", userId);
    }

    @Override
    public void changeStatus(long userId, UserRequestDTO request) {
        User user = getUserById(userId);
        user.setStatus(request.getStatus());
        userRepository.save(user);
        log.info("User has changed successfully, userId={}", user.getId());
    }

    @Override
    public void deleteUser(long userId) {
        userRepository.deleteById(userId);
        log.info("User has deleted successfully, userId={}", userId);
    }

    @Override
    public UserDetailReponse getUser(long userId) {
        User user = getUserById(userId);
        return UserDetailReponse.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .email(user.getEmail())
                .build();

    }

    @Override
    public List<UserDetailReponse> getAllUsers(int pageNo, int pageSize) {
        return List.of();
    }

    private User getUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(()-> new ResourceNotFoundException("User not found"));
    }
}
