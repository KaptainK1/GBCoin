package com.uwgb.GBCoin.Miner;

import com.uwgb.GBCoin.API.Services.WalletService;
import com.uwgb.GBCoin.Interfaces.IMiner;
import com.uwgb.GBCoin.Interfaces.IMinerObserver;
import com.uwgb.GBCoin.Interfaces.ITransactionObserver;
import com.uwgb.GBCoin.Model.*;
import com.uwgb.GBCoin.ProofOfWork.HashCash;
import com.uwgb.GBCoin.Utils.SHAUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.lang.reflect.Array;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Miner implements IMinerObserver, IMiner, ITransactionObserver {

    private PublicKey publicKey;
    private boolean hasPuzzleBeenSolved = false;
    private MinerNetwork minerNetwork;
    private TransactionNetwork transactionNetwork;
    private BlockChain blockChain;

    private TransactionPool transactionPool;
    private List<Transaction> newTransactions;
    private List<Transaction> currentTransactions;
    private boolean keepMining = true;

    public Miner(MinerNetwork minerNetwork, TransactionNetwork transactionNetwork, PublicKey publicKey, BlockChain blockChain, UTXOPool utxoPool) {
//        this.adjacentMiners = adjacentMiners;
        this.publicKey = publicKey;
        this.minerNetwork = minerNetwork;
        this.transactionNetwork = transactionNetwork;
        this.blockChain = new BlockChain(blockChain);
        this.transactionPool = new TransactionPool(utxoPool);
        this.currentTransactions = new ArrayList<>();
        this.newTransactions = new ArrayList<>();
    }

//    public void setAdjacentMiners(HashSet<Miner> adjacentMiners) {
//        this.adjacentMiners = adjacentMiners;
//    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    //IMinerObserver interface functions
    @Override
    public void updateMiner(String challenge, String nonce) {
        //TODO need to implement this update miner code

        minerNetwork.setSolved(true);
        //Pseudocode:
        //get the proposed block
        Block block = minerNetwork.getNextBlock();

        if (isBlockValid(block, challenge, nonce)){
            blockChain.addBlock(block);
            System.out.println("Block added successfully!");
            System.out.println("Block height is: " + Block.getBlockHeight());
        } else {
            System.out.println("Error with adding block");
        }

    }
    // end IMinerObserver interface functions

    public boolean isBlockValid(Block block, String challenge, String nonce) {
        if (challenge == null && nonce == null) {
            if (!(block instanceof GenesisBlock)) {
                return false;
                //check to see if the coinbase reward is not greater than the current block reward
            } else if (block.getTransactions().get(0).getOutputs().get(0).getValue() > CoinbaseTransaction.calculateBlockReward()) {
                return false;
            } else {
                return true;
            }
        } else {

            //do a check to see if this block is already extended onto the block-chain
            if (Block.getBlockHeight() == blockChain.getBlockHeight()) {
                if (HashCash.isValidSolution(challenge, nonce, 28)) {
                    if (validateTransactions(block.getTransactions())) {
                        return true;
                    }
                }
            }

            return false;

        }
    }

    //ITransactionObserver interface functions
    @Override
    public void updateTransaction() {
        Transaction t = this.transactionNetwork.getTransaction();
        this.newTransactions.add(t);
        System.out.println(t);
        System.out.println("Transaction has been added");
    }
    // end ITransactionObserver interface functions

    //IMiner interface functions
    @Override
    public boolean validateTransactions(ArrayList<Transaction> transactions) {
        for (Transaction tx: transactions){
            if (!transactionPool.getTransactionHandler().isValidTX(tx)){
                return false;
            }
        }
        return true;
    }

//    @Override
//    public void receiveTransaction(byte[] privateKey, byte[] publicKey, double amount) {
//
//    }

    @Override
    public void mineNewBlock() {
        //TODO need to implement this code
        //initialize the transactions to add to the block to the list of the new transactions
        this.currentTransactions = new ArrayList<>(newTransactions);

        //clear out the new transactions list now
        this.newTransactions.clear();

        //get the current timestamp
        long timeStamp = System.currentTimeMillis();
        //long nonce = 0;
        //create the block
        Block block = new Block(this.getPublicKey(), blockChain.getCurrentHash(), timeStamp, currentTransactions);

        try{
            block.hashObject();
        } catch(IOException e){
            e.printStackTrace();
        }

        //Create the puzzle and start mining until it is solved
        HashCash puzzle = new HashCash(SHAUtils.encodeBytes(block.getHash()), timeStamp);
        while(!puzzle.isSolved()){
            puzzle.mine();
        }

        block.setNonce(puzzle.getNonce());

        //set the miner network observable's block to the one we just found
        //this is necessary so other blocks can get this data.

        //TODO pass the block object as a parameter instead
        minerNetwork.setNextBlock(block);

        //update all other miners of the new block that was found
        minerNetwork.notifyObserver(puzzle.getChallenge(), String.valueOf(puzzle.getNonce()));

        //setup the new transations to go into the next block
        this.setNewTransactions(loadNextTransactions());

        //call the mine new block again, which will run forever until the miner closes down.
        if (keepMining)
            mineNewBlock();
    }
    // end IMiner interface functions

    public ArrayList<Transaction> loadNextTransactions(){
        ArrayList<Transaction> transactions = new ArrayList<>();
        //TODO figure out where to load this transactions from
        return transactions;
    }

    public List<Transaction> getNewTransactions() {
        return newTransactions;
    }

    public void setNewTransactions(List<Transaction> newTransactions) {
        this.newTransactions = newTransactions;
    }

    public List<Transaction> getCurrentTransactions() {
        return currentTransactions;
    }

    public void setCurrentTransactions(List<Transaction> currentTransactions) {
        this.currentTransactions = currentTransactions;
    }

    public TransactionPool getTransactionPool() {
        return transactionPool;
    }

    public void setTransactionPool(TransactionPool transactionPool) {
        this.transactionPool = transactionPool;
    }

    public static void main(String[] args) throws Exception {

        positiveTest();
        //network.notifyObserver();
    }

    public static void positiveTest() throws Exception {

        Wallet walletA = new Wallet();
        Wallet walletB = new Wallet();

        UTXOPool pool = new UTXOPool();
        BlockChain blockChain = new BlockChain();

        MinerNetwork network = new MinerNetwork();
        TransactionNetwork transactionNetwork = new TransactionNetwork();

        Miner miner1 = new Miner(network, transactionNetwork, walletA.getPublicKey(), blockChain, pool);
        Miner miner2 = new Miner(network, transactionNetwork, walletB.getPublicKey(), blockChain, pool);

        CoinbaseTransaction coinbaseTransaction = new CoinbaseTransaction(walletA.getPublicKey());
        miner1.getTransactionPool().addTransaction(coinbaseTransaction);

        HashMap<PublicKey, Double> payments = new HashMap<>();
        payments.put(walletB.getPublicKey(), 25.0d);
        miner1.getTransactionPool().spendNewTransaction(25.0d, walletA, payments );

        GenesisBlock block = new GenesisBlock(walletA.getPublicKey(), System.currentTimeMillis());
        block.hashObject();
        blockChain.addBlock(block);

        network.addObserver(miner1);
        network.addObserver(miner2);

        transactionNetwork.addObserver(miner1);
        transactionNetwork.addObserver(miner2);

        network.setNextBlock(block);
        network.notifyObserver(null,null);
        BlockChain.printBlockChain(blockChain);
        System.out.println("Miner A has: " + miner1.getTransactionPool().getTotalCoins(miner1.getPublicKey()) + " GBCoins");
        System.out.println("Miner B has: " + miner1.getTransactionPool().getTotalCoins(miner2.getPublicKey()) + " GBCoins");
    }


}
