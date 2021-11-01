package com.uwgb.GBCoin.Model;

import com.uwgb.GBCoin.Interfaces.IMinerObservable;
import com.uwgb.GBCoin.Interfaces.IMinerObserver;
import com.uwgb.GBCoin.Interfaces.ITransactionObservable;
import com.uwgb.GBCoin.Interfaces.ITransactionObserver;

import java.util.HashSet;
import java.util.Set;

public class TransactionNetwork implements ITransactionObservable {

    private Transaction transaction;

    Set<ITransactionObserver> miners;

    public TransactionNetwork() {
        miners = new HashSet<>();
    }

    @Override
    public void addObserver(ITransactionObserver observer) {
        miners.add(observer);
    }

    @Override
    public void removeObserver(ITransactionObserver observer) {
        miners.remove(observer);
    }

    @Override
    public void notifyObserver() {
        assert transaction != null;
        for (ITransactionObserver miner: miners) {
            miner.updateTransaction();
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
