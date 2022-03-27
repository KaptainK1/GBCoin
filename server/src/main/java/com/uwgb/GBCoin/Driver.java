package com.uwgb.GBCoin;

import java.security.PublicKey;
import java.util.*;

import com.uwgb.GBCoin.Miner.Miner;
import com.uwgb.GBCoin.Miner.MinerNetwork;
import com.uwgb.GBCoin.Model.BlockChain;
import com.uwgb.GBCoin.Model.CoinbaseTransaction;
import com.uwgb.GBCoin.Model.GenesisBlock;
import com.uwgb.GBCoin.Model.TransactionNetwork;
import com.uwgb.GBCoin.Model.UTXO;
import com.uwgb.GBCoin.Model.UTXOPool;
import com.uwgb.GBCoin.Model.Wallet;
import com.uwgb.GBCoin.Utils.Crypto;

public class Driver {
	
	public static void main(String args[]) {
		try {
			runMain();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
    private static void runMain() throws Exception {
    	//Create a few simple wallets
        Wallet walletCoinbase = new Wallet();
        Wallet walletA = new Wallet();
        Wallet walletB = new Wallet();

		walletA.setUniqueID("My Wallet");
		walletB.setUniqueID("Bob's Wallet");
        
        LinkedList<Wallet> walletList = new LinkedList();
        walletList.add(walletA);
        walletList.add(walletB);

        //create the utxo pool which is shared between miners
        UTXOPool pool = new UTXOPool();

        //create the blockchain
        BlockChain blockChain = new BlockChain();

        //create the miner network and transaction networks
        //these are simple publisher/subscriber patterns to simulate a distributed network
        MinerNetwork network = new MinerNetwork();
        TransactionNetwork transactionNetwork = new TransactionNetwork();

        //create two miners in this environment to simulate a distributed network
        Miner miner1 = new Miner(network, transactionNetwork, walletA.getPublicKey(), blockChain, pool);
        Miner miner2 = new Miner(network, transactionNetwork, walletB.getPublicKey(), blockChain, pool);

        //add each miner to the miner network and transaction network
        network.addObserver(miner1);
        network.addObserver(miner2);
        transactionNetwork.addObserver(miner1);
        transactionNetwork.addObserver(miner2);

        //assume Genesis Block and Coinbase Transaction was already found
        CoinbaseTransaction coinbaseTransaction = new CoinbaseTransaction(walletA.getPublicKey());
        byte[] coinbaseSig = Crypto.signMessage(walletCoinbase.getPrivateKey(), coinbaseTransaction.getDataToSign(0));
        coinbaseTransaction.addSignature(coinbaseSig, 0);

        //add coinbase utxo to utxo pool as it is available to be spent
        UTXO coinbaseUTXO = new UTXO(coinbaseTransaction.getHash(), 0);
        pool.addUTXO(coinbaseUTXO, coinbaseTransaction.getOutputs().get(0));

        //create the genesis block which we assumed to be found already
        GenesisBlock block = new GenesisBlock(walletA.getPublicKey(), System.currentTimeMillis(), coinbaseTransaction);
        block.hashObject();

        //add said genesis block to the block chain
        blockChain.addBlock(block);

        //clear all transactions from the miner's pool
        miner1.getTransactionPool().clearTransactions();

        //by using the miner network, notify each subscriber of the new genesis block
        network.setNextBlock(block);
        network.notifyObserver(null,null);
        BlockChain.printBlockChain(blockChain);
        
        //create a scanner object to take user input
        Scanner scanner = new Scanner(System.in);
        
        boolean isFinished = false;
        
        while(!isFinished) {
        	printMenu();
			int userChoice;

			try {
				userChoice = scanner.nextInt();
			} catch (InputMismatchException e){
				System.out.println("Error with reading input, please enter only a number");
				userChoice = scanner.nextInt();
			}
			//consume the \n character that is not consumed by next int
			scanner.nextLine();
        	
        	switch(userChoice) {
        	case (1):
        		miner1.mineNewBlock();
    			break;
        	case (2):
        		System.out.print("Enter amount to spend: ");
        		double amount = scanner.nextDouble();

				//consume the \n character that is not consumed by next double
				scanner.nextLine();

				//print the available wallets
				printWallets(walletList);

        		System.out.print("Enter a wallet address to pay: ");

        		String address = scanner.nextLine();

        		Wallet payee = getWallet(address, walletList);
        		if(payee != null && amount > 0 && amount < miner1.getTransactionPool().getTotalCoins(walletA.getPublicKey())) {
        			HashMap<PublicKey, Double> data = new HashMap();
        			data.put(payee.getPublicKey(), amount);
        			miner1.getTransactionPool().spendNewTransaction(amount, walletA, data);
        			
        		} else {
        			System.out.print("Error with wallet address or spending amount \n");
        			break;
        		}
        		break;
        	case (3):
        		System.out.println("Current Wallet Balance is: " + miner1.getTransactionPool().getTotalCoins(walletA.getPublicKey()));
        		break;
			case (4):
				System.out.println("Enter Wallet UniqueID to check..");
				String uniqueID = scanner.nextLine();
				printWalletBalance(uniqueID, walletList, miner1);
				break;
        	case (5):
        		BlockChain.printBlockChain(miner1.getBlockChain());
        		break;
        	case (6):
        		miner1.getTransactionPool().printAllAvailableUTXOs();
        		break;
			case (7):
				isFinished = true;
				break;
    		default:
    			System.out.println("Unknown option selected");
        	}
        	
        }

    }
    
    private static Wallet getWallet(String address, List<Wallet> availableWallets) {
    	Wallet foundWallet = null;
    	
    	for(Wallet w: availableWallets) {
    		String walletAddress = Crypto.getStringFromKey(w.getPublicKey());
    		if(walletAddress.equals(address)) {
    			foundWallet = w;
    			break;
    		}
    	}
    	
    	
    	return foundWallet;
    }

	private static void printWallets(List<Wallet> availableWallets){
		for(Wallet w: availableWallets) {
			System.out.println(Crypto.getStringFromKey(w.getPublicKey()));
			System.out.println("---------------------------");
			System.out.println(w.getUniqueID());
			System.out.println();
		}
	}

	private static void printWalletBalance(String uniqueWalletID, List<Wallet> availableWallets, Miner miner){
		for(Wallet w: availableWallets) {
			if (w.getUniqueID().equals(uniqueWalletID)) {
				System.out.println("Wallet Balance for " + uniqueWalletID + " is: " + miner.getTransactionPool().getTotalCoins(w.getPublicKey()));
				return;
			}
		}
		System.out.println(uniqueWalletID + " wallet was not found.");
	}
    
    
    private static void printMenu() {
    	System.out.println("Select an activity to perform:");
    	System.out.println("1: Mine a new block");
    	System.out.println("2: Submit a new transaction");
//    	System.out.println("3. Publish transactions to blockchain");
    	System.out.println("3: Print current wallet balance");
		System.out.println("4: Print a different wallet balance");
    	System.out.println("5: Print state of the blockchain");
		System.out.println("6: Print state of the UTXO Pool");
    	System.out.println("7: Quit");
    }

}
