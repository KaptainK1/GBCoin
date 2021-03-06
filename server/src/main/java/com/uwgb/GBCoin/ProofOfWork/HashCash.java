package com.uwgb.GBCoin.ProofOfWork;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import com.uwgb.GBCoin.Miner.MinerNetwork;
import com.uwgb.GBCoin.Utils.SHAUtils;

/**
 * HashCash will be our Proof-of-Work concept which will offer a challenge to the miner to solve
 * once the challenge is solved, the miner will propose the next block and transactions up for consensus
 * This concept was created by Adam Back
 * http://www.hashcash.org/
 */
public class HashCash {

    public static final char[] randomCharacters = {
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'
    };

    public static final String hashAlgorithm = "SHA-256";

    private long nonce;
    private int difficulty;
    private int version;
    private String resource;
    private String challenge;
    private long timeStamp;
//    private String randomString;
    private boolean isSolvedByOtherMiner = false;
    private boolean isSolved = false;

    /**
     * @param difficulty [int] difficulty of the puzzle, each increase doubles the difficulty i.e 2^n
     * @param version [int] version of HashCash
     * @param resource [string] unique identifier, for Bitcoin it should be publicKey
     */
    public HashCash(int version, int difficulty, String resource, long timeStamp){

        this.setDifficulty(difficulty);
        this.setVersion(version);
        this.setResource(resource);
        this.setTimeStamp(timeStamp);
//        this.setRandomString(this.generateString(12));

        //now build our string
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.getVersion());
        stringBuilder.append(':');
        stringBuilder.append(this.getDifficulty());
        stringBuilder.append(':');
//        stringBuilder.append(this.getTimeStamp());
//        stringBuilder.append(':');
        stringBuilder.append(this.getResource());
        stringBuilder.append(':');
//        stringBuilder.append(this.getRandomString());
//        stringBuilder.append(':');

        setChallenge(stringBuilder.toString());
        System.out.println(getChallenge());

    }

    //overloaded constructors
    public HashCash(String resource, long timeStamp){
        this(1,8, resource, timeStamp);
    }

    public HashCash(String resource){
        this(1,8, resource, System.currentTimeMillis());
    }

    public HashCash (int version, String resource, long timeStamp){
        this(version, 8, resource, timeStamp);
    }

    /**
     *
     * @param size the amount osf characters we want the random seed string to be
     * @return //create a new random string and return the Base64 encoded version of it
     */

    String generateString(int size){

        char[] characters = new char[size];

        for (int  i = 0; i < size ; i++) {
            Random random = new Random();
            // get a random character and put it into our character array
            characters[i] = randomCharacters[random.nextInt(randomCharacters.length -1)];
        }

        System.out.println(characters);

        return generateBase64EncodedString(new String(characters));
    }

    /**
     *
     * @param string string to be encoded in Base64
     *      Using charset UTF_*
     * @return the encoded string as Base64
     */
    public static String generateBase64EncodedString(String string){
        return Base64.getEncoder().encodeToString(string.getBytes(StandardCharsets.UTF_8));
    }

    public void mine(){

        //get number of bytes we need to check
        int bytes = this.getDifficulty() / 8;
        System.out.println(bytes);
//        boolean isSolved = false;

        //since we are not guaranteed to get a perfect number of bytes
        //we need to check any remaining bits
        int bits = this.getDifficulty() % 8;
        System.out.println(bits);

        setNonce(1);


        while(!isSolved && !isSolvedByOtherMiner){
            //allocate 32 bytes to hold the output of a sha-256 hash
            ByteBuffer buffer = ByteBuffer.allocate(32);

            //encode our nonce with Base64
            String nonceEncoded = generateBase64EncodedString(Long.toString(this.getNonce()));

            //combine our challenge with the nonce
            String combinedChallengeWithNonce = this.getChallenge() + nonceEncoded;
            System.out.println(combinedChallengeWithNonce);

            //hash our combined challenge and nonce strings
            buffer.put(SHAUtils.digest(combinedChallengeWithNonce.getBytes(StandardCharsets.UTF_8), hashAlgorithm));
            System.out.println(SHAUtils.bytesToHex(buffer.array()));

            byte[] byteArray = Arrays.copyOf(buffer.array(), buffer.array().length);

            isSolved = isSolutionToChallenge(byteArray, this.getDifficulty());

            //get the is solved by other miner value
            setSolvedByOtherMiner(MinerNetwork.isSolved);

            if(isSolved && !isSolvedByOtherMiner){
                System.out.println("Puzzle solved!!");
                System.out.println("Nonce value is: " + getNonce());
                System.out.println("Encoded is: " + generateBase64EncodedString(Long.toString(getNonce())));
                System.out.println("Combined Challenge with nonce is: " + combinedChallengeWithNonce);
            } else  if (isSolvedByOtherMiner){
                // if we are here, that means another miner beat us to solving the challenge
                // so we should return, and mine the next challenge.

                return;
            } else {
                //we did not find a solution to the challenge
                //increment our nonce
                //TODO maybe we want to set the nonce to negative values also?
                setNonce(getNonce() +1);
            }

//            try {
//                TimeUnit.MILLISECONDS.sleep(500);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

        }


    }

    /**
     *
     * @param challenge the full challenge string we want to solve
     * @param nonce the value that needs to be tested
     * @param difficulty [int] difficulty of the puzzle, each increase doubles the difficulty i.e 2^n
     * @return true if the nonce value when encoded in base64 solves the puzzle
     */
    //method to check if a nonce value hashed with a challenge is valid
    public static boolean isValidSolution(String challenge, String nonce, int difficulty){
        //allocate 32 bytes to hold the output of a sha-256 hash
        ByteBuffer buffer = ByteBuffer.allocate(32);

        String combinedString = challenge + generateBase64EncodedString(nonce);

        //hash our combined challenge and nonce strings
        buffer.put(SHAUtils.digest(combinedString.getBytes(StandardCharsets.UTF_8), hashAlgorithm));
        System.out.println(SHAUtils.bytesToHex(buffer.array()));

        byte[] byteArray = Arrays.copyOf(buffer.array(), buffer.array().length);

        return isSolutionToChallenge(byteArray, difficulty);
    }

    /**
     *
     * @param byteArray [byte []] the byte array containing the bytes to check
     * @param difficulty [int] difficulty of the puzzle, each increase doubles the difficulty i.e 2^n
     * @return returns true if a byte array has a certain number of leading zeros based on the difficulty
     */
    public static boolean isSolutionToChallenge(byte[] byteArray, int difficulty){

        for (int i = 0; i < difficulty; i++) {
            if (isBitSet(byteArray, i)) {

                return false;
            }
        }

        return true;

    }

    /**
     * Method to return if a bit is set
     * @param array [byte []] the byte array containing the bytes to check
     * @param bit [int] the bit we are trying to validite is set
     * @return returns true if the bit is a 1 else false
     */
    //method to return if a bit is set or not
    public static boolean isBitSet(byte[] array, int bit){
        //get which byte we are on
        int index = bit / 8;

        //get the specific bit
        int bitPosition = bit % 8;

        return (array[index] >> bitPosition & 1) == 1;
    }

    //getters and setters

    public long getNonce() {
        return nonce;
    }

    /**
     * Getter for private variable nonce
     * @param nonce [int] value to be 'guessed'
     */
    public void setNonce(long nonce) {
        this.nonce = nonce;
    }

    public String getChallenge() {
        return challenge;
    }

    /**
     * Getter for private variable nonce
     * @param challenge [string] the entire challenge string
     *  which consists of the version:difficulty:timestamp:resource:randomString:
     */
    public void setChallenge(String challenge) {
        this.challenge = challenge;
    }

    /**
     * Getter for private variable nonce
     * @param timeStamp [long] value of the system time in unix epoch
     */
    public void setTimeStamp(long timeStamp){
        this.timeStamp = timeStamp;
    }

    public long getTimeStamp(){
        return this.timeStamp;
    }

