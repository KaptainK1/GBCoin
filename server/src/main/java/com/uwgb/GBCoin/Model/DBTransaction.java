package com.uwgb.GBCoin.Model;

import javax.persistence.*;
import java.util.Arrays;

@Entity
@Table(name = "Pending_Transactions")
public class DBTransaction {

    @javax.persistence.Id
    @SequenceGenerator(
            name = "tx_sequence",
            sequenceName = "tx_sequence",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.IDENTITY,
            generator = "tx_sequence")
    private long Id;
    private byte[] privateKey;
    private byte[] publicKey;
    private double amount;
    private double transactionFee;

    public DBTransaction(long id, byte[] privateKey, byte[] publicKey, double amount, double transactionFee) {
        Id = id;
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        this.amount = amount;
        this.transactionFee = transactionFee;
    }

    public DBTransaction(byte[] privateKey, byte[] publicKey, double amount, double transactionFee) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
        this.amount = amount;
        this.transactionFee = transactionFee;
    }

    public DBTransaction() {
    }

    public long getId() {
        return Id;
    }

    public void setId(long id) {
        Id = id;
    }

    public byte[] getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(byte[] privateKey) {
        this.privateKey = privateKey;
    }

    public byte[] getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(byte[] publicKey) {
        this.publicKey = publicKey;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getTransactionFee() {
        return transactionFee;
    }

    public void setTransactionFee(double transactionFee) {
        this.transactionFee = transactionFee;
    }

    @Override
    public String toString() {
        return "DBTransaction{" +
                "Id=" + Id +
                ", privateKey=" + Arrays.toString(privateKey) +
                ", publicKey=" + Arrays.toString(publicKey) +
                ", amount=" + amount +
                ", transactionFee=" + transactionFee +
                '}';
    }
}
