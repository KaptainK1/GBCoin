package com.uwgb.GBCoin.Interfaces;

import com.uwgb.GBCoin.Model.Block;

public interface IMiner {

    boolean validateTransactions();
    boolean mineNewBlock();

}
