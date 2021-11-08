package com.uwgb.GBCoin.Model;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;

public class GenesisBlock extends Block{

    public GenesisBlock(PublicKey publicKey, long timeStamp) {
        super(publicKey, null, timeStamp, new ArrayList<Transaction>());
    }


}
