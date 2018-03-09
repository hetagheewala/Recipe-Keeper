package com.gheewala.recipekeeper;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * Created by hetag on 12/10/2017.
 */

public class MainFragment extends Fragment {

    private ListView recipeListView;
    public static final String ROW_ID = "row_id";
    private CursorAdapter recipeAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_fragment, container, false);

        recipeListView = (ListView) view.findViewById(R.id.listView);

        TextView emptyText = (TextView) inflater.inflate(R.layout.main_fragment_empty_item, container,false);
        emptyText.setVisibility(View.GONE);

        ((ViewGroup) recipeListView.getParent()).addView(emptyText);
        recipeListView.setEmptyView(emptyText);

        String[] from = new String[]{"recipename"};
        int[] to = new int[]{R.id.recipeTextView};
        recipeAdapter = new SimpleCursorAdapter(view.getContext(), R.layout.main_fragment_item, null, from, to, 0);

        recipeListView.setAdapter(recipeAdapter);

        recipeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                ViewRecipeFragment viewRecipeFragment = new ViewRecipeFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.displayFragment, viewRecipeFragment);
                ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Recipe");
                ActionBar ab = ((AppCompatActivity)getActivity()).getSupportActionBar();
                ab.setDisplayHomeAsUpEnabled(true);
                Bundle bundle = new Bundle();
                bundle.putLong("ROW_ID", id);
                viewRecipeFragment.setArguments(bundle);
                ft.commit();
            }
        });

        return view;
    }

    private class GetRecipesTask extends AsyncTask<Object, Object, Cursor> {
        DatabaseConnector databaseConnector =
                new DatabaseConnector(getContext());

        @Override
        protected Cursor doInBackground(Object... params)
        {
            databaseConnector.open();
            return databaseConnector.getAllRecipes();
        }

        @Override
        protected void onPostExecute(Cursor result)
        {
            recipeAdapter.changeCursor(result);
            databaseConnector.close();
        }
    }

    @Override
    public void onStop() {
        Cursor cursor = recipeAdapter.getCursor();

        if (cursor != null)
            cursor.close();

        recipeAdapter.changeCursor(null);
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        new GetRecipesTask().execute((Object[]) null);
    }

}
