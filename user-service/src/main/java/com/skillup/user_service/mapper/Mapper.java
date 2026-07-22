package com.skillup.user_service.mapper;

import com.skillup.user_service.model.User;
import com.skillup.user_service.model.dto.UserResponse;
import org.springframework.stereotype.Component;

@Component
public class Mapper {

    public UserResponse toResponse(User user){
        return new UserResponse(user.getName());
    }


}
