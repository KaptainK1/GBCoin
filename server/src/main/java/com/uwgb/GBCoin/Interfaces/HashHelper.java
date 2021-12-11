package com.uwgb.GBCoin.Interfaces;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public interface HashHelper {

    default byte[] toByteArray() throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ObjectOutputStream outputStream = new ObjectOutputStream(stream);
        outputStream.writeObject(this);
        outputStream.flush();
        return stream.toByteArray();
    }

    void hashObject() throws IOException;

}
