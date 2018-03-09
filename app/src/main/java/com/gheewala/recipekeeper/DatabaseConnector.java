package com.gheewala.recipekeeper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;

public class DatabaseConnector 
{
   private static final String DATABASE_NAME = "Recipe";
   private SQLiteDatabase database;
   private DatabaseOpenHelper databaseOpenHelper;

   private class DatabaseOpenHelper extends SQLiteOpenHelper {

      public DatabaseOpenHelper(Context context, String name, CursorFactory factory, int version)
      {
         super(context, name, factory, version);
      }

      @Override
      public void onCreate(SQLiteDatabase db)
      {
         //Overview
         String createRecipesQuery = "CREATE TABLE recipes" +
                 "(_id integer primary key autoincrement," +
                 "recipename TEXT, preptime TEXT, cooktime TEXT, recipeImage TEXT);";

         db.execSQL(createRecipesQuery);

         //Ingredients
         String createIngredientsQuery = "CREATE TABLE ingredients" +
                 "(_id integer primary key autoincrement," +
                 "ingredient TEXT,"+
                 "_id_ingredient integer,"+
                 "FOREIGN KEY (_id_ingredient) REFERENCES recipes(_id));";

         db.execSQL(createIngredientsQuery);

         //Directions
         String createDirectionsQuery = "CREATE TABLE directions" +
                 "(_id integer primary key autoincrement," +
                 "direction TEXT,"+
                 "_id_direction integer,"+
                 "FOREIGN KEY (_id_direction) REFERENCES recipes(_id));";

         db.execSQL(createDirectionsQuery);

         //shopping
         String createshoppingQuery = "CREATE TABLE shopping" +
                 "(_id integer primary key autoincrement," +
                 "item TEXT);";

         db.execSQL(createshoppingQuery);
      }

      @Override
      public void onUpgrade(SQLiteDatabase db, int oldVersion,
                            int newVersion)
      {
      }
   }

   public DatabaseConnector(Context context) {
      databaseOpenHelper = 
         new DatabaseOpenHelper(context, DATABASE_NAME, null, 1);
   }

   public void open() throws SQLException {
      database = databaseOpenHelper.getWritableDatabase();
   }

   public void close() {
      if (database != null)
         database.close();
   }

   public void insertRecipe(String recipename, String preptime, String cooktime, String recipeImage,
                            ArrayList<String> ingredients, ArrayList<String> directions) {

      open();

      //Overview
      ContentValues newRecipe = new ContentValues();
      newRecipe.put("recipename", recipename);
      newRecipe.put("preptime", preptime.toString());
      newRecipe.put("cooktime", cooktime.toString());
      newRecipe.put("recipeImage", recipeImage.toString());


      long id = database.insert("recipes", null, newRecipe);

      //ingredient
      ContentValues ingredient = new ContentValues();
      for(int i=0;i<ingredients.size();i++){
         ingredient.put("ingredient", ingredients.get(i).toString());
         ingredient.put("_id_ingredient", id);
         database.insert("ingredients", null, ingredient);
      }

      //Direction
      ContentValues direction = new ContentValues();
      for(int i=0;i<directions.size();i++){
         direction.put("direction", directions.get(i).toString());
         direction.put("_id_direction", id);
         database.insert("directions", null, direction);
      }

      close();
   }

   public void insertShoppingList(ArrayList<String> shoppinglist){

      open();

      ContentValues shoppingItem = new ContentValues();
      for(int i=0;i<shoppinglist.size();i++){
         shoppingItem.put("item", shoppinglist.get(i).toString());
         database.insert("shopping", null, shoppingItem);
      }

      close();
   }

   public void updateRecipe(long id, String recipename, String preptime, String cooktime,
                            ArrayList<String> ingredients, ArrayList<String> directions) {

      open();

      //Overview
      ContentValues editRecipe = new ContentValues();
      editRecipe.put("recipename", recipename);
      editRecipe.put("preptime", preptime.toString());
      editRecipe.put("cooktime", cooktime.toString());


      database.update("recipes", editRecipe, "_id=" + id, null);

      //Ingredients
      database.delete("ingredients", "_id_ingredient=" + id, null);

      ContentValues ingredient = new ContentValues();
      for(int i=0;i<ingredients.size();i++){
         ingredient.put("ingredient", ingredients.get(i).toString());
         ingredient.put("_id_ingredient", id);
         database.insert("ingredients", null, ingredient);
      }

      //Directions

      database.delete("directions", "_id_direction=" + id, null);

      ContentValues direction = new ContentValues();
      for(int i=0;i<directions.size();i++){
         direction.put("direction", directions.get(i).toString());
         direction.put("_id_direction", id);
         database.insert("directions", null, direction);
      }

      close();
   }

   public Cursor getAllRecipes() {
      return database.query("recipes", new String[] {"_id", "recipename"},
         null, null, null, null, "recipename");
   }

   public Cursor getAllShopping() {
      return database.query("shopping", new String[] {"_id", "item"},
              null, null, null, null, null);
   }

   public Cursor getOneRecipe(long id) {
      return database.query(
         "recipes", null, "_id=" + id, null, null, null, null);
   }

   public Cursor getRecipeIngredients(long id) {
      return database.query(
              "ingredients", new String[] {"_id", "ingredient"}, "_id_ingredient=" + id, null, null, null, "ingredient");
   }

   public Cursor getRecipeDirections(long id) {
      return database.query(
              "directions", new String[]{"_id", "direction"}, "_id_direction=" + id, null, null, null, null);
   }

   public void deleteRecipe(long id) {
      open();
      database.delete("ingredients", "_id_ingredient=" + id, null);
      database.delete("directions", "_id_direction=" + id, null);
      database.delete("recipes", "_id=" + id, null);
      close();
   }

   public void deleteShoppingList() {
      open();
      database.delete("shopping", null, null);
      close();
   }
}

