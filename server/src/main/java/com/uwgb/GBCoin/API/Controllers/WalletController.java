package com.uwgb.GBCoin.API.Controllers;


import com.uwgb.GBCoin.API.Repositories.WalletRepository;
import com.uwgb.GBCoin.API.Services.WalletService;
import com.uwgb.GBCoin.Model.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/")
public class WalletController {

    private final WalletService walletService;

    @Autowired
    public WalletController(WalletService walletService){
        this.walletService = walletService;
    }


    //@GetMapping("/wallets")
    public List<Wallet> getAllWallets(){
        return walletService.getAllWallets();
    }

}
