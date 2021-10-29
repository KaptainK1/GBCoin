package com.uwgb.GBCoin.Model;

import java.io.Serializable;

public class Block implements Serializable {

    //TODO need to implement this block class
    //create a dummy block for testing purposes. remove this line and below code once implemented
    public String name;
    public static int blockHeight = 0;

    public Block(String name){
        this.name = name;
        ++blockHeight;
    }
    //


}
