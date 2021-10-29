package com.uwgb.GBCoin.Miner;

import com.uwgb.GBCoin.Interfaces.IMinerObservable;
import com.uwgb.GBCoin.Interfaces.IMinerObserver;
import com.uwgb.GBCoin.Model.Block;

import java.util.HashSet;

public class MinerNetwork implements IMinerObservable {

    private HashSet<IMinerObserver> minerNetwork;
    private Block nextBlock = null;

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
    public void notifyObserver() {
        assert (nextBlock != null);
        for (IMinerObserver observer: minerNetwork) {
            observer.updateMiner();
        }
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

}
