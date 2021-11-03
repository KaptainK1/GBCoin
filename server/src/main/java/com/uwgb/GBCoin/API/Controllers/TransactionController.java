package com.uwgb.GBCoin.API.Controllers;

import com.uwgb.GBCoin.API.Services.TransactionService;
import com.uwgb.GBCoin.Model.DBTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService txService){
        transactionService = txService;
    }

    @PostMapping(path = "/submitTransaction")
    public void postNewTransaction(@RequestBody DBTransaction transaction){
        transactionService.saveNewTransaction(transaction);
    }

}
