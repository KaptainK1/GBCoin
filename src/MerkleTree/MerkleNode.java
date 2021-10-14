package MerkleTree;

import Utils.SHAUtils;

import java.lang.reflect.Array;
import java.util.Arrays;

public class MerkleNode {

    private byte[] data;



    private String unHashedData;
    private MerkleNode left;
    private MerkleNode right;

    public MerkleNode(byte[] data, String unHashedData, MerkleNode left, MerkleNode right){
        //clone the data (ie data to be hashed)
        this.data = data.clone();
        this.unHashedData = unHashedData;
        this.left = left;
        this.right = right;
    }

    public MerkleNode(){
        this.data = null;
        this.unHashedData = null;
        this.left = null;
        this.right = null;
    }

//getters and setters
    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data.clone();
    }

    public MerkleNode getLeft() {
        return left;
    }

    public void setLeft(MerkleNode left) {
        this.left = left;
    }

    public MerkleNode getRight() {
        return right;
    }

    public void setRight(MerkleNode right) {
        this.right = right;
    }

    public String getUnHashedData() {
        return unHashedData;
    }

    public void setUnHashedData(String unHashedData) {
        this.unHashedData = unHashedData;
    }

    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();
        builder.append("-Data---");
        builder.append(this.getUnHashedData());
        builder.append('\n');
        builder.append(SHAUtils.bytesToHex(this.getData()));
        builder.append("---");
        if (this.left != null){
            builder.append(this.left.getUnHashedData());
        } else {
            builder.append("Left is null ");
        }
        if (this.right != null){
            builder.append(this.right.getUnHashedData());
        } else {
            builder.append("Right is null ");
        }

        return builder.toString();
    }

}
