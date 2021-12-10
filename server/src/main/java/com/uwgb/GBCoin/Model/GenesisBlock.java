package com.uwgb.GBCoin.Model;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GenesisBlock extends Block{

    public GenesisBlock(PublicKey publicKey, long timeStamp, CoinbaseTransaction coinbaseTransaction) {
        super(publicKey, null, timeStamp, new ArrayList<>(List.of(coinbaseTransaction)));
    }


}
