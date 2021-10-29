package com.uwgb.GBCoin.Interfaces;

public interface ITransactionObservable {

    void addObserver(IMinerObserver observer);
    void removeObserver(IMinerObserver observer);
    void notifyObserver();
}
