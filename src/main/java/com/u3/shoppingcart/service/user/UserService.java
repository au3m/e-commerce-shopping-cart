package com.u3.shoppingcart.service.user;

import com.u3.shoppingcart.dto.UserDto;
import com.u3.shoppingcart.exceptions.AlreadyEsistsException;
import com.u3.shoppingcart.exceptions.ResourceNOtFoundException;
import com.u3.shoppingcart.model.User;
import com.u3.shoppingcart.repository.UserRepository;
import com.u3.shoppingcart.request.CreateUserRequest;
import com.u3.shoppingcart.request.UserUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService implements IUserService{

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;


    @Override
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNOtFoundException("User not found"));
    }

    @Override
    public User createUser(CreateUserRequest request) {
        return Optional.of(request)
                .filter(user -> !userRepository.existsByEmail(request.getEmail()))
                .map(req -> {
                    User user = new User();
                    user.setEmail(request.getEmail());
                    user.setPassword(passwordEncoder.encode(request.getPassword()));
                    user.setFirstName(request.getFirstName());
                    user.setLastName(request.getLastName());
                    return userRepository.save(user);
                }).orElseThrow(()-> new AlreadyEsistsException("Oops!" + request.getEmail() + " already exists!"));
    }

    @Override
    public User updateUser(UserUpdateRequest request, Long userId) {
        return userRepository.findById(userId).map(existingUser -> {
            existingUser.setFirstName(request.getFirstName());
            existingUser.setLastName(request.getLastName());
            return userRepository.save(existingUser);
        })
                .orElseThrow(() -> new ResourceNOtFoundException("User not found"));
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.findById(userId).ifPresentOrElse(userRepository::delete, () ->{
            throw new ResourceNOtFoundException("User not found");
        } );
    }
    @Override
    public UserDto convertUsertoDto(User user){
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return userRepository.findByEmail(email);
    }

}
