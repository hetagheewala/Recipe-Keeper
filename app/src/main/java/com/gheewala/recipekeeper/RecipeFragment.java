package com.gheewala.recipekeeper;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Created by hetag on 12/10/2017.
 */

public class RecipeFragment extends Fragment {

    private long rowID;

    //Camera
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    public static final int MEDIA_TYPE_IMAGE = 1;
    private static final String IMAGE_DIRECTORY_NAME = "Heta";
    private Uri fileUri;
    private ImageView recipeImageView;
    private ImageButton recipeuploadImageButton;

    //Overview
    private EditText recipenameEditText;
    private EditText preptimeEditText;
    private EditText cooktimeEditText;

    //Ingredients
    private EditText qtyEditText;
    private EditText measureEditText;
    private EditText ingredientEditText;
    private ImageButton addIngredientButton;
    private ListView ingredientsListView;
    private ArrayList<String> ingredientsListItems;
    private ArrayAdapter<String> ingredientsAdapter;

    private EditText sizeEditText;
    private ImageButton calculateButton;

    //Direction
    private EditText directionEditText;
    private ImageButton addDirectionButton;
    private ListView directionsListView;
    private ArrayList<String> directionsListItems;
    private ArrayAdapter<String> directionAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.recipe_fragment,container, false);

        setHasOptionsMenu(true);

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

        //camera
        recipeImageView = (ImageView) view.findViewById(R.id.imageRecipe);
        recipeuploadImageButton = (ImageButton) view.findViewById(R.id.ibRecipeUpload);

        recipeuploadImageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                captureImage();
            }
        });

        if (!isDeviceSupportCamera()) {
            Toast.makeText(getActivity(),
                    "Sorry! Your device doesn't support camera",
                    Toast.LENGTH_LONG).show();
            getActivity().finish();
        }

        //Overview
        recipenameEditText = (EditText) view.findViewById(R.id.etRecipe);
        preptimeEditText = (EditText) view.findViewById(R.id.etPrepTime);
        cooktimeEditText = (EditText) view.findViewById(R.id.etCookTime);

        Bundle extras = getActivity().getIntent().getExtras();

        if (extras != null)
        {
            rowID = extras.getLong("row_id");
            recipenameEditText.setText(extras.getString("recipename"));
            preptimeEditText.setText(extras.getString("preptime"));
            cooktimeEditText.setText(extras.getString("cooktime"));
        }

        //Ingredients
        qtyEditText = (EditText) view.findViewById(R.id.etQty);
        measureEditText = (EditText) view.findViewById(R.id.etMeasure);
        ingredientEditText = (EditText) view.findViewById(R.id.etIngredient);
        addIngredientButton = (ImageButton) view.findViewById(R.id.addIngredient);

        sizeEditText = (EditText) view.findViewById(R.id.etSize);
        calculateButton = (ImageButton) view.findViewById(R.id.calculate);

        sizeEditText.setVisibility(View.INVISIBLE);
        calculateButton.setVisibility(View.INVISIBLE);


        ingredientsListView = (ListView) view.findViewById(R.id.lvViewIngredients);
        ingredientsListItems = new ArrayList<String>();
        ingredientsAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, ingredientsListItems);
        ingredientsListView.setAdapter(ingredientsAdapter);

        addIngredientButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

                if(ingredientEditText.getText().toString().length() > 0) {
                    ingredientsListItems.add(qtyEditText.getText().toString() + "- " + measureEditText.getText().toString()
                            + " : " + ingredientEditText.getText().toString());
                    ingredientsAdapter.notifyDataSetChanged();

                    qtyEditText.setText(null);
                    measureEditText.setText("");
                    ingredientEditText.setText("");

                } else {
                    Toast.makeText(getActivity(), "Please enter ingredient", Toast.LENGTH_LONG).show();
                }
            }
        });

        ingredientsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, final int position,
                                    long id) {

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
                                ingredientsAdapter.notifyDataSetChanged();
                            }
                        }
                );

                builder.setNegativeButton(R.string.cancel, null);
                builder.show();
            }
        });

        //Direction
        directionEditText = (EditText) view.findViewById(R.id.etDirection);
        addDirectionButton = (ImageButton) view.findViewById(R.id.addDirection);
        directionsListView = (ListView) view.findViewById(R.id.lvviewDirections);
        directionsListItems = new ArrayList<String>();
        directionAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, directionsListItems);
        directionsListView.setAdapter(directionAdapter);

        addDirectionButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                if(directionEditText.getText().toString().length() > 0) {
                    directionsListItems.add(directionEditText.getText().toString());
                    directionAdapter.notifyDataSetChanged();

                    directionEditText.setText("");

                } else {
                    Toast.makeText(getActivity(), "Please enter direction", Toast.LENGTH_LONG).show();
                }
            }
        });

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
                                ingredientsAdapter.notifyDataSetChanged();
                            }
                        }
                );

                builder.setNegativeButton(R.string.cancel, null);
                builder.show();
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.actionmenu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

        switch (item.getItemId()) {
            case R.id.actionSave:
                if (recipenameEditText.getText().length() != 0) {

                    AsyncTask<Object, Object, Object> saveRecipeTask =
                            new AsyncTask<Object, Object, Object>() {
                                @Override
                                protected Object doInBackground(Object... params) {
                                    saveRecipe();
                                    return null;
                                }

                                @Override
                                protected void onPostExecute(Object result) {
                                    getActivity().finish();
                                }
                            };
                    saveRecipeTask.execute((Object[]) null);

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
            case R.id.actionCencel:
                Intent i = new Intent(getActivity(),MainActivity.class);
                startActivity(i);

        }
        return false;
    }

    private void saveRecipe() {
        DatabaseConnector databaseConnector = new DatabaseConnector(getActivity());

        String filepath;

        if(fileUri == null){
            filepath = "file:///storage/emulated/0/Heta/noimage.png";

        }else{
            filepath = fileUri.toString();
        }

        try {
                databaseConnector.insertRecipe(
                        recipenameEditText.getText().toString(),
                        preptimeEditText.getText().toString(),
                        cooktimeEditText.getText().toString(), filepath, ingredientsListItems, directionsListItems
                );

        }catch(Exception e){
            Toast.makeText(getActivity(),"Please enter valid recipe details to save recipe",Toast.LENGTH_LONG).show();
        }
    }

    //Camera

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private static File getOutputMediaFile(int type) {

        File mediaStorageDir = new File(
                Environment
                        .getExternalStorageDirectory(),
                IMAGE_DIRECTORY_NAME);

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }

    private boolean isDeviceSupportCamera() {
        if (getActivity().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        try {
            startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
        }catch (Exception e){
            Toast.makeText(getActivity(),
                    "Please allow permissions to access Camera & Storage from Device Settings -> Application Manager -> RecipeKeeper -> Permissions",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                previewCapturedImage();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getActivity(),
                        "User cancelled image capture", Toast.LENGTH_LONG)
                        .show();
            } else {

                Toast.makeText(getActivity(),
                        "Sorry! Failed to capture image", Toast.LENGTH_LONG)
                        .show();
            }
        }
    }

    private void previewCapturedImage() {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;
            final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(), options);
            recipeImageView.setImageBitmap(bitmap);

        }catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
}
