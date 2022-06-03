package com.bodykh.covid_19detectionapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "tag";
    public static String imageString;
    final String url = "http://10.0.2.2:5000/sendbase64";
    Button buttonSelectImage, buttonCheckCovid19;
    TextView stateText;
    ImageView previewImage;
    JSONObject jsonObject;
    RequestQueue rQueue;
    String tag_json_obj = "json_obj_req";
    ActivityResultLauncher<Intent> launchSomeActivity
            = registerForActivityResult(
            new ActivityResultContracts
                    .StartActivityForResult(),
            result -> {
                if (result.getResultCode()
                        == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    // do your operation from here....
                    if (data != null
                            && data.getData() != null) {
                        Uri selectedImageUri = data.getData();
                        Bitmap selectedImageBitmap = null;
                        try {
                            selectedImageBitmap
                                    = MediaStore.Images.Media.getBitmap(
                                    this.getContentResolver(),
                                    selectedImageUri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        previewImage.setBackgroundResource(android.R.color.transparent);
                        previewImage.setImageBitmap(
                                selectedImageBitmap);
                    }
                }
            });

    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setNavigationBarColor(getResources().getColor(R.color.teal_700));

        buttonSelectImage = findViewById(R.id.ButtonSelectImage);
        buttonCheckCovid19 = findViewById(R.id.ButtonCheckCovid19);
        stateText = findViewById(R.id.StateText);
        previewImage = findViewById(R.id.PreviewImage);

        buttonSelectImage.setOnClickListener(v -> imageChooser());


        buttonCheckCovid19.setOnClickListener(view -> {
            if (previewImage.getDrawable() == null) {
                Toast.makeText(MainActivity.this, "You Must Select An Chest X-Ray!", Toast.LENGTH_LONG).show();

            } else {
                Bitmap bitmap = ((android.graphics.drawable.BitmapDrawable) previewImage.getDrawable()).getBitmap();
                postDataa(url, bitmap);
            }
        });

    }

    private void imageChooser() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        launchSomeActivity.launch(i);
    }


    private void postDataa(String url, Bitmap bitmap) {

        final ProgressDialog pDialog = new ProgressDialog(MainActivity.this);
        pDialog.setMessage("Please Wait...");
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.show();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        try {
            jsonObject = new JSONObject();
            jsonObject.put("pic", "data:image/jpeg;base64,"+imageString);

        } catch (JSONException e) {
            Log.e("JSONObject Here", e.toString());
        }

        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        // on below line we are calling a string
        // request method to post the data to our API
        // in this we are calling a post method.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                url, jsonObject, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(MainActivity.this, "Detection Done!", Toast.LENGTH_SHORT).show();
                try {
                    stateText.setText("Detection Result:\n" + response.get("percentage").toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                pDialog.hide();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error: " + error.getMessage());
                pDialog.hide();
            }
        });
        rQueue = Volley.newRequestQueue(MainActivity.this);
        rQueue.add(jsonObjectRequest);



    }
}
