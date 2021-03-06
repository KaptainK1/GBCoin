package com.uwgb.GBCoin.Miner;

import com.uwgb.GBCoin.Interfaces.IMiner;
import com.uwgb.GBCoin.Interfaces.IMinerObserver;
import com.uwgb.GBCoin.Interfaces.ITransactionObserver;
import com.uwgb.GBCoin.Model.*;
import com.uwgb.GBCoin.ProofOfWork.HashCash;
import com.uwgb.GBCoin.Utils.SHAUtils;
//import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.PublicKey;
import java.util.*;

/**
 * Main Miner class
 */
public class Miner implements IMinerObserver, IMiner, ITransactionObserver {

    private PublicKey publicKey;
    private boolean hasPuzzleBeenSolved = false;
    private MinerNetwork minerNetwork;
    private TransactionNetwork transactionNetwork;
    private BlockChain blockChain;
    private List<Miner> connectedMiners;

    private TransactionPool transactionPool;
    private List<Transaction> newTransactions;
    private List<Transaction> currentTransactions;
    private boolean keepMining = false;

    /**
     * A miner is a node that is actively miner for new blocks as well as verifying transactions
     * @param minerNetwork the network the miner is apart of
     * @param transactionNetwork the transaction network that miners are apart of
     * @param publicKey the public key associated with this miner
     * @param blockChain a copy of the block chain
     * @param utxoPool a reference to the utxo pool set
     */
    public Miner(MinerNetwork minerNetwork, TransactionNetwork transactionNetwork, PublicKey publicKey, BlockChain blockChain, UTXOPool utxoPool) {
//        this.adjacentMiners = adjacentMiners;
        this.publicKey = publicKey;
        this.minerNetwork = minerNetwork;
        this.transactionNetwork = transactionNetwork;
        this.blockChain = new BlockChain(blockChain);
        this.transactionPool = new TransactionPool(utxoPool);
        this.currentTransactions = new ArrayList<>();
        this.newTransactions = new ArrayList<>();
        this.connectedMiners = new LinkedList<>();
    }

//    public void setAdjacentMiners(HashSet<Miner> adjacentMiners) {
//        this.adjacentMiners = adjacentMiners;
//    }

    public void shareNetworkTransaction(Transaction transaction){
        for (Miner miner: connectedMiners) {

        }
    }

    /**
     * Method to get the public key
     * @return the public key
     */
    public PublicKey getPublicKey() {
        return publicKey;
    }

    /**
     * Method ot set the public key
     * @param publicKey
     */
    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    //IMinerObserver interface functions

    /**
     * Method to update other miners when a puzzle has been solved and a block is being proposed for consensus
     * @param challenge the challenge that was solved
     * @param nonce the nonce that solves the challenge
     */
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

    /**
     * Method to check to see if a block is valid
     * @param block the block that needs to be checked
     * @param challenge the challenge that was solved
     * @param nonce the nonce that solves the challenge
     * @return True if the block is successfully solved
     */
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
            System.out.println("Number of blocks:" + Block.getBlockHeight());
            System.out.println("current chain:" + blockChain.getBlockHeight());

            if (!isHashValid(blockChain.getCurrentHash(), block)){
                return false;
            }

            if (Block.getBlockHeight() > blockChain.getBlockHeight() -1) {
                if (HashCash.isValidSolution(challenge, nonce, 8)) {
                    return true;
                }
            }

            return false;

        }
    }

    /**
     * Method to check if the hash of a block is a valid hash
     * @param prevBlockHash the prevBlockHash is the hash of the block BEFORE this block in the chain
     * @param block the current block
     * @return IF the prevBlockHash is equal to the block's prev Hash, then it is valid and the chain can continue
     * so return true, if not, then this represents a break in the chain, so return false and don't add this block
     */
    private boolean isHashValid(byte[] prevBlockHash, Block block){
        byte[] prevHash = Arrays.copyOf(prevBlockHash, prevBlockHash.length);
        byte[] currentBlockHash = Arrays.copyOf(block.getPreviousHash(), block.getPreviousHash().length);

        for (int i = 0; i < currentBlockHash.length; i++) {
            if (prevHash[i] != currentBlockHash[i])
                return false;
        }
        return true;
    }

    //ITransactionObserver interface functions

    /**
     * Method to update this miner when there is a new transaction
     */
    @Override
    public void updateTransaction() {
        Transaction t = this.transactionNetwork.getTransaction();
        this.newTransactions.add(t);
        System.out.println(t);
        System.out.println("Transaction has been added");
    }
    // end ITransactionObserver interface functions

    //IMiner interface functions

    /**
     * Method to get an array of valid transactions given a set of PENDING transactions
     * @param pendingTransactions that need to be valid
     * @return returns an array of transactions that are valid
     */
    @Override
    public Transaction[] getValidTransactions(List<Transaction> pendingTransactions){
        //create a transaction handler
        TransactionHandler txHandler = new TransactionHandler(this.getTransactionPool().getUtxoPool());

        //array to hold the Transactions that need to be verified
        Transaction[] testTransactions = new Transaction[pendingTransactions.size()];

        //convert to an array list
        pendingTransactions.toArray(testTransactions);

        //get the resulting array of transactions that are verified
        //FYI this method also handles creating and removing coins (UTXOs)
        return txHandler.handleTransactions(testTransactions);

    }

