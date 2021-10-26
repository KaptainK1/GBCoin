package com.uwgb.GBCoin.MSQLHelpers;

import com.uwgb.GBCoin.Model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
}
