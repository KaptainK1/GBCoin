package com.uwgb.GBCoin.Model;

import java.security.PublicKey;


public class CoinbaseTransaction extends Transaction{

    /**
     * Simple coinbase transaction for which is created/paid when a miner
     * successfully mines a block. the previous hash is null and the index is 0
     * the output is set to a static 10.0 coins
     * @param publicKey the recipient who should get the coins. i.e. the miners address
     * @param prevTxHash since there is no previous transaction from which this originates
     *                   this can literally be anything the miner wishes to put into it.
     */
    public CoinbaseTransaction(PublicKey publicKey, byte[] prevTxHash){
        super();
        this.addInput(prevTxHash, 0);
        this.addOutput(10.0d, publicKey);
    }
}
