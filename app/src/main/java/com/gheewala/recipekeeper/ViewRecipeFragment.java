package com.gheewala.recipekeeper;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by hetag on 12/11/2017.
 */

public class ViewRecipeFragment  extends Fragment {

    private long rowID;
    private Menu fragmentmenu;

    //Overview
    private TextView recipenameTextView;
    private TextView preptimeTextView;
    private TextView cooktimeTextView;

    //Camera
    private ImageView recipeImage;
    private ImageButton recipeuploadImageButton;

    //Ingredients
    private EditText qtyEditText;
    private EditText measureEditText;
    private EditText ingredientEditText;
    private ImageButton addIngredientButton;
    private ListView ingredientsListView;
    private CursorAdapter ingredientsAdapter;
    private ArrayList<String> ingredientsListItems;
    private ArrayAdapter<String> ingredientsArrayAdapter;

    private ArrayList<String> originalIngredientsListItems;

    private EditText sizeEditText;
    private ImageButton calculateButton;

    //Direction
    private EditText directionEditText;
    private ImageButton addDirectionButton;
    private ListView directionsListView;
    private CursorAdapter directionsAdapter;
    private ArrayList<String> directionsListItems;
    private ArrayAdapter<String> directionsArrayAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view;
        view = inflater.inflate(R.layout.recipe_fragment,container, false);

        TabHost host = (TabHost)view.findViewById(R.id.tabHost);
        host.setup();

        //Tab 1
        TabHost.TabSpec spec = host.newTabSpec("Overview");
        spec.setContent(R.id.overview);
        spec.setIndicator("Overview");
        host.addTab(spec);

        //Tab 2
        spec = host.newTabSpec("Ingredients");
        spec.setContent(R.id.ingredients);
        spec.setIndicator("Ingredients");
        host.addTab(spec);

        //Tab 3
        spec = host.newTabSpec("Direction");
        spec.setContent(R.id.direction);
        spec.setIndicator("Direction");
        host.addTab(spec);

        //Overview
        recipenameTextView = (TextView) view.findViewById(R.id.etRecipe);
        preptimeTextView = (TextView) view.findViewById(R.id.etPrepTime);
        cooktimeTextView = (TextView) view.findViewById(R.id.etCookTime);

        //Ingredients
        qtyEditText = (EditText) view.findViewById(R.id.etQty);
        measureEditText = (EditText) view.findViewById(R.id.etMeasure);
        ingredientEditText = (EditText) view.findViewById(R.id.etIngredient);
        addIngredientButton = (ImageButton) view.findViewById(R.id.addIngredient);

        recipeImage = (ImageView) view.findViewById(R.id.imageRecipe);
        recipeuploadImageButton = (ImageButton) view.findViewById(R.id.ibRecipeUpload);
        recipeuploadImageButton.setVisibility(View.INVISIBLE);

        sizeEditText = (EditText) view.findViewById(R.id.etSize);
        calculateButton = (ImageButton) view.findViewById(R.id.calculate);

        sizeEditText.setVisibility(View.VISIBLE);
        calculateButton.setVisibility(View.VISIBLE);

        ingredientsListItems = new ArrayList<String>();
        originalIngredientsListItems = new ArrayList<String>();
        ingredientsListView = (ListView) view.findViewById(R.id.lvViewIngredients);
        String[] fromingredient = new String[]{"ingredient"};
        int[] toingredient = new int[]{R.id.recipeTextView};
        ingredientsAdapter = new SimpleCursorAdapter(view.getContext(), R.layout.main_fragment_item, null, fromingredient, toingredient, 0);

        ingredientsListView.setAdapter(ingredientsAdapter);

        ingredientsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> a, View v, final int position, long id) {

                final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

                AlertDialog.Builder builder =
                        new AlertDialog.Builder(getActivity());

                builder.setTitle(R.string.confirmTitle);
                builder.setMessage(R.string.confirmIngredientMessage);

                builder.setPositiveButton(R.string.delete,
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int button) {
                                ingredientsListItems.remove(position);
                                ingredientsArrayAdapter.notifyDataSetChanged();
                            }
                        }
                );

                builder.setNegativeButton(R.string.cancel, null);
                builder.show();
            }
        });

        addIngredientButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

                if(ingredientEditText.getText().toString().length() > 0) {
                    ingredientsListItems.add(qtyEditText.getText().toString() + "- " + measureEditText.getText().toString()
                            + " : " + ingredientEditText.getText().toString());
                    ingredientsArrayAdapter.notifyDataSetChanged();

                    qtyEditText.setText(null);
                    measureEditText.setText("");
                    ingredientEditText.setText("");

                } else {
                    Toast.makeText(getActivity(), "Please enter ingredient", Toast.LENGTH_LONG).show();
                }
            }
        });

        calculateButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

                if(sizeEditText.getText().toString().length() > 0 ) {

                    for(int i=0;i<ingredientsListItems.size();i++){
                        String[] ingredient = ingredientsListItems.get(i).split("-");
                        int qty = Integer.parseInt(ingredient[0]) * Integer.parseInt(sizeEditText.getText().toString());

                        String newingredient = qty +"- " + ingredient[1];

                        originalIngredientsListItems.add(newingredient);

                    }
                    ingredientsArrayAdapter = new ArrayAdapter<String>(getActivity(),
                            android.R.layout.simple_list_item_1, originalIngredientsListItems);
                    ingredientsListView.setAdapter(ingredientsArrayAdapter);

                    sizeEditText.setText("");

                } else {
                    Toast.makeText(getActivity(), "Please enter Serving size", Toast.LENGTH_LONG).show();
                }
            }
        });


        //Directions
        directionEditText = (EditText) view.findViewById(R.id.etDirection);
        addDirectionButton = (ImageButton) view.findViewById(R.id.addDirection);

        directionsListItems = new ArrayList<String>();
        directionsListView = (ListView) view.findViewById(R.id.lvviewDirections);
        String[] fromdirection = new String[]{"direction"};
        int[] todirection = new int[]{R.id.recipeTextView};
        directionsAdapter = new SimpleCursorAdapter(view.getContext(), R.layout.main_fragment_item, null, fromdirection, todirection, 0);

        directionsListView.setAdapter(directionsAdapter);

        directionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, final int position,
                                    long id) {

                final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

                AlertDialog.Builder builder =
                        new AlertDialog.Builder(getActivity());

                builder.setTitle(R.string.confirmTitle);
                builder.setMessage(R.string.confirmDirectionMessage);

                builder.setPositiveButton(R.string.delete,
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int button) {
                                directionsListItems.remove(position);
                                directionsArrayAdapter.notifyDataSetChanged();
                            }
                        }
                );

                builder.setNegativeButton(R.string.cancel, null);
                builder.show();
            }
        });

        addDirectionButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

                if(directionEditText.getText().toString().length() > 0) {
                    directionsListItems.add(directionEditText.getText().toString());
                    directionsArrayAdapter.notifyDataSetChanged();

                    directionEditText.setText("");

                } else {
                    Toast.makeText(getActivity(), "Please enter direction", Toast.LENGTH_LONG).show();
                }
            }
        });

        Bundle bundle = getArguments();
        rowID = bundle.getLong("ROW_ID");

        setFragmentEnable(false);
        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        new LoadRecipeTask().execute(rowID);
        new LoadRecipeIngredientsTask().execute(rowID);
        new LoadRecipeDirectionsTask().execute(rowID);
    }

    @Override
    public void onStop() {

        //Ingredients
        Cursor ingredientscursor = ingredientsAdapter.getCursor();

        if (ingredientscursor != null)
            ingredientscursor.close();

        ingredientsAdapter.changeCursor(null);

        //Directions
        Cursor directionscursor = directionsAdapter.getCursor();

        if (directionscursor != null)
            directionscursor.close();

        directionsAdapter.changeCursor(null);

        super.onStop();
    }

    private class LoadRecipeTask extends AsyncTask<Long, Object, Cursor> {
        DatabaseConnector databaseConnector =
                new DatabaseConnector(getContext());

        @Override
        protected Cursor doInBackground(Long... params)
        {
            databaseConnector.open();
            return databaseConnector.getOneRecipe(params[0]);
        }

        @Override
        protected void onPostExecute(Cursor result)
        {
            super.onPostExecute(result);

            result.moveToFirst();

            int recipenameIndex = result.getColumnIndex("recipename");
            int preptimeIndex = result.getColumnIndex("preptime");
            int cooktimeIndex = result.getColumnIndex("cooktime");
            int recipeimageIndex = result.getColumnIndex("recipeImage");

            recipenameTextView.setText(result.getString(recipenameIndex));
            preptimeTextView.setText(result.getString(preptimeIndex));
            cooktimeTextView.setText(result.getString(cooktimeIndex));

            if(result.getString(recipeimageIndex) != null){
                String[] uri = result.getString(recipeimageIndex).split(":");
                File imgFile = new  File(uri[1]);

                if(imgFile.exists()){

                    if(!result.getString(recipeimageIndex).equals("file:///storage/emulated/0/Heta/noimage.png")) {
                        Bitmap myBitmap = decodeFile(uri[1]);
                        recipeImage.setImageBitmap(myBitmap);
                    }
                }
            }

            result.close();
            databaseConnector.close();
        }
    }

    private class LoadRecipeIngredientsTask extends AsyncTask<Long, Object, Cursor> {
        DatabaseConnector databaseConnector =
                new DatabaseConnector(getContext());

        @Override
        protected Cursor doInBackground(Long... params)
        {
            databaseConnector.open();
            return databaseConnector.getRecipeIngredients(params[0]);
        }

        @Override
        protected void onPostExecute(Cursor result)
        {
            super.onPostExecute(result);

            int i=1;
            for(result.moveToFirst(); !result.isAfterLast(); result.moveToNext()) {
                ingredientsListItems.add(result.getString(i));
            }

            ingredientsArrayAdapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_list_item_1, ingredientsListItems);
            ingredientsListView.setAdapter(ingredientsArrayAdapter);

            databaseConnector.close();
        }
    }

    private class LoadRecipeDirectionsTask extends AsyncTask<Long, Object, Cursor> {
        DatabaseConnector databaseConnector =
                new DatabaseConnector(getContext());

        @Override
        protected Cursor doInBackground(Long... params)
        {
            databaseConnector.open();
            return databaseConnector.getRecipeDirections(params[0]);
        }

        @Override
        protected void onPostExecute(Cursor result)
        {
            super.onPostExecute(result);

            int i=1;
            for(result.moveToFirst(); !result.isAfterLast(); result.moveToNext()) {
                directionsListItems.add(result.getString(i));
            }

            directionsArrayAdapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_list_item_1, directionsListItems);
            directionsListView.setAdapter(directionsArrayAdapter);

            databaseConnector.close();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.viewmenu, menu);
        menu.findItem(R.id.actionSave).setVisible(false);
        fragmentmenu = menu;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

        switch (item.getItemId())
        {
            case R.id.actionEdit:
                setFragmentEnable(true);
                fragmentmenu.findItem(R.id.actionEdit).setVisible(false);
                fragmentmenu.findItem(R.id.actionDelete).setVisible(false);
                fragmentmenu.findItem(R.id.actionShoppingList).setVisible(false);
                fragmentmenu.findItem(R.id.actionSave).setVisible(true);

                ingredientsArrayAdapter = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_list_item_1, ingredientsListItems);
                ingredientsListView.setAdapter(ingredientsArrayAdapter);

                sizeEditText.setVisibility(View.INVISIBLE);
                calculateButton.setVisibility(View.INVISIBLE);

                return true;
            case R.id.actionDelete:
                deleteRecipe();
                return true;
            case R.id.actionSave:
                if (recipenameTextView.getText().length() != 0) {

                    AsyncTask<Object, Object, Object> updateRecipeTask =
                            new AsyncTask<Object, Object, Object>() {
                                @Override
                                protected Object doInBackground(Object... params) {
                                    updateRecipe();
                                    return null;
                                }

                                @Override
                                protected void onPostExecute(Object result) {
                                    getActivity().finish();
                                }
                            };
                    updateRecipeTask.execute((Object[]) null);

                    Intent i = new Intent(getActivity(),MainActivity.class);
                    startActivity(i);
                }
                else {
                    AlertDialog.Builder builder =
                            new AlertDialog.Builder(getActivity());

                    builder.setTitle(R.string.errorTitle);
                    builder.setMessage(R.string.errorMessage);
                    builder.setPositiveButton(R.string.errorButton, null);
                    builder.show();
                    return true;
                }
                return true;
            case R.id.actionShoppingList:

                if (ingredientsListItems.size() > 0) {

                    AsyncTask<Object, Object, Object> saveShoppingListTask =
                            new AsyncTask<Object, Object, Object>() {
                                @Override
                                protected Object doInBackground(Object... params) {
                                    saveShoppingList();
                                    return null;
                                }

                                @Override
                                protected void onPostExecute(Object result) {
                                    getActivity().finish();
                                }
                            };
                    saveShoppingListTask.execute((Object[]) null);

                    Intent i = new Intent(getActivity(),MainActivity.class);
                    startActivity(i);
                }
                else {
                    AlertDialog.Builder builder =
                            new AlertDialog.Builder(getActivity());

                    builder.setTitle(R.string.errorTitleShopping);
                    builder.setMessage(R.string.errorMessageShopping);
                    builder.setPositiveButton(R.string.errorButton, null);
                    builder.show();
                    return true;
                }
                return true;
        }
        return false;
    }

    private void deleteRecipe() {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.confirmTitle);
        builder.setMessage(R.string.confirmMessage);

        builder.setPositiveButton(R.string.delete,
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int button)
                    {
                        final DatabaseConnector databaseConnector =
                                new DatabaseConnector(getActivity());

                        AsyncTask<Long, Object, Object> deleteTask =
                                new AsyncTask<Long, Object, Object>()
                                {
                                    @Override
                                    protected Object doInBackground(Long... params)
                                    {
                                        databaseConnector.deleteRecipe(params[0]);
                                        return null;
                                    }

                                    @Override
                                    protected void onPostExecute(Object result)
                                    {
                                        getActivity().finish();
                                    }
                                };

                        deleteTask.execute(new Long[] { rowID });

                        Intent i = new Intent(getActivity(),MainActivity.class);
                        startActivity(i);
                    }
                }
        );

        builder.setNegativeButton(R.string.cancel, null);
        builder.show();
    }

    private void updateRecipe(){
        DatabaseConnector databaseConnector = new DatabaseConnector(getActivity());

        databaseConnector.updateRecipe(rowID,
                recipenameTextView.getText().toString(),
                preptimeTextView.getText().toString(),
                cooktimeTextView.getText().toString(),
                ingredientsListItems, directionsListItems);
    }

    private void saveShoppingList() {
        DatabaseConnector databaseConnector = new DatabaseConnector(getActivity());
        if(originalIngredientsListItems.size() == 0){
            originalIngredientsListItems = ingredientsListItems;
        }
        databaseConnector.insertShoppingList(originalIngredientsListItems);
    }

    public void setFragmentEnable(boolean set){

        //Overview
        recipenameTextView.setEnabled(set);
        preptimeTextView.setEnabled(set);
        cooktimeTextView.setEnabled(set);

        //Ingredients
        ingredientsListView.setEnabled(set);
        ingredientEditText.setEnabled(set);
        qtyEditText.setEnabled(set);
        measureEditText.setEnabled(set);
        addIngredientButton.setEnabled(set);

        //Directions
        directionsListView.setEnabled(set);
        directionEditText.setEnabled(set);
        addDirectionButton.setEnabled(set);
    }

    //Camera

    public static Bitmap decodeFile(String pathName) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 5;
        try {
            bitmap = BitmapFactory.decodeFile(pathName, options);
        } catch (OutOfMemoryError outOfMemoryError) {
            Log.e("Image", "outOfMemoryError while reading file for sampleSize " + options.inSampleSize
                        + " retrying with higher value");
        }

        return bitmap;
    }
}
