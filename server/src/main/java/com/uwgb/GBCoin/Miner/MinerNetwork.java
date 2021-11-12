package com.uwgb.GBCoin.Miner;

import com.uwgb.GBCoin.Interfaces.IMinerObservable;
import com.uwgb.GBCoin.Interfaces.IMinerObserver;
import com.uwgb.GBCoin.Model.Block;
import com.uwgb.GBCoin.Model.GenesisBlock;

import java.util.HashSet;

public class MinerNetwork implements IMinerObservable {

    private HashSet<IMinerObserver> minerNetwork;
    private Block nextBlock = null;

    //FYI this is a code smell, this variable should not have global scope
    //since any class can change it
    public static boolean isSolved = false;

    public MinerNetwork() {
        minerNetwork = new HashSet<>();
    }

    @Override
    public void addObserver(IMinerObserver observer) {
        minerNetwork.add(observer);
    }

    @Override
    public void removeObserver(IMinerObserver observer) {
        minerNetwork.remove(observer);
    }

    @Override
    public void notifyObserver(String challenge, String nonce) {
        assert (nextBlock != null);
        for (IMinerObserver observer: minerNetwork) {
            observer.updateMiner(challenge, nonce);
        }
        MinerNetwork.isSolved = false;
        setNextBlock(null);
    }

    public void setNextBlock(Block nextBlock){
        this.nextBlock = nextBlock;
    }

    // return the next block that was supposedly solved.
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
