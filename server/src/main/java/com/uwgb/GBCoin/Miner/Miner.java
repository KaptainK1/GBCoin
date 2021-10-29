package com.uwgb.GBCoin.Miner;

import com.uwgb.GBCoin.Interfaces.IMiner;
import com.uwgb.GBCoin.Interfaces.IMinerObserver;
import com.uwgb.GBCoin.Interfaces.ITransactionObserver;
import com.uwgb.GBCoin.Model.*;
import com.uwgb.GBCoin.ProofOfWork.HashCash;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Miner implements IMinerObserver, IMiner, ITransactionObserver {

    //TODO figure out if we even need a graph data structure for these?
    private HashSet<Miner> adjacentMiners;

    private PublicKey publicKey;
    private boolean hasPuzzleBeenSolved = false;
    private MinerNetwork minerNetwork;
    private TransactionNetwork transactionNetwork;
    private BlockChain blockChain;
    private HashCash hashCash;
    private TransactionHandler transactionHandler;
    private static UTXOPool utxoPool;
    private List<Transaction> newTransactions;

    public Miner(MinerNetwork minerNetwork, TransactionNetwork transactionNetwork, HashSet<Miner> adjacentMiners, PublicKey publicKey, BlockChain blockChain) {
        this.adjacentMiners = adjacentMiners;
        this.publicKey = publicKey;
        this.minerNetwork = minerNetwork;
        this.transactionNetwork = transactionNetwork;
        this.blockChain = blockChain;
        init();
    }

    public Miner(MinerNetwork minerNetwork, TransactionNetwork transactionNetwork, PublicKey publicKey, BlockChain blockChain) {
        this.adjacentMiners = new HashSet<>();
        this.publicKey = publicKey;
        this.minerNetwork = minerNetwork;
        this.transactionNetwork = transactionNetwork;
        this.blockChain = blockChain;
        init();
    }

    public HashSet<Miner> getAdjacentMiners() {
        return adjacentMiners;
    }

    private void init(){
        //TODO figure out where we want to get the current utxo pool from
        // we could use our database if we want too, which is what bitcoin uses
        UTXOPool utxoPool = new UTXOPool();

        this.transactionHandler = new TransactionHandler(utxoPool);
        this.newTransactions = new ArrayList<>();
    }

    public void setAdjacentMiners(HashSet<Miner> adjacentMiners) {
        this.adjacentMiners = adjacentMiners;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    //IMinerObserver interface functions
    @Override
    public void updateMiner() {
        //TODO need to implement this update miner code
        //Pseudocode:
        //get the proposed block
        //check to see if the hash of the block is truly solved then
        //do a check to see if this block is already extended onto the block-chain
            //if not begin validation
        //if (validateTransactions()){
            //Don't extend the blockchain as the block that was found contains invalid transactions
            //continue mining next block
        //if all looks well, extend our version of the block-chain
            //then mine next block
        //}
        System.out.println("updated!");
        Block block = this.minerNetwork.getNextBlock();
        System.out.println("Block name is: " + block.name);
        System.out.println("Block height is: " + Block.blockHeight);
    }
    // end IMinerObserver interface functions

    //ITransactionObserver interface functions
    @Override
    public void updateTransaction() {
        //TODO need to handle when we hear about a new transaction
        System.out.println("Transaction1 has been added");
    }
    // end ITransactionObserver interface functions

    //IMiner interface functions
    @Override
    public boolean validateTransactions() {
        return false;
    }

    @Override
    public boolean mineNewBlock() {
        //TODO need to implement this code
        //create new instance of a coinbase transaction
        //add new transaction to transaction list
        //create merkle tree with said transaction list
        //hash the block
        //now begin mining
        hashCash = new HashCash(this.getPublicKey().toString());
        return false;
    }
    // end IMiner interface functions


    public static void main(String args[]) throws Exception {
        Wallet walletA = new Wallet();
        Wallet walletB = new Wallet();

        Block block = new Block("test1");

        MinerNetwork network = new MinerNetwork();
        TransactionNetwork transactionNetwork = new TransactionNetwork();

        Miner miner1 = new Miner(network, transactionNetwork, walletA.getPublicKey(), null);
        Miner miner2 = new Miner(network, transactionNetwork, walletB.getPublicKey(), null);

        network.addObserver(miner1);
        network.addObserver(miner2);

        transactionNetwork.addObserver(miner1);
        transactionNetwork.addObserver(miner2);

        network.setNextBlock(block);
        network.notifyObserver();
    }


}
