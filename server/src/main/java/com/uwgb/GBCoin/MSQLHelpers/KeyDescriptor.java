package com.uwgb.GBCoin.MSQLHelpers;

import com.mysql.cj.util.Base64Decoder;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;

import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;
import org.hibernate.type.descriptor.java.ImmutableMutabilityPlan;
import org.hibernate.type.descriptor.spi.JdbcRecommendedSqlTypeMappingContext;

import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.Key;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.sql.Blob;
import java.sql.SQLException;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import static java.lang.Class.forName;


public class KeyDescriptor extends AbstractTypeDescriptor<java.security.Key> {

    protected KeyDescriptor() {
        super(java.security.Key.class, new ImmutableMutabilityPlan<>());
        Security.addProvider(new BouncyCastleProvider());
    }

    @Override
    public String toString(java.security.Key value) {
        return new String(value.getEncoded());
    }

    @Override
    public java.security.Key fromString(String string) {

        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        PublicKey key = null;
        try {
            assert keyFactory != null;
            key = keyFactory.generatePublic( new X509EncodedKeySpec(string.getBytes(StandardCharsets.UTF_8)));
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }

        return key;
    }

    @Override
    public <X> X unwrap(java.security.Key value, Class<X> type, WrapperOptions options) {
        if (value == null){
            return null;
        }
        if (String.class.isAssignableFrom(type)){
            return (X) String.valueOf(value);
        }

        throw unknownUnwrap(type);
    }

    @Override
    public <X> Key wrap(X value, WrapperOptions options) {

        Blob blob = (Blob) value;
        byte[] bytes = new byte[0];
        byte[] decodedKey = new byte[0];
        PublicKey publicKey = null;
        
        try {
            bytes = blob.getBytes(1, (int) blob.length());
            decodedKey = Base64Decoder.decode(bytes, 0, bytes.length);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String keyAsString = new String(decodedKey, StandardCharsets.UTF_8);

        try (
            Reader reader = new StringReader(keyAsString);
            PemReader pemReader = new PemReader(reader);
        ) {
            KeyFactory fact = KeyFactory.getInstance("RSA");
            PemObject pemObject = pemReader.readPemObject();
            byte[] keyContentAsBytesFromBC = pemObject.getContent();
            X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(keyContentAsBytesFromBC);
            publicKey = fact.generatePublic(pubKeySpec);

//            byte[] encodedKey = publicKey.getEncoded();
//            String base64Key = Base64.getEncoder().encodeToString(encodedKey);

            String encodedKey = new String(bytes);

            System.out.println(encodedKey);
            //return publicKey;
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return publicKey;
    }

    @Override
    public SqlTypeDescriptor getJdbcRecommendedSqlType(JdbcRecommendedSqlTypeMappingContext context) {
        return null;
    }


}
