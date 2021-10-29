package com.uwgb.GBCoin.Model;

import com.uwgb.GBCoin.Utils.Crypto;
import net.bytebuddy.implementation.bytecode.Throw;

import java.security.PublicKey;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TransactionHandler {

    private UTXOPool utxoPool;
    private double transactionFees;

    public TransactionHandler(UTXOPool utxoPool){
        this.utxoPool = new UTXOPool(utxoPool);
        this.transactionFees = -1;
    }

    /**
     * main function to check if a given transaction is valid
     * @param transaction the transaction we want to check
     * @return true if the transaction passes all of our checks
     *  false otherwise if it fails the checks. The checks include: is a coin double spent,
     *  is a coin available to be spent, does a coin's signature match, and finally ensure that
     *  the transaction. Output sum is bigger than the transaction. Input sum
     */
    public boolean isValidTX(Transaction transaction){

        double inputSum = 0;
        double outputSum = 0;

        // get the set of all claimed utxo's
        Set<UTXO> claimedUTXO = new HashSet<>(utxoPool.getUTXOs());


        List<Transaction.Input> inputs = transaction.getInputs();
        int index = 0;

        //loop through all inputs apart of the transaction
        for (Transaction.Input input: inputs) {
            //check 1: to see if the transaction is a double spend
            if (!isCoinDoubleSpent(claimedUTXO, input)){
                return false;
            }

            //check 2: see if the transaction input is available to be spent
            if (!isConsumedCoinAvailable(input)){
                return false;
            }

            //check 3: to see if the signature verifies
            if (!isSignatureVerified(transaction, index, input)){
                return false;
            }

            UTXO utxo = new UTXO(input);
            Transaction.Output correspondingOutput = utxoPool.getTxOutput(utxo);
            inputSum += correspondingOutput.getValue();

            index++;
        }

        index = 0;
        List<Transaction.Output> outputs = transaction.getOutputs();
        for (Transaction.Output output: outputs) {
            if (output.getValue() <= 0){
                return false;
            }

            outputSum += output.getValue();
            index++;
        }

        //check 4: to see if the outputs are greater than the total number of inputs
        //if so, then this transaction
        if (outputSum > inputSum){
            return false;
        }

        return true;
    }

    /**
     * Method to get the transaction fees for all transactions in the list
     * the transaction fee is the output sum minus the input sum
     * which means the person who is paying i.e. the inputs
     * decided to spend more than the outputs thus whatever is left over is implicitly the fee.
     * @param transactions the list of transactions from which to calculate the total fee
     * @return the total transaction fee for all transactions as a double
     */
    public double getTransactionFees(List<Transaction> transactions){

        //create some values to hold the totals for the output sum and input sum
        double outputSum = 0;
        double inputSum = 0;

        //loop through each transaction in the list provided
        assert (transactions != null);

        for (Transaction t: transactions) {

            //first, ensure that the transaction is valid
            assert isValidTX(t);

            //now get all the outputs associated with this transaction
            List<Transaction.Output> outputs = t.getOutputs();

            //loop through all the outputs to get their output values
            for (Transaction.Output output: outputs) {
                outputSum+= output.getValue();
            }

            //now the same as the outputs, get all the input -> output values
            //for this we will need to get the output from the utxo set
            List<Transaction.Input> inputs = t.getInputs();

            for (Transaction.Input input: inputs){
                //create a new utxo with the input values
                UTXO utxo = new UTXO(input);

                //get the corresponding transaction output from the utxo pool
                Transaction.Output correspondingOutput = utxoPool.getTxOutput(utxo);
                inputSum+= correspondingOutput.getValue();
            }

        }

        //a double check (no pun intended) to ensure the output sum is > the input sum
        assert (outputSum > inputSum);

        //set the transaction fees variable for this class
        this.setTransactionFees(outputSum - inputSum);

        //not sure if this should be a setter or just a getter, so leaving as a getter for now
        return outputSum - inputSum;

    }


    /**
     * Method to check to see if a coin has been double spent
     * @param claimedUTXO a set of all claimed utxo's
     * @param input the corresponding input transaction that lines up with the utxo
     * @return false if the utxo based off of the input param is already in the set
     */
    private boolean isCoinDoubleSpent(Set<UTXO> claimedUTXO, Transaction.Input input){

        //create the utxo with the input that was provided
        UTXO utxo = new UTXO(input);

        //if the utxo is already in the set of all claimed utxo's, then this is a double spend
        return !claimedUTXO.add(utxo);

    }

    /**
     * Method to check if the signature of the transaction matches with the utxo
     * @param transaction the transaction to check
     * @param index the index of the input transaction
     * @param input the input transaction that needs to be verified against
     * @return true if the signatures match up
     */
    private boolean isSignatureVerified(Transaction transaction, int index, Transaction.Input input){
        UTXO utxo = new UTXO(input);
        Transaction.Output correspondingOutput = utxoPool.getTxOutput(utxo);
        PublicKey publicKey = correspondingOutput.getPublicKey();
        return Crypto.verifySignature(publicKey, transaction.getDataToSign(index), input.getSignature());
    }

    /**
     * Method to check if the consumed coin is available to spend
     * if so, then it will exist in the utxo pool of transactions
     * @param input the Transaction.Input that is available
     * @return true if the utxo pool contains this utxo transaction
     * false if it doesn't exist
     */
    private boolean isConsumedCoinAvailable(Transaction.Input input){
        UTXO utxo = new UTXO(input);
        return utxoPool.contains(utxo);
    }

    public double getTransactionFees() {
        return transactionFees;
    }

    public void setTransactionFees(double transactionFees) {
        this.transactionFees = transactionFees;
    }
}
