package com.uwgb.GBCoin.API.Services;

import com.uwgb.GBCoin.API.Repositories.UserRepository;
import com.uwgb.GBCoin.Model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public boolean isSuccessful(Long id, String  username, String privateKey){
        if (!userRepository.existsById(id))
            return false;

        User user = userRepository.getById(id);
        if (Objects.equals(user.getUserName(), username) && Objects.equals(user.getPrivateKey(), privateKey) )
            return true;
        else {
            return false;
        }
    }
}
