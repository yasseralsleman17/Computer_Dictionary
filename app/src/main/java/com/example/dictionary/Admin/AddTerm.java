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

public class AddTerm extends AppCompatActivity {


    Button button_add_term;
    EditText term_keyword, term_meaning, term_example, short_term, english_definition, arabic_definitions;
    ImageView term_image;
    String term_keyword_txt, term_meaning_txt, term_example_txt, imgname, category_id, short_term_txt, english_definition_txt, arabic_definitions_txt;

    Uri imuri;

    Bitmap bitmap;
    RequestQueue rQueue;
    JSONObject jsonObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_term);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            category_id = extras.getString("category_id");

        }

        button_add_term = findViewById(R.id.button_add_term);

        term_keyword = findViewById(R.id.term_keyword);
        term_meaning = findViewById(R.id.term_meaning);
        term_example = findViewById(R.id.term_example);
        short_term = findViewById(R.id.short_term);
        english_definition = findViewById(R.id.english_definition);
        arabic_definitions = findViewById(R.id.arabic_definitions);

        term_image = findViewById(R.id.term_image);

        term_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openfilechooser();
            }
        });
        button_add_term.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                term_keyword_txt = term_keyword.getText().toString().trim();
                term_meaning_txt = term_meaning.getText().toString().trim();
                term_example_txt = term_example.getText().toString().trim();
                short_term_txt = short_term.getText().toString().trim();
                english_definition_txt = english_definition.getText().toString().trim();
                arabic_definitions_txt = arabic_definitions.getText().toString().trim();
                boolean flag = true;


                if (term_keyword_txt.equals("")) {
                    term_keyword.setError("This field is required");
                    flag = false;
                }
                if (term_meaning_txt.equals("")) {
                    term_meaning.setError("This field is required");
                    flag = false;
                }
                if (term_example_txt.equals("")) {
                    term_example.setError("This field is required");
                    flag = false;
                }
                if (english_definition_txt.equals("")) {
                    english_definition.setError("This field is required");
                    flag = false;
                }
                if (short_term_txt.equals("")) {
                    short_term_txt = "NULL";
                }
                if (arabic_definitions_txt.equals("")) {
                    arabic_definitions.setError("This field is required");
                    flag = false;
                }
                if (imuri == null) {
                    Toast.makeText(getApplicationContext(), "You must select a picture", Toast.LENGTH_LONG).show();
                    flag = false;
                }
                if (!flag) {
                    return;
                }

                final String REQUEST_URL = Var.add_term_url;


                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
                try {
                    jsonObject = new JSONObject();

                    jsonObject.put("image", encodedImage);
                    jsonObject.put("term_example", term_example_txt);
                    jsonObject.put("term_meaning", term_meaning_txt);
                    jsonObject.put("term_keyword", term_keyword_txt);
                    jsonObject.put("category_id", category_id);
                    jsonObject.put("short_term", short_term_txt);
                    jsonObject.put("english_definition", english_definition_txt);
                    jsonObject.put("arabic_definitions", arabic_definitions_txt);
                    jsonObject.put("image_name", imgname);
                } catch (JSONException e) {
                    Log.e("JSONObject Here", e.toString());
                }
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, REQUEST_URL, jsonObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                rQueue.getCache().clear();
                                Toast.makeText(getApplication(), "term added Successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.e("aaaaaaa", volleyError.toString());
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
            term_image.setImageURI(imuri);
        }
    }


}