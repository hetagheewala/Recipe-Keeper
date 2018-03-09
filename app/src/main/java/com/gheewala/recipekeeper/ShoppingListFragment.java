package com.gheewala.recipekeeper;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * Created by hetag on 12/10/2017.
 */

public class ShoppingListFragment extends Fragment {

    private ListView shoppingListView;
    public static final String ROW_ID = "row_id";
    private CursorAdapter shoppingAdapter;
    private Cursor testCursor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.shoppinglist_fragment,container, false);

        setHasOptionsMenu(true);

        shoppingListView = (ListView) view.findViewById(R.id.lvShoppingList);

        TextView emptyText = (TextView) inflater.inflate(R.layout.main_fragment_empty_item, container,false);
        emptyText.setText(R.string.no_Shopping);
        emptyText.setVisibility(View.GONE);

        ((ViewGroup) shoppingListView.getParent()).addView(emptyText);
        shoppingListView.setEmptyView(emptyText);

        String[] from = new String[]{"item"};
        int[] to = new int[]{R.id.recipeTextView};
        shoppingAdapter = new SimpleCursorAdapter(view.getContext(), R.layout.main_fragment_item, null, from, to, 0);

        shoppingListView.setAdapter(shoppingAdapter);

        return view;
    }

    private class GetShoppingTask extends AsyncTask<Object, Object, Cursor> {
        DatabaseConnector databaseConnector =
                new DatabaseConnector(getContext());

        @Override
        protected Cursor doInBackground(Object... params)
        {
            databaseConnector.open();
            testCursor =  databaseConnector.getAllShopping();
            return testCursor;
        }

        @Override
        protected void onPostExecute(Cursor result)
        {
            shoppingAdapter.changeCursor(result);
            databaseConnector.close();
        }
    }

    @Override
    public void onStop() {
        Cursor cursor = shoppingAdapter.getCursor();

        if (cursor != null)
            cursor.close();

        shoppingAdapter.changeCursor(null);
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        new GetShoppingTask().execute((Object[]) null);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.shoppingmenu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

        switch (item.getItemId()) {
            case R.id.actionDelete:
                if(testCursor.getCount() > 0) {
                    deleteShoppingList();
                }
                return true;
        }
        return false;
    }

    private void deleteShoppingList() {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.confirmTitleShopping);
        builder.setMessage(R.string.confirmMessageShopping);

        builder.setPositiveButton(R.string.delete,
                new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int button)
                    {
                        final DatabaseConnector databaseConnector =
                                new DatabaseConnector(getActivity());

                        AsyncTask<Object, Object, Object> deleteTask =
                                new AsyncTask<Object, Object, Object>()
                                {
                                    @Override
                                    protected Object doInBackground(Object... params)
                                    {
                                        databaseConnector.deleteShoppingList();
                                        return null;
                                    }

                                    @Override
                                    protected void onPostExecute(Object result)
                                    {
                                        getActivity().finish();
                                    }
                                };

                        deleteTask.execute();

                        Intent i = new Intent(getActivity(),MainActivity.class);
                        startActivity(i);
                    }
                }
        );

        builder.setNegativeButton(R.string.cancel, null);
        builder.show();
    }
}
