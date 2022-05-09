package com.example.dictionary.Admin;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.dictionary.R;
import com.example.dictionary.Var;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class AddCategory extends AppCompatActivity {

    Button button_add_category;
    EditText category_name;
    ImageView category_image;
    String category_name_txt, imgname;

    Uri imuri;

    Bitmap bitmap;
    RequestQueue rQueue;
    JSONObject jsonObject;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        button_add_category = findViewById(R.id.button_add_category);

        category_name = findViewById(R.id.category_name);
        category_image = findViewById(R.id.category_image);


        category_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openfilechooser();
            }
        });
        button_add_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                category_name_txt = category_name.getText().toString().trim();
                boolean flag = true;
                if (category_name_txt.equals("")) {
                    category_name.setError("This field is required");flag = false;
                }
                if (imuri == null) {
                    Toast.makeText(getApplicationContext(), "You must select a picture", Toast.LENGTH_LONG).show();flag = false;
                }
                if (!flag) { return;
                }
                final String REQUEST_URL = Var.add_category_url;
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
                try {
                    jsonObject = new JSONObject();
                    jsonObject.put("image", encodedImage);
                    jsonObject.put("name", category_name_txt);
                    jsonObject.put("image_name", imgname);
                } catch (JSONException e) { }
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, REQUEST_URL, jsonObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                rQueue.getCache().clear();
                                Toast.makeText(getApplication(), "Category added Successfully", Toast.LENGTH_SHORT).show();
                                finish(); }}, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                    }
                });
                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                        0,
                        -1,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                rQueue = Volley.newRequestQueue(getApplicationContext());
                rQueue.add(jsonObjectRequest);

            }
        });
    }

    private void openfilechooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imuri = data.getData();
            try {

                imgname = String.valueOf(Calendar.getInstance().getTimeInMillis());
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imuri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            category_image.setImageURI(imuri);
        }
    }
}




