package com.uwgb.GBCoin.DB;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.ArrayList;

public class DBPool {
    private static final int MAX_CONNECTIONS = 5;
    private DriverManager manager;
    ArrayList<Connection> connections;

    public DBPool(DriverManager manager){
        this.manager = manager;
        for (int i = 0; i < MAX_CONNECTIONS; i++) {
            //DriverManager.registerDriver(new Driver());
        }
    }



}
