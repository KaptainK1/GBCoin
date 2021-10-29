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
        double blockReward = calculateBlockReward();
        this.addInput(prevTxHash, 0);
        this.addOutput(blockReward, publicKey);
    }

    /**
     * method to calculate the current block reward
     * which halves every 1000 blocks
     * starting block reward is 100 gbcoins
     * @return the block reward as a double
     */
    private static double calculateBlockReward(){
        int rewardTier = ((Block.blockHeight / 1000) + 1);
        double blockReward = 100.0d;
        for (int i = 1; i < rewardTier; i++) {
            blockReward /= 2;
        }
        return blockReward;
    }

    /*
    public static void main(String args[]){
        int rewardTier = ((420001 / 210000) + 1);
        System.out.println("Reward tier is: " + rewardTier);
        double base = 50.0d;
        for (int i = 1; i < rewardTier; i++) {
            base /= 2;
        }
        System.out.println(50.0d / 2);
        System.out.println("base tier is: " + base);
        System.out.println("Reward tier is: " + rewardTier);
        double blockReward = (50.0d / rewardTier);
        System.out.println("Block reward is : " + blockReward + " gbcoins");
    }
    */
}
