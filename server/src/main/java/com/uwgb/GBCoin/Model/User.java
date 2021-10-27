package com.uwgb.GBCoin.Model;

import com.mysql.cj.util.Base64Decoder;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

import javax.persistence.*;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLException;

@Entity
@Table(name = "Users")
public class User {
    @Id
    @SequenceGenerator(
            name = "user_sequence",
            sequenceName = "user_sequence",
            allocationSize = 1
    )
    @GeneratedValue(strategy = GenerationType.IDENTITY,
                    generator = "user_sequence")
    private Long id;
    private String userName;
    private String publicKey;

    //TODO maybe make this a hash of the private key
    private String privateKey;

    public User(){
    }

    public User(String userName, String publicKey, String privateKey) {
        this.userName = userName;
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    public User(Long id, String userName, String publicKey, String privateKey) {
        this.id = id;
        this.userName = userName;
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    public PublicKey getPublicKeyAsKey(){
        if (this.getPublicKey() == null) {
            return null;
        } else {

            byte[] bytes = new byte[0];
            byte[] decodedKey = new byte[0];
            PublicKey publicKey = null;

            try {
                bytes = this.getPublicKey().getBytes(this.getPublicKey());
                decodedKey = Base64Decoder.decode(bytes, 0, bytes.length);
            } catch (UnsupportedEncodingException e) {
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
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", publicKey='" + publicKey + '\'' +
                ", privateKey='" + privateKey + '\'' +
                '}';
    }
}
