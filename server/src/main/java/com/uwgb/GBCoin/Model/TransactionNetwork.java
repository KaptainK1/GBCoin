package com.uwgb.GBCoin.Model;

import com.uwgb.GBCoin.Interfaces.IMinerObservable;
import com.uwgb.GBCoin.Interfaces.IMinerObserver;

import java.util.HashSet;
import java.util.Set;

public class TransactionNetwork implements IMinerObservable {

    private Transaction transaction;

    Set<IMinerObserver> miners;

    public TransactionNetwork() {
        miners = new HashSet<>();
    }

    @Override
    public void addObserver(IMinerObserver observer) {
        miners.add(observer);
    }

    @Override
    public void removeObserver(IMinerObserver observer) {
        miners.remove(observer);
    }

    @Override
    public void notifyObserver() {
        assert transaction != null;
        for (IMinerObserver miner: miners) {
            miner.updateMiner();
        }
        setTransaction(null);
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }
}
