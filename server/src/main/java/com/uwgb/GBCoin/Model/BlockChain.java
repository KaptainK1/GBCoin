package com.uwgb.GBCoin.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//TODO need to implement this code
public class BlockChain implements Serializable {

    //TODO use the max height of a block chain to trim off older nodes
    //otherwise eventually we will run out of memory

    public final int MAX_HEIGHT = 30;
    private UTXOPool utxoPool;
    private List<Block> blockChain;

    public BlockChain(UTXOPool utxoPool){
        blockChain = new ArrayList<>();
        if (utxoPool == null){
            this.utxoPool = new UTXOPool();
        } else {
            this.utxoPool = utxoPool;
        }

    }

    public void addBlock(Block block){
        blockChain.add(block);
    }

    public void addUTXO(UTXO utxo, Transaction.Input input){
        Transaction.Output correspondingOutput = utxoPool.getTxOutput(utxo);
        this.utxoPool.addUTXO(utxo, correspondingOutput);
    }

    public void removeUTXO(UTXO utxo){
        this.utxoPool.removeUTXO(utxo);
    }

    public byte[] getCurrentHash(){
        Block block = this.blockChain.get(this.blockChain.size() -1);
        return block.getHash();
    }

    public int getBlockHeight(){
        return this.blockChain.size();
    }




}
