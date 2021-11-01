package com.uwgb.GBCoin.API.Services;

import com.uwgb.GBCoin.API.Repositories.TransactionRepository;
import com.uwgb.GBCoin.Exceptions.ResouceNotFoundException;
import com.uwgb.GBCoin.Model.DBTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {


    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionService (TransactionRepository txRepository){
        transactionRepository = txRepository;
    }

    public void saveNewTransaction(DBTransaction transaction){
        if(transaction == null){
            throw new ResouceNotFoundException("Transaction is null");
        } else{
            transactionRepository.save(transaction);
        }
    }

    public List<DBTransaction> getAllTransactions(){
        return transactionRepository.findAll();
    }

}
