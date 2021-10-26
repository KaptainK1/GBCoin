package com.uwgb.GBCoin.Controllers;


import com.uwgb.GBCoin.MSQLHelpers.WalletRepository;
import com.uwgb.GBCoin.Model.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/")
public class WalletController {

    @Autowired
    private WalletRepository walletRepository;

    @GetMapping("/wallets")
    public List<Wallet> getAllWallets(){
        return walletRepository.findAll();
    }

}
