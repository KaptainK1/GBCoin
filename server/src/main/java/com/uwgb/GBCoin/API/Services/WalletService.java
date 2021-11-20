package com.uwgb.GBCoin.API.Services;

import com.uwgb.GBCoin.API.Repositories.WalletRepository;
import com.uwgb.GBCoin.Model.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;

    public void addWallet(Wallet wallet){
        walletRepository.save(wallet);
    }

    public List<Wallet> getAllWallets(){
        return walletRepository.findAll();
    }

}
