package com.gheewala.recipekeeper;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainmenu);

        if (savedInstanceState == null) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            MainFragment fragment = new MainFragment();
            ft.add(R.id.displayFragment, fragment);
            ft.addToBackStack("");
            ft.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if(this.getCurrentFocus() != null)
            imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        FragmentTransaction ft;
        ActionBar ab;

        switch (item.getItemId()) {
            case R.id.mAbout:
                AboutFragment aboutfragment = new AboutFragment();
                ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.displayFragment, aboutfragment);
                getSupportActionBar().setTitle("About");
                ab = getSupportActionBar();
                ab.setDisplayHomeAsUpEnabled(true);
                ft.commit();
                break;
            case R.id.mAddRecipe:
                RecipeFragment addrecipefragment = new RecipeFragment();
                ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.displayFragment, addrecipefragment);
                getSupportActionBar().setTitle("Add Recipe");
                ab = getSupportActionBar();
                ab.setDisplayHomeAsUpEnabled(true);
                ft.commit();
                break;
            case R.id.mShoppingList:
                ShoppingListFragment shoppinglistfragment = new ShoppingListFragment();
                ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.displayFragment, shoppinglistfragment);
                getSupportActionBar().setTitle("Shopping List");
                ab = getSupportActionBar();
                ab.setDisplayHomeAsUpEnabled(true);
                ft.commit();
                break;
            case android.R.id.home: //back home button
                Intent i = new Intent(this,MainActivity.class);
                startActivity(i);
        }
        return false;
    }
}
