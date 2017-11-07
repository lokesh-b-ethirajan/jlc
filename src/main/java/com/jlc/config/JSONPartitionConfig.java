package com.jlc.config;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

/**
 * @author lokesh
 */

public class JSONPartitionConfig {

    private Gson gson = new Gson();
    private JsonReader jsonReader = null;

    public JSONPartitionConfig(File file) throws FileNotFoundException {
        jsonReader = new JsonReader(new FileReader(file));
    }

    public PartitionConfig[] getPartitionConfigs() {
        return gson.fromJson(jsonReader, PartitionConfig[].class);
    }

}
