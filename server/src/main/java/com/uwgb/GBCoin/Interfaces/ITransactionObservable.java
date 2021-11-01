package com.uwgb.GBCoin.Interfaces;

public interface ITransactionObservable {

    void addObserver(ITransactionObserver observer);
    void removeObserver(ITransactionObserver observer);
    void notifyObserver();
}
