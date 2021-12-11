package com.uwgb.GBCoin.Interfaces;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public interface Beautify {

    default String beautify(Object object){
        String jsonString = "";

        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();

        jsonString = gson.toJson(object);
        return jsonString;
    }

}
