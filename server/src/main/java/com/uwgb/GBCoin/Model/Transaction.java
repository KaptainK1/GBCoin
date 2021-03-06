package com.uwgb.GBCoin.Model;

import com.uwgb.GBCoin.Interfaces.HashHelper;
import com.uwgb.GBCoin.Utils.Crypto;
import com.uwgb.GBCoin.Utils.SHAUtils;
//import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * The Transaction class represents a GB Coin transaction
 * A transaction features a list of Inputs, Outputs, and its own hash
 */
public class Transaction implements HashHelper {

        // The Inner Output class represents outgoing spend transactions
        // so, we need the Public Key of whom the payment should go to
        // and the amount in GBCoins
        public class Output implements Comparable<Transaction.Output>{

            private PublicKey publicKey;
            private double value;

            /**
             *
             * @param publicKey the public key represents the address of whom the payment should go to
             * @param value is the value in GBCoins of the transaction
             */
                public Output(double value, PublicKey publicKey){
                    this.publicKey = publicKey;
                    this.value = value;
                }

                @Override
                public String toString(){
                    StringBuilder builder = new StringBuilder();
                    builder.append("----Value of this output----");
                    builder.append("\n");
                    builder.append(this.value);
                    builder.append("\n");
                    builder.append("----Public Key of the recipient----");
                    builder.append("\n");
                    builder.append(Crypto.getStringFromKey(this.publicKey));
                    builder.append("\n");
                    return builder.toString();
                }

            public PublicKey getPublicKey() {
                return publicKey;
            }

            public void setPublicKey(PublicKey publicKey) {
                this.publicKey = publicKey;
            }

            public double getValue() {
                return value;
            }

            public void setValue(double value) {
                this.value = value;
            }

            @Override
            public int compareTo(Transaction.Output o) {
                    if (this.value < o.getValue()){
                        return -1;
                    } else if (this.value > o.getValue()){
                        return 1;
                    } else {
                        return 0;
                    }
            }

            public boolean equals(Object other){
                if (other == null){
                    return false;
                }

                if (getClass() != other.getClass()){
                    return false;
                }

                Transaction.Output output = (Transaction.Output)other;

                if (this.value != output.value){
                    return false;
                }

                if (this.getPublicKey() != output.getPublicKey()){
                    return false;
                }

                return true;
            }
        }

        public class Input {

            private byte[] prevTxHash;
            private int outputIndex;
            private byte[] signature;

            public Input(byte[] prevTxHash, int outputIndex, byte[] signature){
                this.setSignature(signature);
                this.setOutputIndex(outputIndex);
                this.setPrevTxHash(prevTxHash);
            }

            public Input(byte[] prevTxHash, int outputIndex){
                this(prevTxHash, outputIndex, null);
            }

            public void addSignature(byte[] signature){
                if (signature == null){
                    this.signature = null;
                } else {
                    this.signature = Arrays.copyOf(signature, signature.length);
                }
            }

            /**
             *
             * @return
             */
            public byte[] getPrevTxHash() {
                return prevTxHash;
            }

            public void setPrevTxHash(byte[] prevTxHash) {
                this.prevTxHash = prevTxHash.clone();
            }

            public int getOutputIndex() {
                return outputIndex;
            }

            public void setOutputIndex(int outputIndex) {
                this.outputIndex = outputIndex;
            }

            public byte[] getSignature() {
                return signature;
            }

            public void setSignature(byte[] signature) {
                this.signature = signature;
            }

        }

    private byte[] hash;
    private ArrayList<Input> inputs;
    private ArrayList<Output> outputs;

    public Transaction(){
        setInputs(new ArrayList<Input>());
        setOutputs(new ArrayList<Output>());
    }

    //Constructor to make a new transaction based off a previous one
    public Transaction(Transaction tx){
        hash = tx.hash.clone();
        this.setInputs(new ArrayList<Input>(tx.inputs));
        this.setOutputs(new ArrayList<Output>(tx.outputs));
    }
    
    /**
     * add a signature to the input
     * @param signature a valid signature that is not null
     * @param index the index of the input
     */
    public void addSignature(byte[] signature, int index){
        inputs.get(index).addSignature(signature);
    }

    /**
     *
     * @param prevTxHash the hash of the previous transaction
     * @param outputIndex the output index from which we derive our amount from
     */
    public void addInput(byte[] prevTxHash, int outputIndex){
        Input input = new Input(prevTxHash, outputIndex);
        this.getInputs().add(input);
    }

    /**
     * Method to add a new output to our list of outputs
     * @param value the value of the GBcoin transaction
     * @param address the address of whom it should go to
     */
    public void addOutput(double value, PublicKey address){
        Output output = new Output(value, address);
        this.getOutputs().add(output);
    }

    public void removeInput(int index){
        this.getInputs().remove(index);
    }

