package com.uwgb.GBCoin;

import com.uwgb.GBCoin.API.Services.WalletService;
import com.uwgb.GBCoin.Miner.Miner;
import com.uwgb.GBCoin.Miner.MinerNetwork;
import com.uwgb.GBCoin.Model.*;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class MainTest {

    public static void main(String[] args) throws Exception {

        //WalletService service = new WalletService();
        Wallet walletA = new Wallet();
        Wallet walletB = new Wallet();
        UTXOPool pool = new UTXOPool();
        BlockChain blockChain = new BlockChain();
        MinerNetwork network = new MinerNetwork();
        TransactionNetwork transactionNetwork = new TransactionNetwork();
        Miner miner1 = new Miner(network, transactionNetwork, walletA.getPublicKey(), blockChain, pool);
        Miner miner2 = new Miner(network, transactionNetwork, walletB.getPublicKey(), blockChain, pool);
        network.addObserver(miner1);
        network.addObserver(miner2);
        transactionNetwork.addObserver(miner1);
        transactionNetwork.addObserver(miner2);

        //assume Genesis Block and Coinbase Transaction was already found
        CoinbaseTransaction coinbaseTransaction = new CoinbaseTransaction(walletA.getPublicKey());
        miner1.getTransactionPool().addTransaction(coinbaseTransaction);

        GenesisBlock block = new GenesisBlock(walletA.getPublicKey(), System.currentTimeMillis());
        block.hashObject();
        blockChain.addBlock(block);

        network.setNextBlock(block);
        network.notifyObserver(null,null);
        BlockChain.printBlockChain(blockChain);

        while (true) {
            //TODO update next transaction list
            //start miners


        }
    }


}