package com.u3.shoppingcart.service.user;

import com.u3.shoppingcart.dto.UserDto;
import com.u3.shoppingcart.model.User;
import com.u3.shoppingcart.request.CreateUserRequest;
import com.u3.shoppingcart.request.UserUpdateRequest;

public interface IUserService {
    User getUserById(Long userId);
    User createUser(CreateUserRequest request);
    User updateUser(UserUpdateRequest request, Long userId);
    void deleteUser(Long userId);

    UserDto convertUsertoDto(User user);

    User getAuthenticatedUser();
}
