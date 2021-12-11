package com.uwgb.GBCoin.Model;

import com.uwgb.GBCoin.Interfaces.HashHelper;
import com.uwgb.GBCoin.MerkleTree.MerkleNode;
import com.uwgb.GBCoin.MerkleTree.MerkleTree;
import com.uwgb.GBCoin.Utils.SHAUtils;

import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

/**
 * A single Block class which contains the block number,
 * hash of the block,
 * hash of the previous block
 * transaction list
 * and the merkle root
 */
public class Block implements HashHelper {
    private static int blockHeight = 0;
    private int blockNumber;
    private byte[] hash;
    private byte[] previousHash;
    private byte[] merkleRoot;
//    private CoinbaseTransaction coinbaseTransaction;
    private ArrayList<Transaction> transactions;

    //these two variables need to be set ONCE the block is solved
    private long timeStamp;
    private long nonce;

    /**
     * Constructor to create a new block
     * @param publicKey the public key of the miner who solved it
     * @param prevHash the hash of the previous block in the block chain
     * @param timeStamp the current timestamp of the block
     * @param transactions the valid transaction set to be included in this block
     */
    public Block(PublicKey publicKey, byte[] prevHash, long timeStamp, List<Transaction> transactions) {
        this.blockNumber = blockHeight;
        if (prevHash == null){
            prevHash = new byte[32];
            Arrays.fill(prevHash, (byte) 0);
        }
        previousHash = Arrays.copyOf(prevHash, prevHash.length);
        initTransactions(publicKey, transactions);
        try{
            initMerkleRoot();
        } catch(IOException e) {
            e.printStackTrace();
        }

        this.timeStamp = timeStamp;
        ++blockHeight;
    }

    /**
     * function to initialize the merkle tree with the transactions
     */
    private void initMerkleRoot() throws IOException {
        //ensure that transactions are not 0
        assert transactions.size() <= 0;

        //get the transactions to be put into the block
        ArrayList<Transaction> txs = this.getTransactions();

        //build the merkle tree with our transactions
        MerkleTree tree = new MerkleTree(transactions);
        //tree.buildMerkleTree();

        //get the merkle root which is a merkle node
        MerkleNode root = tree.getMerkleRoot();
        setMerkleRoot(root.getData());
    }

    /**
     * Method to initial the transation set
     * @param publicKey the public key of the miner who solved this puzzle
     * @param txs the transaction list to include
     */
    private void initTransactions(PublicKey publicKey, List<Transaction> txs){

        ArrayList<Transaction> transactions = new ArrayList<>(txs);
        //Check
//        CoinbaseTransaction coinbaseTransaction = new CoinbaseTransaction(publicKey);
//        transactions.add(coinbaseTransaction);
        this.setTransactions(transactions);
    }

    /**
     * Method to hash the block
     * @throws IOException if the hashing alg doesn't exist
     */
    @Override
     public void hashObject() throws IOException {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(this.toByteArray());
            hash = md.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace(System.err);
        }
    }

    /**
     * Method to convert the block into a byte array for hashing
     * @return a byte array
     */
    @Override
    public byte[] toByteArray(){
        ArrayList<Byte> bytes = new ArrayList<>();

        assert previousHash != null;
        assert merkleRoot != null;

        //add previous hash to byte array
        for (Byte b: previousHash) {
            bytes.add(b);
        }

        //add merkle rood hash to byte array
        for (Byte b: merkleRoot){
            bytes.add(b);
        }

        //add block height to byte array
        ByteBuffer byteBuffer = ByteBuffer.allocate(Integer.SIZE / 8);
        byteBuffer.putInt(blockNumber);
        byte[] height = byteBuffer.array();

        for (Byte b: height){
            bytes.add(b);
        }

        byteBuffer.clear();
        byteBuffer = ByteBuffer.allocate(Long.SIZE / 8);
//        byteBuffer.putLong(nonce);
//        byte[] nonceData = byteBuffer.array();
//
//        for (Byte b: nonceData){
//            bytes.add(b);
//        }
//        byteBuffer.clear();

        byteBuffer.putLong(timeStamp);
        byte[] timestampData = byteBuffer.array();

        for (Byte b: timestampData){
            bytes.add(b);
        }

        byte[] hash = new byte[bytes.size()];

        for (int i = 0; i < bytes.size(); i++) {
            hash[i] = bytes.get(i);
        }

        return hash;

    }

    public static int getBlockHeight() {
        return blockHeight;
    }

//    public static void setBlockHeight(int blockHeight) {
//        Block.blockHeight = blockHeight;
//    }

    public byte[] getHash() {
        return hash;
    }

    public void setHash(byte[] hash) {
        this.hash = hash;
    }

    public byte[] getPreviousHash() {
        return previousHash;
    }

    public void setPreviousHash(byte[] previousHash) {
        this.previousHash = previousHash;
    }

    public byte[] getMerkleRoot() {
        return merkleRoot;
    }

    public void setMerkleRoot(byte[] merkleRoot) {
        this.merkleRoot = merkleRoot;
    }

//    public CoinbaseTransaction getCoinbaseTransaction() {
//        return coinbaseTransaction;
//    }
//
//    public void setCoinbaseTransaction(CoinbaseTransaction coinbaseTransaction) {
//        this.coinbaseTransaction = coinbaseTransaction;
//    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(ArrayList<Transaction> transactions) {
        this.transactions = transactions;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public long getNonce() {
        return nonce;
    }

    public void setNonce(long nonce) {
        this.nonce = nonce;
    }

    public void printBlock(){
        System.out.print("|----------------|");
        System.out.printf("\n| %s: %d |","Block Number", blockNumber);
        System.out.printf("\n| %s: %d |","Timestamp", timeStamp);
        System.out.printf("\n| %s: %d |","Nonce", nonce);
        System.out.printf("\n| %s: %s |","Block Hash", SHAUtils.bytesToHex(hash));
        System.out.printf("\n| %s %s |","Previous Hash", SHAUtils.bytesToHex(previousHash));
        System.out.printf("\n| %s %s |","Merkle Root", SHAUtils.bytesToHex(merkleRoot));
        System.out.print("\n|---------------|");
    }
}
