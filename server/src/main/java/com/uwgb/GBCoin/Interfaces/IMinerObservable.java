package com.uwgb.GBCoin.Interfaces;

public interface IMinerObservable {

    void addObserver(IMinerObserver observer);
    void removeObserver(IMinerObserver observer);
    void notifyObserver(String challenge, String nonce);
}
