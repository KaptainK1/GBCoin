package com.uwgb.GBCoin.MerkleTree;

import com.uwgb.GBCoin.Model.Transaction;
import com.uwgb.GBCoin.Utils.SHAUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

/**
 * The Merkle Tree data structure which will consist of Merkle Nodes
 */
public class MerkleTree {

    public MerkleNode merkleRoot;
    private int size;
    private int levels;
    public String hashingAlgorithm = "SHA-256";
    private List<MerkleNode> leafs = new ArrayList<>();
    private ArrayList<MerkleNode> parents = new ArrayList<>();

    /**
     * Constructor to create a MerkleTree
     * @param unspentTransactions the unspent transactions that need to be added
     * @throws IOException if the hashing alg doesn't exist
     */
    public MerkleTree(ArrayList<Transaction> unspentTransactions) throws IOException {

        this.size = unspentTransactions.size();

        //calculate the number of levels needed for our tree1
        this.levels = (int) Math.ceil((Math.log10(size) / Math.log10(2)));

        if (this.levels == 0 ){
            this.levels = 1;
        }

        System.out.println("Levels: " + this.levels);

        //create all the leaf nodes
        for (Transaction unspentTransaction : unspentTransactions) {
            //create a byte buffer and allocate 32 bytes for the hash output of 256
            ByteBuffer b = ByteBuffer.allocate(32);

            //we need to covert the data into a byte array by using the byte buffer
            //b.put(unspentTransactions.get(i));
            //b.put(unspentTransaction.getBytes());
            //b.putChar(unspentTransaction.charAt(0));

            byte[] data = unspentTransaction.toByteArray();

            //add the leaf node
            leafs.add(new MerkleNode(SHAUtils.digest(data, hashingAlgorithm), null, null));

        }

        int numberOfDuplicateNodes = (int)(Math.pow(2,this.levels) - size);
        System.out.println("Number of duplicates: " + numberOfDuplicateNodes);

        //create some dummy nodes in order to create a full tree. we cant have a tree that does not equal
        //a power of two.
        for (int i = 0; i < numberOfDuplicateNodes; i++) {
            //TODO this will need to be updated once we have our Transaction Class, we should be hashing the entire object
//            leafs.add(new MerkleNode(leafs.get(i+size -1).getData(), leafs.get(i+size -1).getUnHashedData() ,null,null));
            leafs.add(new MerkleNode(leafs.get(i+size -1).getData() ,null,null));
        }

        if (this.size == 1){
            this.merkleRoot = new MerkleNode(unspentTransactions.get(0).getHash(), null, null);
        } else if (this.size < 3){
            byte[] combinedHash = getDataForHashing(unspentTransactions.get(0).getHash(), unspentTransactions.get(1).getHash());
            MerkleNode leftNode = new MerkleNode(unspentTransactions.get(0).getHash(), null, null);
            MerkleNode rightNode = new MerkleNode(unspentTransactions.get(1).getHash(), null, null);
            this.merkleRoot = new MerkleNode(combinedHash, leftNode, rightNode);
        } else {
            buildMerkleTree();
        }

    }

    /**
     * function to build the Merkle Tree from the bottom up
     */
    public void buildMerkleTree(){

        //function to create the immediate parents to our Transaction data blocks
        createDirectParents();

//        System.out.println("Levels: " + this.levels);
//        System.out.println("Size: " + this.size);

        //use variable k to hold our position in the parent's array
        //we increment it by levels of two since each parent only has 2 children
        int k = 0;

        //Since we build our direct parent nodes in another function,
        //our first two bottom levels are built, so we can start at the third level
        //since this is an inverted binary tree, start at the # of levels -2 then decrement it
        for (int i = this.levels - 2; i > 0; i--) {

            //Here we want to loop as many times as there are nodes at the ith level
            // which we can calculate with Math.pow(2,i)
            for (int j = 0; j < (int) Math.pow(2,i); j++) {

//                System.out.println(Math.pow(2,i));
//                System.out.println("i is" + i);
//                System.out.println("j is" + j);

                //combine the hash of the parent's 2 children
                //TODO this will need to be updated once we have our Transaction Class, we should be hashing the entire object

                byte[] combinedHash = getDataForHashing(parents.get(k).getData(), parents.get(k+1).getData());
//                byte[] combinedHash = getHash((parents.get(k).getUnHashedData() + parents.get(k+1).getUnHashedData()));
//                byte[] combinedHash = SHAUtils.concatenateHash(parents.get(k).getUnHashedData().getBytes(StandardCharsets.UTF_8),
//                                                                parents.get(k+1).getUnHashedData().getBytes(StandardCharsets.UTF_8),
//                                                                hashingAlgorithm);

                //now create the new node and add it to our parents arraylist
//                MerkleNode node = new MerkleNode(combinedHash,parents.get(k).getUnHashedData() + parents.get(k+1).getUnHashedData(),
//                                                    parents.get(k),
//                                                    parents.get(k+1));
                MerkleNode node = new MerkleNode(combinedHash, parents.get(k), parents.get(k+1));

                parents.add(node);
//                System.out.println(node);

                k+=2;

            }

        }

        //set the merkle root to the last element in the parents array list
        //TODO this will need to be updated once we have our Transaction Class, we should be hashing the entire object

        byte[] combinedHash = getDataForHashing(parents.get(parents.size() -2).getData(), parents.get(parents.size() -1).getData());
//        byte[] combinedHash = getHash((parents.get(parents.size() -2).getUnHashedData() + parents.get(parents.size() -1).getUnHashedData()));
//        byte[] combinedHash = SHAUtils.concatenateHash(parents.get(parents.size() -2).getUnHashedData().getBytes(StandardCharsets.UTF_8),
//                parents.get(parents.size() -1).getUnHashedData().getBytes(StandardCharsets.UTF_8),
//                hashingAlgorithm);

        this.merkleRoot = new MerkleNode(combinedHash, parents.get(parents.size() -2), parents.get(parents.size() -1));

    }

