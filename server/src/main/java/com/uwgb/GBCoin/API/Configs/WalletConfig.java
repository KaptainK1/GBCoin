package com.uwgb.GBCoin.API.Configs;

import com.uwgb.GBCoin.API.Repositories.UserRepository;
import com.uwgb.GBCoin.API.Repositories.WalletRepository;
import com.uwgb.GBCoin.Model.User;
import com.uwgb.GBCoin.Model.Wallet;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WalletConfig {


    @Bean
    CommandLineRunner commandLineRunner(
            WalletRepository repository
    ) {
        return args -> {
            repository.save(
                new Wallet()
            );
            repository.save(new Wallet());
        };
    }
}
