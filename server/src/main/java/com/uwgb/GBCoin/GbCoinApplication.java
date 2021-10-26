package com.uwgb.GBCoin;

import com.uwgb.GBCoin.ProofOfWork.HashCash;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GbCoinApplication {

	public static void main(String[] args) {
		//HashCash cash = new HashCash("test");
		SpringApplication.run(GbCoinApplication.class, args);
	}

}
