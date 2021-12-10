package com.uwgb.GBCoin;

import com.uwgb.GBCoin.API.Exceptions.TransactionException;
import com.uwgb.GBCoin.API.Services.WalletService;
import com.uwgb.GBCoin.Miner.Miner;
import com.uwgb.GBCoin.Miner.MinerNetwork;
import com.uwgb.GBCoin.Model.*;
import com.uwgb.GBCoin.Utils.Crypto;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class MainTest {

    public static void main(String[] args) throws Exception {

        //WalletService service = new WalletService();
        Wallet walletCoinbase = new Wallet();
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
        byte[] coinbaseSig = Crypto.signMessage(walletCoinbase.getPrivateKey(), coinbaseTransaction.getDataToSign(0));
        coinbaseTransaction.addSignature(coinbaseSig, 0);
        //miner1.getTransactionPool().addTransaction(coinbaseTransaction, coinbaseTransaction.getInputs().get(0).getPrevTxHash());

        //add coinbase utxo
        UTXO coinbaseUTXO = new UTXO(coinbaseTransaction.getHash(), 0);
        pool.addUTXO(coinbaseUTXO, coinbaseTransaction.getOutputs().get(0));

        GenesisBlock block = new GenesisBlock(walletA.getPublicKey(), System.currentTimeMillis(), coinbaseTransaction);
        block.hashObject();
        blockChain.addBlock(block);

        miner1.getTransactionPool().clearTransactions();

        network.setNextBlock(block);
        network.notifyObserver(null,null);
        BlockChain.printBlockChain(blockChain);

        while (true) {
            //TODO update next transaction list
            generateTransactions(walletA, walletB, 1, miner1.getTransactionPool());
//            generateTransactions(walletB, walletA, 2, miner1.getTransactionPool());

//            Transaction tx1 = new Transaction();
//            tx1.addInput(coinbaseTransaction.getHash(), 0);
//            tx1.addOutput(50.0d, walletB.getPublicKey());
//            byte[] signature1 = Crypto.signMessage(walletA.getPrivateKey(), tx1.getDataToSign(0));
//            tx1.addSignature(signature1, 0);
//            tx1.hashObject();
//            miner1.getTransactionPool().addTransaction(tx1, tx1.getHash());

            miner1.mineNewBlock();
            BlockChain.printBlockChain(miner1.getBlockChain());


        }



    }


    public static void positiveTest(){

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