//    @Override
//    public void receiveTransaction(byte[] privateKey, byte[] publicKey, double amount) {
//
//    }

    /**
     * Main method to start mining for a new block
     */
    @Override
    public void mineNewBlock() {
        //TODO need to implement this code
        //initialize the transactions to add to the block to the list of the new transactions
        this.currentTransactions = new ArrayList<>(transactionPool.getTransactionList());

        //clear out the new transactions list now
        this.transactionPool.clearTransactions();

        //get the current timestamp
        long timeStamp = System.currentTimeMillis();
        //long nonce = 0;

        //add coinbase Transaction
        this.addCoinbaseTransaction(currentTransactions);

        //set Block to valid transactions
        Transaction[] results = this.getValidTransactions(currentTransactions);

        ArrayList<Transaction> acceptedTransactions = new ArrayList<>(Arrays.stream(results).toList());

        //create the block
        Block block = new Block(this.getPublicKey(), blockChain.getCurrentHash(), timeStamp, acceptedTransactions);

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

    /**
     * Method to add a coinbase transaction
     * which is done when a miner successfully finds a new block
     * @param transactions the list of transactions to where this coinbase transaction needs to be added to
     */
    private void addCoinbaseTransaction(List<Transaction> transactions){
        CoinbaseTransaction coinbaseTransaction = new CoinbaseTransaction(this.getPublicKey());
        transactions.add(coinbaseTransaction);
    }

    /**
     * Method to load the next set of transaction
     * Current this is not being utilized
     * @return the transaction list that was loaded
     */
    public ArrayList<Transaction> loadNextTransactions(){
        ArrayList<Transaction> transactions = new ArrayList<>();
        //TODO figure out where to load this transactions from
        return transactions;
    }

    /**
     * Method to get the new transactions that are not yet in the chain
     * @return the list of new transactions
     */
    public List<Transaction> getNewTransactions() {
        return newTransactions;
    }

    /**
     * Method to set the new transactions that are not yet in the chain
     * @param newTransactions the list of new transactions
     */
    public void setNewTransactions(List<Transaction> newTransactions) {
        this.newTransactions = newTransactions;
    }

    /**
     * Method to get the transactions that are in a block waiting to be put onto the block chain
     * @return the list of transactions
     */
    public List<Transaction> getCurrentTransactions() {
        return currentTransactions;
    }

    /**
     * Method to get the transactions that are in a block waiting to be put onto the block chain
     * @param currentTransactions the list of transactions
     */
    public void setCurrentTransactions(List<Transaction> currentTransactions) {
        this.currentTransactions = currentTransactions;
    }

    /**
     * Method to get the reference to the transaction pool
     * @return the transaction pool
     */
    public TransactionPool getTransactionPool() {
        return transactionPool;
    }

    /**
     * Method to set the reference to the transaction pool
     * @param transactionPool the reference to the transaction pool
     */
    public void setTransactionPool(TransactionPool transactionPool) {
        this.transactionPool = transactionPool;
    }

    public BlockChain getBlockChain(){
        return this.blockChain;
    }

    public static void main(String[] args) {
        //Begin Network Variables
        ServerSocket server = null;
        try {
            server = new ServerSocket(8000);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Socket socket = null;
         InputStreamReader inputStreamReader = null;
         OutputStreamWriter outputStreamWriter = null;
         BufferedWriter bufferedWriter = null;
         BufferedReader bufferedReader = null;

         try {
             assert server != null;
             socket = server.accept();
             inputStreamReader = new InputStreamReader(socket.getInputStream());
             outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());

             bufferedReader = new BufferedReader(inputStreamReader);
             bufferedWriter = new BufferedWriter(outputStreamWriter);

             while (true){

             }


         } catch (IOException e) {
             e.printStackTrace();
         }

    }
}
