package com.seoultechappsoftlab.wireloc.helpers;

import android.os.Environment;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Created by farissyariati on 6/21/15.
 */
public class JsonExportHelper<T> {

    private static final String JSON_RECORD_DIR = "/pdrpractice-particle-dump/";
    private static final String APPENDIX_JSON_NAME = "_json_record.txt";

    public JsonExportHelper(){
    }

    public boolean saveToJson(List<T> objects){
        boolean result = false;
        File rootPath = Environment.getExternalStorageDirectory();
        File saveDirectory = new File(rootPath.getAbsolutePath() + JSON_RECORD_DIR);

        if (!saveDirectory.exists()) {
            saveDirectory.mkdir();
        }

        String fileName = System.currentTimeMillis() + APPENDIX_JSON_NAME;
        File recordFile = new File(saveDirectory, fileName);

        String jsonString = "";

        try {
            jsonString = new Gson().toJson(objects);
        } catch (JsonParseException jsonEx) {
            jsonEx.printStackTrace();
        }

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(recordFile);
            PrintWriter printWriter = new PrintWriter(fileOutputStream);
            printWriter.print(jsonString);
            printWriter.flush();
            printWriter.close();
            result = true;
        } catch (IOException ioEx) {
            ioEx.toString();
        }

        return result;
    }
}