    public byte[] getDataToSign(int inputIndex){
        // get the specified input and all of its outputs
        ArrayList<Byte> signatureData = new ArrayList<>();

        //check to see if the input index is higher than our inputs size
        // if so, then the index doesn't exist, so return null
        if (inputIndex > this.getInputs().size()){
            return null;
        }
        //create a new input object and set it to the specified index
        Input input = this.getInputs().get(inputIndex);

        //get the hash of the input data
        byte[] hash = input.getPrevTxHash();

        //create  a new byte buffer and add our index
        ByteBuffer byteBuffer = ByteBuffer.allocate(Integer.SIZE / 8);
        byteBuffer.putInt(input.outputIndex);

        //create an array of the integer as a byte array
        byte[] outputIndex = byteBuffer.array();

        //add all the hash as bytes into our main rain
        if (hash != null){
            for (Byte b: hash) {
                signatureData.add(b);
            }
        }

        //add all the bytes of the output index into our main array
        for (Byte b: outputIndex) {
            signatureData.add(b);
        }

        //loop through all the outputs to get their byte data
        //which includes the value as well as the output addresses (public key)
        for (Output op : this.getOutputs()){
            ByteBuffer outputBuffer = ByteBuffer.allocate(Double.SIZE / 8);
            outputBuffer.putDouble(op.value);
            byte[] value = outputBuffer.array();

            for (Byte b: value) {
                signatureData.add(b);
            }

            byte[] address = op.publicKey.getEncoded();
            for (Byte b: address) {
                signatureData.add(b);
            }
        }

        //create a new byte array to be returned
        byte[] returnedSignatureData = new byte[signatureData.size()];
        int i = 0;
        for (Byte b : signatureData){
            returnedSignatureData[i] = b;
            i++;
        }

        return returnedSignatureData;
    }

    /**
     * Override the HashHelper's toByteArray method
     * because we do not want to create a byte representation of this entire object
     * as it includes a private key, which is a big no-no
     * @return a byte representation of this entire object, minus the private key
     */
    @Override
    public byte[] toByteArray(){
        // create the byte arraylist
        ArrayList<Byte> transactionData = new ArrayList<>();

        ByteBuffer inputBuffer = ByteBuffer.allocate(Integer.SIZE / 8);
        ByteBuffer outputBuffer = ByteBuffer.allocate(Double.SIZE / 8);

        //loop through all the inputs and get their byte data
        for (Input input : this.getInputs()){
            inputBuffer.putInt(input.getOutputIndex());
            byte[] outputIndex = inputBuffer.array();
            byte[] hash = input.getPrevTxHash();

            for (Byte b: outputIndex) {
                transactionData.add(b);
            }

            for (Byte b: hash) {
                transactionData.add(b);
            }

            inputBuffer.clear();
        }

        //loop through all the outputs and get their byte data
        for (Output output: this.getOutputs()) {
            outputBuffer.putDouble(output.value);
            byte[] value = outputBuffer.array();

            for (Byte b: value) {
                transactionData.add(b);
            }

            byte[] address = output.publicKey.getEncoded();

            for (Byte b: address) {
                transactionData.add(b);
            }

            outputBuffer.clear();
        }

        //create a new byte array to be returned
        byte[] returnedSignatureData = new byte[transactionData.size()];
        int i = 0;
        for (Byte b : transactionData){
            returnedSignatureData[i] = b;
            i++;
        }

        return returnedSignatureData;

    }

//    @Override
//    public String toString(){
//        //return ("Transaction:" + title + '\n' + "Amount: $" + this.amount);
//        return this.beautify(this);
//    }

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

    public byte[] getHash()  {
        return this.hash;
    }

    public void setHash(byte[] hash) {
        this.hash = hash;
    }

    public ArrayList<Input> getInputs() {
        return inputs;
    }

    public void setInputs(ArrayList<Input> inputs) {
        this.inputs = inputs;
    }

    public ArrayList<Output> getOutputs() {
        return outputs;
    }

    public void setOutputs(ArrayList<Output> outputs) {
        this.outputs = outputs;
    }

    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();
        for (Input input: getInputs()) {
            builder.append("Previous Transaction Hash: ");
            builder.append(SHAUtils.encodeBytes(input.getPrevTxHash()));
            builder.append("\n");
            builder.append("Previous Output Index: ");
            builder.append(input.getOutputIndex());
            builder.append("\n");
        }
        for (Output output: getOutputs()){
            builder.append("Receiver of this output tx: ");
            builder.append(SHAUtils.encodeBytes(output.getPublicKey().getEncoded()));
            builder.append("\n");
            builder.append("Output value: \u20BF");
            builder.append(output.getValue());
            builder.append("\n");
        }
        if (this.hash != null) {
            builder.append("Hash of this Transaction: ");
            builder.append(SHAUtils.encodeBytes(this.hash));
            builder.append("\n");
        }
        return builder.toString();
    }
}
