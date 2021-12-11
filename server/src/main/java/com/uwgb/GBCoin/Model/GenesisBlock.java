package com.uwgb.GBCoin.Model;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The GenesisBlock, which is instantiated only once, is the beginning of the blockchain
 * Here we use it to assume it was found automatically
 */
public class GenesisBlock extends Block{

    public GenesisBlock(PublicKey publicKey, long timeStamp, CoinbaseTransaction coinbaseTransaction) {
        super(publicKey, null, timeStamp, new ArrayList<>(List.of(coinbaseTransaction)));
    }


}
