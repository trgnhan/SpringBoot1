package nhan.demo.service.impl;


import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nhan.demo.dto.request.UserRequestDTO;
import nhan.demo.dto.response.PageResponse;
import nhan.demo.dto.response.UserDetailResponse;
import nhan.demo.exception.ResourceNotFoundException;
import nhan.demo.model.Address;
import nhan.demo.model.User;
import nhan.demo.repository.SearchRepository;
import nhan.demo.repository.UserRepository;
import nhan.demo.repository.specification.SpecSearchCriteria;
import nhan.demo.repository.specification.UserSpec;
import nhan.demo.repository.specification.UserSpecificationBuilder;
import nhan.demo.service.UserService;
import nhan.demo.util.Gender;
import nhan.demo.util.UserStatus;
import nhan.demo.util.UserType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static nhan.demo.repository.specification.SearchOperation.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final SearchRepository searchRepository;

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

        if (StringUtils.hasLength(request.getUsername())) {
            user.setUsername(request.getUsername());
        }
        if (StringUtils.hasLength(request.getPhone())) {
            //check
            //check trung sdt da co trong ht chua
            // ....

        }
        user.setPassword(request.getPassword());
        user.setStatus(request.getStatus());
        user.setType(UserType.valueOf(request.getType().toUpperCase()));
        user.setAddress(user.getAddress());
        userRepository.save(user);
        log.info("User has updated successfully, userId={}", userId);
    }

    @Override
    public void changeStatus(long userId, UserStatus status) {
        User user = getUserById(userId);
        user.setStatus(status);
        userRepository.save(user);
        log.info("User has changed successfully, userId={}", user.getId());
    }

    @Override
    public void deleteUser(long userId) {
        userRepository.deleteById(userId);
        log.info("User has deleted successfully, userId={}", userId);
    }

    @Override
    public UserDetailResponse getUser(long userId) {
        User user = getUserById(userId);
        return UserDetailResponse.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .email(user.getEmail())
                .build();

    }

    @Override
    public PageResponse<?> getAllUsersWithSortBy(int pageNo, int pageSize, String sortBy) {
        int page=pageNo;
        if(pageNo>0){
            pageNo=pageNo-1;
        }

        // lam trong may cai filter sap xep a->z or z->a ....
        List<Sort.Order> sorts = new ArrayList<>();


        // neu co gia tri
        if(StringUtils.hasLength(sortBy)){
            // firstName: asc|desc
            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
            Matcher matcher = pattern.matcher(sortBy);
            if(matcher.find()){
                if(matcher.group(3).equalsIgnoreCase("asc")){
                    sorts.add(new Sort.Order(Sort.Direction.ASC, matcher.group(1)));
                }else if (matcher.group(3).equalsIgnoreCase("desc")){
                    sorts.add(new Sort.Order(Sort.Direction.DESC, matcher.group(1)));
                }
            }

        }


        Pageable pageable = PageRequest.of(pageNo,pageSize, Sort.by(sorts));

        Page<User> users = userRepository.findAll(pageable);
        List<UserDetailResponse> response = users.stream().map(user -> UserDetailResponse.builder()
                .id((long) user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .build()).toList();
        return PageResponse.builder()
                .pageNo(page)
                .pageSize(pageSize)
                .totalPages(users.getTotalPages())
                .items(response)
                .build();
    }


    @Override
    public PageResponse<?> getAllUsersWithSortByMultipleColumns(int pageNo, int pageSize, String... sorts) {
        int page = pageNo;
        if(pageNo>0){
            pageNo=pageNo-1;
        }

        // lam trong may cai filter sap xep a->z or z->a ....
        List<Sort.Order> orders = new ArrayList<>();
        // vi no la chuoi nen phai tach ra tiep
        // neu co gia tri
        for (String s : sorts) {
            // firstName: asc|desc
            Pattern pattern = Pattern.compile("(\\w+?)(:)(.*)");
            Matcher matcher = pattern.matcher(s);
            if(matcher.find()){
                if(matcher.group(3).equalsIgnoreCase("asc")){
                    orders.add(new Sort.Order(Sort.Direction.ASC, matcher.group(1)));
                }else if (matcher.group(3).equalsIgnoreCase("desc")){
                    orders.add(new Sort.Order(Sort.Direction.DESC, matcher.group(1)));
                }
            }
        }


        Pageable pageable = PageRequest.of(pageNo,pageSize, Sort.by(orders));

        Page<User> users = userRepository.findAll(pageable);
        List<UserDetailResponse> response = users.stream().map(user -> UserDetailResponse.builder()
                .id((long) user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .build()).toList();
        return PageResponse.builder()
                .pageNo(page)
                .pageSize(pageSize)
                .totalPages(users.getTotalPages())
                .items(response)
                .build();


    }

    @Override
    public PageResponse<?> getAllUsersWithSortByColumnsAndSearch(int pageNo, int pageSize, String search, String sorts) {


        return searchRepository.getAllUsersWithSortByColumnsAndSearch(pageNo, pageSize, search, sorts);
    }

    @Override
    public PageResponse<?> advanceSearchByCriteria(int pageNo, int pageSize, String sortBy, String address,String... search) {

        return searchRepository.advanceSearchByCriteria(pageNo, pageSize,sortBy , address,search);
    }

    @Override
    public PageResponse<?> advanceSearchBySpecification(Pageable pageable, String[] user, String[] address) {

        Page<User> users =null;
        List<User> list = new ArrayList<>();
        if (user!=null && address!=null){
            //tim kiem tren ca bang user va address -> join table
            return searchRepository.getUsersJoinedAddress(pageable,user,address);



        }else if(user!=null && address==null){
            //tim kiem tren bang user


//            Specification<User> spec = UserSpec.hasFirstName("t");
//            Specification<User> genderSpec = UserSpec.notEqualGender(Gender.FEMALE);
//            Specification<User> finalSpec = spec.and(genderSpec);

            UserSpecificationBuilder builder = new UserSpecificationBuilder();


            for (String s : user){
                Pattern pattern = Pattern.compile("(\\w+?)([:<>~!])(.*)(\\p{Punct}?)(.*)(\\p{Punct}?)");
                Matcher matcher = pattern.matcher(s);
                if(matcher.find()){
                    builder.with(matcher.group(1),matcher.group(2), matcher.group(3), matcher.group(4), matcher.group(5));
                }
            }
            list = userRepository.findAll(builder.build());
            users =  userRepository.findAll(pageable);
            return PageResponse.builder()
                    .pageNo(pageable.getPageNumber())
                    .pageSize(pageable.getPageSize())
                    .totalPages(users.getTotalPages())
                    .items(list)
                    .build();
        }

            users =  userRepository.findAll(pageable);


        return PageResponse.builder()
                .pageNo(pageable.getPageNumber())
                .pageSize(pageable.getPageSize())
                .totalPages(users.getTotalPages())
                .items(users.stream().toList())
                .build();
    }

    private User getUserById(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
