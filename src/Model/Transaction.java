package Model;

import Interfaces.Beautify;
import Interfaces.HashHelper;

import java.io.IOException;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.ArrayList;

public class Transaction implements Serializable, HashHelper, Beautify {

        // The Inner Output class represents outgoing spend transactions
        // so, we need the Public Key of whom the payment should go to
        // and the amount in GBCoins
        public class Output implements Serializable, HashHelper{

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
        }

        public class Input implements Serializable, HashHelper{

            private byte[] prevOutputHash;
            private int outputIndex;
            private byte[] signature;

            public Input(byte[] prevOutputHash, int outputIndex, byte[] signature){
                this.setSignature(signature);
                this.setOutputIndex(outputIndex);
                this.setPrevOutputHash(prevOutputHash);
            }

            public Input(byte[] prevOutputHash, int outputIndex){
                this(prevOutputHash, outputIndex, null);
            }

            public void addSignature(byte[] signature){
                setSignature(signature);
            }

            /**
             *
             * @return
             */
            public byte[] getPrevOutputHash() {
                return prevOutputHash;
            }

            public void setPrevOutputHash(byte[] prevOutputHash) {
                this.prevOutputHash = prevOutputHash.clone();
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


    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private double amount;
    private String title;

    public Transaction(String title, double amount){
        this.amount = amount;
        this.title = title;
    }

    @Override
    public String toString(){
        //return ("Transaction:" + title + '\n' + "Amount: $" + this.amount);
        return this.beautify(this);
    }

    public void finalize() {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(this.toByteArray());
            hash = md.digest();
        } catch (NoSuchAlgorithmException | IOException x) {
            x.printStackTrace(System.err);
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
}
