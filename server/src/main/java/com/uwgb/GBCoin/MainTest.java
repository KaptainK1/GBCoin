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

        //Create a few simple wallets
        Wallet walletCoinbase = new Wallet();
        Wallet walletA = new Wallet();
        Wallet walletB = new Wallet();

        //create the utxo pool which is shared between miners
        UTXOPool pool = new UTXOPool();

        //create the blockchain
        BlockChain blockChain = new BlockChain();

        //create the minter network and transaction networks
        //these are simple publisher/subscriber patterns to simulate a distributed network
        MinerNetwork network = new MinerNetwork();
        TransactionNetwork transactionNetwork = new TransactionNetwork();

        //create two miners in this environment to simulate a distributed network
        Miner miner1 = new Miner(network, transactionNetwork, walletA.getPublicKey(), blockChain, pool);
        Miner miner2 = new Miner(network, transactionNetwork, walletB.getPublicKey(), blockChain, pool);

        //add each miner to the miner network and transaction network
        network.addObserver(miner1);
        network.addObserver(miner2);
        transactionNetwork.addObserver(miner1);
        transactionNetwork.addObserver(miner2);

        //assume Genesis Block and Coinbase Transaction was already found
        CoinbaseTransaction coinbaseTransaction = new CoinbaseTransaction(walletA.getPublicKey());
        byte[] coinbaseSig = Crypto.signMessage(walletCoinbase.getPrivateKey(), coinbaseTransaction.getDataToSign(0));
        coinbaseTransaction.addSignature(coinbaseSig, 0);

        //add coinbase utxo to utxo pool as it is available to be spent
        UTXO coinbaseUTXO = new UTXO(coinbaseTransaction.getHash(), 0);
        pool.addUTXO(coinbaseUTXO, coinbaseTransaction.getOutputs().get(0));

        //create the genesis block which we assumed to be found already
        GenesisBlock block = new GenesisBlock(walletA.getPublicKey(), System.currentTimeMillis(), coinbaseTransaction);
        block.hashObject();

        //add said genesis block to the block chain
        blockChain.addBlock(block);

        //clear all transactions from the miner's pool
        miner1.getTransactionPool().clearTransactions();

        //by using the miner network, notify each subscriber of the new genesis block
        network.setNextBlock(block);
        network.notifyObserver(null,null);
        BlockChain.printBlockChain(blockChain);


        //run this loop forever which generates fake transactions then mines a new block
        while (true) {
            // generate a fake transaction from WalletA to WalletB
            //we pick from WalletA > B because in the above scenario, we assume the genesis block was found by minerA
            generateTransactions(walletA, walletB, 1, miner1.getTransactionPool());

            //run the mine block method
            miner1.mineNewBlock();

            //finally, print the miner's version of the blockchain
            BlockChain.printBlockChain(miner1.getBlockChain());

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