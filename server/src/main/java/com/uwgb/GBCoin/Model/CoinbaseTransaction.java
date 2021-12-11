package com.uwgb.GBCoin.Model;

import com.uwgb.GBCoin.Utils.SHAUtils;

import java.io.IOException;
import java.security.PublicKey;
import java.util.Arrays;

/**
 * A CoinbaseTransaction is a transaction that is created when a miner finds a new block
 * So they are rewarded with this transaction for their efforts
 */
public class CoinbaseTransaction extends Transaction{

    private static final byte[] nullAddress = new byte[32];

    /**
     * Simple coinbase transaction for which is created/paid when a miner
     * successfully mines a block. the previous hash is null and the index is 0
     * the output is set to a static 10.0 coins
     * @param publicKey the recipient who should get the coins. i.e. the miners address
     */
    public CoinbaseTransaction(PublicKey publicKey) {
        super();
        Arrays.fill(nullAddress, (byte) 0);
        double blockReward = calculateBlockReward();
        this.addInput(nullAddress, 0);
        this.addSignature(nullAddress,0);
        this.addOutput(blockReward, publicKey);
        try {
            super.hashObject();
            System.out.println(super.getHash());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * method to calculate the current block reward
     * which halves every 1000 blocks
     * starting block reward is 100 gbcoins
     * @return the block reward as a double
     */
    public static double calculateBlockReward(){
        int rewardTier = ((Block.getBlockHeight() / 1000) + 1);
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

    @Override
    public String toString(){
        return ("Transaction Reward: "+ String.valueOf(calculateBlockReward()) + " " + SHAUtils.encodeBytes(super.getOutputs().get(0).getPublicKey().getEncoded()));
    }
}
