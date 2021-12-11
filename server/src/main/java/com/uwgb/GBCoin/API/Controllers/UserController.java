package com.uwgb.GBCoin.API.Controllers;

import com.uwgb.GBCoin.API.Services.UserService;
import com.uwgb.GBCoin.Model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getAllUsers(){
        return userService.getAllUsers();
    }

    @GetMapping("/users/user")
    public boolean isSuccessful(Long id, String  username, String privateKey) {
        return this.userService.isSuccessful(id, username, privateKey);
    }
}
