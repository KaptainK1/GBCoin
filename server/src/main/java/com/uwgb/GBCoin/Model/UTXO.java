package com.uwgb.GBCoin.Model;

import com.uwgb.GBCoin.Utils.SHAUtils;

import java.util.Arrays;

/**
 * The UTXO represents en unspent Transaction Output
 * meaning it is available to be spent
 */
public class UTXO implements Comparable<UTXO>{

    private byte[] txhash;

    private int index;

    public UTXO(byte[] txhash, int index){
        this.txhash = Arrays.copyOf(txhash, txhash.length);
        this.index = index;
    }


    /** simple comparison to see if two utxos are the exact same object
     * Simple
     * @param other the utxo object we want to compare
     * @return true if they are the same, else false
     */
    public boolean equals(Object other){

        if (other == null){
            return false;
        }

        if (getClass() != other.getClass()){
            return false;
        }

        UTXO utxo = (UTXO) other;

        if (this.getIndex() != utxo.getIndex()){
            return false;
        }

        for (int i = 0; i < utxo.getTxhash().length; i++) {
            if (this.getTxhash()[i] != utxo.getTxhash()[i]){
                return false;
            }
        }

        return true;

    }

    public UTXO(Transaction.Input input){
        assert (input != null);
        this.txhash = input.getPrevTxHash();
        this.index = input.getOutputIndex();
    }


    /**
     *
     * @param utxo UTXO object we want to compare
     * @return -1 if this utxo is less than the passed in utxo
     * 1 if if this utxo is greater than the passed in utxo
     * 0 if they are the same
     */
    //simple comparison between different utxos
    @Override
    public int compareTo(UTXO utxo) {
        if (this.getIndex() < utxo.getIndex()){
            return -1;
        } else if (this.getIndex() > utxo.getIndex()){
            return 1;
        } else {
            if (this.getTxhash().length < utxo.getTxhash().length){
                return -1;
            } else if (this.getTxhash().length > utxo.getTxhash().length){
                return 1;
            } else {
                for (int i = 0; i < this.getTxhash().length; i++) {
                    if (this.getTxhash()[i] < utxo.getTxhash()[i]){
                        return -1;
                    } else if (this.getTxhash()[i] > utxo.getTxhash()[i]){
                        return 1;
                    }
                }

                return 0;
            }
        }
    }


    //getters and setters

    /**
     *
     * @return the byte array of the transaction hash from which this utxo originates
     */
    public byte[] getTxhash() {
        return txhash;
    }

    /**
     *
     * @param txhash set the txhash to the given byte array
     */
    public void setTxhash(byte[] txhash) {
        this.txhash = txhash;
    }

    /**
     *
     * @return the index which represents an output index from a previous input
     */
    public int getIndex() {
        return index;
    }

    /**
     *
     * @param index set the index which represents an output index from a previous input
     */
    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();
        builder.append("\n");
        builder.append("Hash of this UTXO:    ");
        builder.append(SHAUtils.encodeBytes(getTxhash()));
        builder.append("Output Index of this UTXO:   ");
        builder.append(getIndex());
        builder.append("\n");
        return (builder.toString());
    }

}
