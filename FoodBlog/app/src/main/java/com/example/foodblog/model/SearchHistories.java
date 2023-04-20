package com.example.foodblog.model;

import android.app.Activity;
import android.content.Context;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class SearchHistories {
    private static final int MAX_LENGTH = 10;
    private static final String FILE_NAME = "search_history.txt";

    private static SearchHistories instance;
    private ArrayList<Integer> recipeIdHistories;

    public SearchHistories() {
    }

    public static SearchHistories getInstance(Context context){
        if (instance == null){
            instance = new SearchHistories();
            instance.loadHistories(context);
        }
        return instance;
    }

    public ArrayList<Integer> getRecipeIdHistories() {
        return recipeIdHistories;
    }

    public void addHistory(Context context, Integer recipeId){
        if(recipeIdHistories == null){
            recipeIdHistories = new ArrayList<>();
        }
        recipeIdHistories.remove(recipeId);
        recipeIdHistories.add(0, recipeId);
        if (recipeIdHistories.size() > MAX_LENGTH){
            recipeIdHistories.remove(recipeIdHistories.size() - 1);
        }
        saveHistories(context);
    }

    public void clearHistory(Context context){
        recipeIdHistories.clear();
        saveHistories(context);
    }

    private void loadHistories(Context context) {
        ObjectInputStream objectIn = null;
        this.recipeIdHistories = new ArrayList<>();
        try {

            FileInputStream fileIn = context.getApplicationContext().openFileInput(FILE_NAME);
            objectIn = new ObjectInputStream(fileIn);
            this.recipeIdHistories = (ArrayList<Integer>) objectIn.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (objectIn != null) {
                try {
                    objectIn.close();
                } catch (IOException e) {
                    // do nowt
                }
            }
        }
    }

    private void saveHistories(Context context) {
        ObjectOutputStream objectOut = null;
        try {

            FileOutputStream fileOut = context.openFileOutput(FILE_NAME, Activity.MODE_PRIVATE);
            objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(recipeIdHistories);
            fileOut.getFD().sync();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (objectOut != null) {
                try {
                    objectOut.close();
                } catch (IOException e) {
                    // do nowt
                }
            }
        }
    }
}
