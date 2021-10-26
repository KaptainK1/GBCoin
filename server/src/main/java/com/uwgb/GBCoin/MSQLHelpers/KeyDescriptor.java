package com.uwgb.GBCoin.MSQLHelpers;

import com.mysql.cj.util.Base64Decoder;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;

import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;
import org.hibernate.type.descriptor.java.ImmutableMutabilityPlan;
import org.hibernate.type.descriptor.spi.JdbcRecommendedSqlTypeMappingContext;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAKeyGenParameterSpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Properties;

public class KeyDescriptor extends AbstractTypeDescriptor<java.security.Key> {

    protected KeyDescriptor() {
        super(java.security.Key.class, new ImmutableMutabilityPlan<>());
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
    public <X> java.security.Key wrap(X value, WrapperOptions options) {

        System.out.println(value);
        Blob blob = (Blob) value;
        byte[] bytes = new byte[0];
        byte[] decodedKey = new byte[0];
        
        try {
            bytes = blob.getBytes(1, (int) blob.length());
            decodedKey = Base64Decoder.decode(bytes, 0, bytes.length);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        
        PublicKey key = null;
        RSAPublicKey rsaPubKey = null;

        try {
            assert keyFactory != null;
            rsaPubKey = (RSAPublicKey )keyFactory.generatePublic( new X509EncodedKeySpec(decodedKey));
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }

        return rsaPubKey;
    }

    @Override
    public SqlTypeDescriptor getJdbcRecommendedSqlType(JdbcRecommendedSqlTypeMappingContext context) {
        return null;
    }


}
