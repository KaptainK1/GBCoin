package com.uwgb.GBCoin.Model;

import java.io.Serializable;
import java.util.List;

//TODO need to implement this code
public class BlockChain implements Serializable {

    //TODO use the max height of a block chain to trim off older nodes
    //otherwise eventually we will run out of memory

    public final int MAX_HEIGHT = 30;
    private UTXOPool utxoPool;
    private List<Block> blockChain;

    public BlockChain(){

    }




}