//    public String getRandomString() {
//        return randomString;
//    }

    /**
     * Getter for private variable nonce
     */
//    public void setRandomString(String randomString) {
//        this.randomString = randomString;
//    }

    public int getDifficulty() {
        return difficulty;
    }

    /**
     * Getter for private variable nonce
     * @param difficulty [int] difficulty of the puzzle, each increase doubles the difficulty i.e 2^n
     */
    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public int getVersion() {
        return version;
    }

    /**
     * Getter for private variable nonce
     * @param version [int] version of HashCash
     */
    public void setVersion(int version) {
        this.version = version;
    }

    public String getResource() {
        return resource;
    }

    /**
     * @param resource [string] unique identifier, for Bitcoin it should be publicKey
     */
    public void setResource(String resource) {
        this.resource = resource;
    }

    /**
     *
     * @return the full token which is the challenge plus our nonce encoded
     * @throws Exception We need to throw an exception for if the puzzle has not been solved
     * therefore there is no full token
     */
    public String getToken() throws Exception {
        //get the full token if it is solved
        String[] values = this.getChallenge().split(":");
        if (values[5] == null || values[5].equals("")){
            throw new Exception("Token has not been solved! It needs to be mined first!");
        }
        return this.getChallenge() + generateBase64EncodedString(Long.toString(this.getNonce()));
    }

    public static void main(String[] args){

        HashCash hashCash = new HashCash(1,"Test1",System.currentTimeMillis());
        System.out.println(hashCash.getChallenge());
        long start = System.nanoTime();

        // handle the case where if the puzzle has not been solved
        // thus we don't actually have the token yet!
        try {
            hashCash.getToken();
        } catch (Exception e){
            System.out.println("Puzzle has not be solved yet");
        }

        hashCash.mine();

        if (HashCash.isValidSolution(hashCash.getChallenge(), Long.toString(hashCash.getNonce()), 8)) {
            System.out.println("Puzzle has valid solution");
            System.out.println(hashCash.getNonce() + " is a valid solution");
        } else {
            System.out.println("Puzzle is invalid!!!");
        }
        long end = System.nanoTime();
        double result = (double)(end - start) / 1000000000;
        System.out.println("Running time is: " + result + " seconds");

    }

    public boolean isSolvedByOtherMiner() {
        return isSolvedByOtherMiner;
    }

    public void setSolvedByOtherMiner(boolean solvedByOtherMiner) {
        isSolvedByOtherMiner = solvedByOtherMiner;
    }

    public boolean isSolved() {
        return isSolved;
    }

    public void setSolved(boolean solved) {
        isSolved = solved;
    }
}
