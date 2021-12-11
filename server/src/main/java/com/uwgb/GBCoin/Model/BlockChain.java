package com.uwgb.GBCoin.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The BlockChain class is a list of Blocks linked together with has pointers
 */
public class BlockChain implements Serializable {

    //TODO use the max height of a block chain to trim off older nodes
    //otherwise eventually we will run out of memory

    public final int MAX_HEIGHT = 30;
//    private UTXOPool utxoPool;
    private List<Block> blockChain;

    /**
     * Simple constructor which takes no args and makes an empty blockchain
     */
    public BlockChain(){
        blockChain = new ArrayList<>();
//        this.utxoPool = utxoPool;
    }

    /**
     * initialize a blockchain class provided a block chain
     * we want to copy their blocks to our new chain
     * @param chain the list of blocks
     */
    public BlockChain(BlockChain chain){
        this.blockChain = new ArrayList<>(chain.blockChain);
//        this.utxoPool = chain.utxoPool;
    }

    /**
     * Method to add a new block to the chain
     * @param block
     */
    public void addBlock(Block block){
        blockChain.add(block);
    }

    /**
     * Method to get the current hash of the leading block
     * @return the byte hash of the leading block
     */
    public byte[] getCurrentHash(){
        Block block = this.blockChain.get(this.blockChain.size() -1);
        return block.getHash();
    }

    /**
     * Method to get the current number of blocks
     * @return the current number of blocks
     */
    public int getBlockHeight(){
        return this.blockChain.size();
    }

    /**
     * Method to print each block in the chain in order from beginning to end
     * @param chain the chain we need to print
     */
    public static void printBlockChain(BlockChain chain){
        for (int i = 0; i < chain.blockChain.size(); i++) {
            chain.blockChain.get(i).printBlock();
        }
    }

}
