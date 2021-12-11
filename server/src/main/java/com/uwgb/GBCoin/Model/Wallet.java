package com.uwgb.GBCoin.Model;

import org.hibernate.annotations.Type;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.security.*;
import java.security.spec.RSAKeyGenParameterSpec;
import java.util.ArrayList;
import java.util.Collections;

//@Entity
//@Table(name = "wallets")
/**
 * The wallet class will hold both private and public keys
 * the private key needs to be kept safe, as it is used to sign transactions
 * the public key can be distributed as it is the form of address
 */
public class Wallet {

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private long ID;
//
//    @Lob
    private final PublicKey publicKey;
//
//    @Lob
    private final PrivateKey privateKey;

    public Wallet() throws Exception {

        //generate keyPair
        KeyPair keyPair;
        try {

            //create an instance of the ken pair generator using RSA
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");

            //create a new secure random with the SHA1PRNG algorithm provided by sun
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");

            //Use a RSA spec since we are using RSA for key generations
            RSAKeyGenParameterSpec rsaKeyGenParameterSpec = new RSAKeyGenParameterSpec(2048, RSAKeyGenParameterSpec.F4);

            //initialize our key pair generator and create the key pair
            keyGen.initialize(rsaKeyGenParameterSpec, random);
            keyPair = keyGen.genKeyPair();

            //set the private and public keys from the key pair
            this.privateKey = keyPair.getPrivate();
            this.publicKey = keyPair.getPublic();

        } catch(NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new Exception("Error with generating key");
        }

    }

    /**
     * Get the public key from when the wallet object was instantiated
     * @return return the public key
     */
    public PublicKey getPublicKey() {
        return publicKey;
    }

    /**
     * Get the private key from when the wallet object was instantiated
     * @return return the private key
     */
    public PrivateKey getPrivateKey() {
        return privateKey;
    }
}
