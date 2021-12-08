package com.uwgb.GBCoin.Miner;

import com.uwgb.GBCoin.Interfaces.IMinerObservable;
import com.uwgb.GBCoin.Interfaces.IMinerObserver;
import com.uwgb.GBCoin.Model.Block;
import com.uwgb.GBCoin.Model.GenesisBlock;

import java.util.HashSet;

/**
 * This is a basic publisher subscriber pattern
 * Whereas miner object subscribe to a miner network
 * So when a new block is found, it can be distributed to other nodes
 */
public class MinerNetwork implements IMinerObservable {

    //The set of all the miners subscribed
    private HashSet<IMinerObserver> minerNetwork;

    //A reference variable for the next block that was found
    private Block nextBlock = null;

    //FYI this is a code smell, this variable should not have global scope
    //since any class can change it
    public static boolean isSolved = false;

    /**
     * Initialize the miner network with a blank set of subscribed miners
     */
    public MinerNetwork() {
        minerNetwork = new HashSet<>();
    }

    /**
     * Method to add a new miner (observer) to be subscribed
     * @param observer the Miner that is subscribing to the network
     */
    @Override
    public void addObserver(IMinerObserver observer) {
        minerNetwork.add(observer);
    }

    /**
     * Method to remove a miner from the network
     * @param observer the Miner that is unsubscribing from the network
     */
    @Override
    public void removeObserver(IMinerObserver observer) {
        minerNetwork.remove(observer);
    }

    /**
     * Method to notify all subscribed miners of a new block that is found
     * @param challenge the challenge that was solved
     * @param nonce the nonce that solves the challenge
     */
    @Override
    public void notifyObserver(String challenge, String nonce) {
        assert (nextBlock != null);
        for (IMinerObserver observer: minerNetwork) {
            observer.updateMiner(challenge, nonce);
        }
        MinerNetwork.isSolved = false;
        setNextBlock(null);
    }

    /**
     * Method to set the next block to the one that the miner found
     * @param nextBlock the next block
     */
    public void setNextBlock(Block nextBlock){
        this.nextBlock = nextBlock;
    }

    /**
     * Method to get the block that was solved
     * @return the next block
     */
    public Block getNextBlock() {
        return nextBlock;
    }

    public void setState(){

    }

    public boolean isSolved() {
        return isSolved;
    }

    public void setSolved(boolean solved) {
        isSolved = solved;
    }
}
