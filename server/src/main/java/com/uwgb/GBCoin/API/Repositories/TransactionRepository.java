package com.uwgb.GBCoin.API.Repositories;

import com.uwgb.GBCoin.Model.DBTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<DBTransaction, Long> {
}
