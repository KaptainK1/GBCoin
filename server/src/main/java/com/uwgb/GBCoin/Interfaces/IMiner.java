package com.uwgb.GBCoin.Interfaces;

import com.uwgb.GBCoin.Model.Block;

public interface IMiner {

    boolean validateTransactions();
    void receiveTransaction(byte[] privateKey, byte[] publicKey, double amount);
    boolean mineNewBlock();

}
