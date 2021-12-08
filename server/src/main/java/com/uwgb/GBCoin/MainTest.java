package com.uwgb.GBCoin;

import com.uwgb.GBCoin.API.Exceptions.TransactionException;
import com.uwgb.GBCoin.API.Services.WalletService;
import com.uwgb.GBCoin.Miner.Miner;
import com.uwgb.GBCoin.Miner.MinerNetwork;
import com.uwgb.GBCoin.Model.*;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

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

        miner1.getTransactionPool().clearTransactions();

        network.setNextBlock(block);
        network.notifyObserver(null,null);
        BlockChain.printBlockChain(blockChain);

//        HashMap<Wallet, Integer> data = new HashMap<>();
//        data.put(walletA, 2);
//        data.put(walletB, 2);

        while (true) {
            //TODO update next transaction list
            generateTransactions(walletA, walletB, 2, miner1.getTransactionPool());
            generateTransactions(walletB, walletA, 2, miner1.getTransactionPool());


            //start miners

            //set the miner's new transaction list to the combined list of A and B
            //miner1.setNewTransactions(newTransactionsA);

            miner1.mineNewBlock();


        }


    }

    private static void generateTransactions(Wallet spender, Wallet receiver, int numberOfTransactions, TransactionPool transactionPool) throws TransactionException, NoSuchAlgorithmException, SignatureException, IOException, InvalidKeyException {

        for (int i = 0; i < numberOfTransactions; i++) {
            double maxSpendAmount = transactionPool.getTotalCoins(spender.getPublicKey());
            double randomAmount = ThreadLocalRandom.current().nextDouble(0, maxSpendAmount / 2);

            HashMap<PublicKey, Double> data = new HashMap<>();
            data.put(receiver.getPublicKey(), randomAmount);
            transactionPool.spendNewTransaction(randomAmount, spender, data);
        }
//        return transactionPool.getTransactionList();
    }

    private static List<Transaction> generateTransactions(HashMap<Wallet, Integer> data, TransactionPool transactionPool) throws TransactionException, NoSuchAlgorithmException, SignatureException, IOException, InvalidKeyException {
        Iterator<Wallet> iterator = data.keySet().iterator();

        Map.Entry<Wallet, Integer> entry = data.entrySet().iterator().next();
        Wallet firstWallet = entry.getKey();

        //fill in iterator has next here
//        while()

        for (Wallet wallet: data.keySet()) {
            int numOfTransactions = data.get(wallet);

            for (int i = 0; i < numOfTransactions; i++) {
                double maxSpendAmount = transactionPool.getTotalCoins(wallet.getPublicKey());
                double randomAmount = ThreadLocalRandom.current().nextDouble(0, maxSpendAmount / 2);

                Wallet receiverWallet;
                if (iterator.hasNext()){
                    receiverWallet = iterator.next();
                } else {
                    receiverWallet = firstWallet;
                }

                HashMap<PublicKey, Double> receiverKeys = new HashMap<>();
                receiverKeys.put(receiverWallet.getPublicKey(), randomAmount);

                transactionPool.spendNewTransaction(randomAmount, wallet, receiverKeys);
            }
        }

        return transactionPool.getTransactionList();
    }


}