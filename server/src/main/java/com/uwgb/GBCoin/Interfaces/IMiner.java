package com.uwgb.GBCoin.Interfaces;

import com.uwgb.GBCoin.Model.Block;
import com.uwgb.GBCoin.Model.Transaction;

import java.util.ArrayList;
import java.util.List;

public interface IMiner {

//    boolean validateTransactions(ArrayList<Transaction> transactions);
    //void receiveTransaction(byte[] privateKey, byte[] publicKey, double amount);
    void mineNewBlock();
    Transaction[] getValidTransactions(List<Transaction> transactions);

}
