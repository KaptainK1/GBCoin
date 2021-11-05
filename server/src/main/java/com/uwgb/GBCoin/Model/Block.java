package com.uwgb.GBCoin.Model;

import com.uwgb.GBCoin.Interfaces.HashHelper;

import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

public class Block implements HashHelper {
    public static int blockHeight = 0;
    private byte[] hash;
    private byte[] previousHash;
    private byte[] merkleRoot;
    private CoinbaseTransaction coinbaseTransaction;
    private List<Transaction> transactions;
    private long timeStamp;
    private long nonce;

    //TODO need to implement this block class
    //create a dummy block for testing purposes. remove this line and below code once implemented
    public String name;
    //public static int blockHeight = 0;

    public Block(String name){
        this.name = name;
        ++blockHeight;
    }
    //

    public Block(PublicKey publicKey) {

    }

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
        byteBuffer.putInt(blockHeight);
        byte[] height = byteBuffer.array();

        for (Byte b: height){
            bytes.add(b);
        }

        byteBuffer.clear();
        byteBuffer = ByteBuffer.allocate(Long.SIZE / 8);
        byteBuffer.putLong(nonce);
        byte[] nonceData = byteBuffer.array();

        for (Byte b: nonceData){
            bytes.add(b);
        }

        byteBuffer.clear();
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

}
