package com.example.dictionary.User;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.dictionary.R;
import com.example.dictionary.Var;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Translate extends AppCompatActivity {

    String term_id;
    ImageView term_image;
    TextView term_keyword, term_meaning, arabic_definitions, english_definition;
    ImageButton imageButton_english_definition, imageButton_term_keyword;
    TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            term_id = extras.getString("term_id");
        }

        term_image = findViewById(R.id.term_image);
        term_keyword = findViewById(R.id.term_keyword);
        arabic_definitions = findViewById(R.id.arabic_definitions);
        english_definition = findViewById(R.id.english_definition);
        term_meaning = findViewById(R.id.term_meaning);


        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) {
                    int lang = textToSpeech.setLanguage(Locale.ENGLISH);
                } }
        });
        imageButton_term_keyword = findViewById(R.id.imageButton_term_keyword);
        imageButton_english_definition = findViewById(R.id.imageButton_english_definition);
        imageButton_term_keyword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String term_keyword_txt = term_keyword.getText().toString();
                int speech = textToSpeech.speak(term_keyword_txt, TextToSpeech.QUEUE_FLUSH, null);

            }
        });
        imageButton_english_definition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String english_definition_txt = english_definition.getText().toString();
                int speech = textToSpeech.speak(english_definition_txt, TextToSpeech.QUEUE_FLUSH, null);

            }
        });
        getterm();

    }


    private void getterm() {

        final String REQUEST_URL = Var.get_term_info_url;
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest jsonRequest = new StringRequest(
                Request.Method.POST,
                REQUEST_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject obj = new JSONObject(response);
                            Log.d("response", String.valueOf(obj));
                            JSONObject data;
                            data = obj.getJSONObject("data");
                            String download_URL = Var.images_url + data.getString("image_name") + ".jpg";
                            Glide.with(getApplicationContext())
                                    .load(download_URL)
                                    .into(term_image);
                            term_keyword.setText(data.getString("term_keyword"));
                            term_meaning.setText(data.getString("term_meaning"));
                            english_definition.setText(data.getString("english_definition"));
                            arabic_definitions.setText(data.getString("arabic_definitions"));
                        } catch (JSONException e) { e.printStackTrace(); } }
                },
                new Response.ErrorListener() { @Override public void onErrorResponse(VolleyError error) { }

                }) {
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                int mStatusCode = response.statusCode;
                Log.d("mStatusCode", String.valueOf(mStatusCode));
                return super.parseNetworkResponse(response);

            }

            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put("term_id", term_id);

                return MyData;
            }


        };
        requestQueue.add(jsonRequest);


    }


}