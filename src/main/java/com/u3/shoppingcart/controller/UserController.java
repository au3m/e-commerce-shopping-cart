package com.u3.shoppingcart.controller;

import com.u3.shoppingcart.dto.UserDto;
import com.u3.shoppingcart.exceptions.AlreadyEsistsException;
import com.u3.shoppingcart.exceptions.ResourceNOtFoundException;
import com.u3.shoppingcart.model.User;
import com.u3.shoppingcart.request.CreateUserRequest;
import com.u3.shoppingcart.request.UserUpdateRequest;
import com.u3.shoppingcart.response.ApiResponse;
import com.u3.shoppingcart.service.user.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/users")
public class UserController {
    private final IUserService userService;
    private final RestTemplateAutoConfiguration restTemplateAutoConfiguration;


    @GetMapping("/{userId}/user")
    public ResponseEntity<ApiResponse> getUserById(@PathVariable Long userId){
        try {
            User user = userService.getUserById(userId);
            UserDto userDto = userService.convertUsertoDto(user);
            return ResponseEntity.ok(new ApiResponse("Success", userDto));
        } catch (ResourceNOtFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> createUser(@RequestBody CreateUserRequest request){
        try {
            User user = userService.createUser(request);
            UserDto userDto = userService.convertUsertoDto(user);
            return ResponseEntity.ok(new ApiResponse("Create User Success!", userDto));
        } catch (AlreadyEsistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PutMapping("/{userId}/update")
    public ResponseEntity<ApiResponse> updateUser(@RequestBody UserUpdateRequest request, @PathVariable Long userId){
        try {
            User user = userService.updateUser(request, userId);
            UserDto userDto = userService.convertUsertoDto(user);
            return ResponseEntity.ok(new ApiResponse("Update User Success!", userDto));
        } catch (ResourceNOtFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }



    @DeleteMapping("/{userId}/delete")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable Long userId){
        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok(new ApiResponse("Delete User Success!", null));
        } catch (ResourceNOtFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }

    }

}
