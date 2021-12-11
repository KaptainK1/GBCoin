package com.uwgb.GBCoin.Utils;

import java.util.Arrays;

public class ByteArrayWrapper {

    private byte[] data;

    public ByteArrayWrapper(byte[] bytes){
        data = Arrays.copyOf(bytes, bytes.length);
    }

    public boolean equals(Object other){
        if (other == null)
            return false;
        if (getClass() !=other.getClass())
            return false;

        ByteArrayWrapper otherByte = (ByteArrayWrapper) other;
        byte[] otherData = ((ByteArrayWrapper) other).data;

        if (data == null) {
            if (otherData == null){
                return true;
            }
            else {
                return false;
            }
        } else {
            if (otherData == null){
                return false;
            }
            else {
                if (data.length != otherData.length)
                    return false;

                for (int i = 0; i < data.length; i++) {
                    if (data[i] != otherData[i]) {
                        return false;
                    }

                }

                return true;
            }
        }
    }

    public int hashCode(){
        return Arrays.hashCode(data);
    }

}