    private byte[] getDataForHashing(byte[] leftHash, byte[] rightHash){
        byte[] newByte = new byte[leftHash.length + rightHash.length];
        ArrayList<Byte> combinedBytes = new ArrayList<>(leftHash.length + rightHash.length);

        for (Byte b : leftHash) {
            combinedBytes.add(b);
        }

        for (Byte b : rightHash){
            combinedBytes.add(b);
        }

        for (int i = 0; i < combinedBytes.size(); i++) {
            newByte[i] = combinedBytes.get(i);
        }

        String encodedString = Base64.getEncoder().encodeToString(newByte);


       return getHash(encodedString);
    }

    public void printMerkleTree(){

        //loop through each of the leaf nodes to print them out
        //this is our data nodes, in the future these will be our
        //transactions. For now they are just integers

        printInOrder(this.merkleRoot);

    }

    /**
     *  simple recursive print method for printing the merkle tree
     *  start at the furthest left node, print it, then print its parent
     *  then print the parent's right child and percolate up the tree
     * @param node the node we want to print
     */

    public void printInOrder(MerkleNode node){
        if (node == null)
            return;

        printInOrder(node.getLeft());

        //System.out.println(node.getUnHashedData());
        System.out.println(node);

        printInOrder(node.getRight());
    }

    /**
     * function to create the direct parents of the Transaction (i.e data blocks)
     */
    private void createDirectParents(){

        for (int i = 0; i < leafs.size() ; i+=2) {

            //TODO this will need to be updated once we have our Transaction Class, we should be hashing the entire object
//            byte[] combinedHash = getHash((leafs.get(i).getData() + leafs.get(i+1).getData()));
            byte[] combinedHash = getDataForHashing(leafs.get(i).getData(), leafs.get(i+1).getData());

//            byte[] combinedHash = SHAUtils.concatenateHash(leafs.get(i).getData(),
//                                                            leafs.get(i+1).getData(),
//                                                            hashingAlgorithm);
            MerkleNode directParent = new MerkleNode(combinedHash,
                                                        leafs.get(i),
                                                        leafs.get(i+1));
            parents.add(directParent);
        }

    }

    private byte[] getHash(String data){
        return SHAUtils.digest(data.getBytes(StandardCharsets.UTF_8), hashingAlgorithm);
    }

    public MerkleNode getMerkleRoot() {
        return merkleRoot;
    }

    public void setMerkleRoot(MerkleNode merkleRoot) {
        this.merkleRoot = merkleRoot;
    }
/*
    public static void main(String[] args) throws IOException {
        String[] strings = new String[]{"A", "B", "C", "D", "E", "F", "G", "H"};
        ArrayList<String> test = new ArrayList<String>(
                Arrays.asList(strings)
                );

        Transaction tx1 = new Transaction("Test1".getBytes(StandardCharsets.UTF_8), 2.00);
        Transaction tx2 = new Transaction("Test2", 3.00);
        Transaction tx3 = new Transaction("Test3", 4.00);
        Transaction tx4 = new Transaction("Test4", 5.00);
        Transaction tx5 = new Transaction("Test5", 6.00);
        Transaction tx6 = new Transaction("Test6", 7.00);

        ArrayList<Transaction> txs = new ArrayList<>(
                Arrays.asList(tx1, tx2, tx3, tx4, tx5, tx6)
        );

        System.out.println(test);

        MerkleTree tree = new MerkleTree(txs);

        tree.printMerkleTree();

        System.out.println(tree.merkleRoot.getUnHashedData());
        System.out.println(tree.merkleRoot);
    }
*/

}
