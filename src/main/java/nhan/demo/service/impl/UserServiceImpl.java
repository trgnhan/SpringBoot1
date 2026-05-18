package nhan.demo.service.impl;

import nhan.demo.dto.request.UserRequestDTO;
import nhan.demo.exception.ResourceNotFoundException;
import nhan.demo.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Override
    public int addUser(UserRequestDTO requestDTO) {
        System.out.println("save user to db");
        if(!requestDTO.getFirstName().equals("string")){
            throw new ResourceNotFoundException("string k ton tai");
        }
        return 0;
    }
}
