package com.example.dictionary.User;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SearchTerm extends AppCompatActivity {


    LinearLayout linear_term, linear_relative_term;


    EditText editText_search;

    String term_keyword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_term);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            term_keyword = extras.getString("term_keyword");
        }

        linear_term = findViewById(R.id.linear_term);
        linear_relative_term = findViewById(R.id.linear_relative_term);

        editText_search = findViewById(R.id.editText_search);

        editText_search.setImeOptions(EditorInfo.IME_ACTION_DONE);

        getterm(term_keyword);

        editText_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                if (i == EditorInfo.IME_ACTION_DONE) {
                    String term_keyword = editText_search.getText().toString().trim();
                    linear_term.removeAllViews();
                    getterm(term_keyword);

                    return true;
                }
                return false;

            }
        });

    }

    private void getterm(String term_keyword) {

        final String REQUEST_URL = Var.get_term_info_by_name;

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest jsonRequest = new StringRequest(
                Request.Method.POST,
                REQUEST_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONObject data;
                            data = obj.getJSONObject("data");
                            String term_id = data.getString("id");
                            View view = getLayoutInflater().inflate(R.layout.term_card, null);
                            TextView term_keyword = view.findViewById(R.id.term_keyword);
                            term_keyword.setText(data.getString("term_keyword"));
                            ImageView term_image = view.findViewById(R.id.term_image);
                            String download_URL = Var.images_url + data.getString("image_name") + ".jpg";
                            Glide.with(getApplicationContext()).load(download_URL).into(term_image);
                            ImageView translate = view.findViewById(R.id.translate);
                            translate.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) { }
                            });
                            linear_term.addView(view);
                            getrelative(data.getString("category_id"));
                        } catch (JSONException e) {
                            e.printStackTrace(); } }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }) {
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                int mStatusCode = response.statusCode;
                Log.d("mStatusCode", String.valueOf(mStatusCode));


                return super.parseNetworkResponse(response);

            }


            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put("term_keyword", term_keyword);

                return MyData;
            }


        };
        requestQueue.add(jsonRequest);


    }

    private void getrelative(String category_id) {


        final String REQUEST_URL = Var.get_term_url;

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest jsonRequest = new StringRequest(
                Request.Method.POST,
                REQUEST_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray obj = new JSONArray(response);
                            for (int i = 0; i < obj.length(); i++) {
                                showTerms(obj.getJSONObject(i));

                            }
                        } catch (JSONException e) {

                            e.printStackTrace();


                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }

                }) {
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                int mStatusCode = response.statusCode;
                Log.d("mStatusCode", String.valueOf(mStatusCode));


                return super.parseNetworkResponse(response);

            }


            protected Map<String, String> getParams() {
                Map<String, String> MyData = new HashMap<String, String>();
                MyData.put("category_id", category_id);

                return MyData;
            }


        };
        requestQueue.add(jsonRequest);


    }


    private void showTerms(JSONObject jsonObject) throws JSONException {

        JSONObject data = new JSONObject(jsonObject.getString("data"));

        String term_id = data.getString("id");

        View view = getLayoutInflater().inflate(R.layout.term_card, null);

        TextView term_keyword = view.findViewById(R.id.term_keyword);
        term_keyword.setText(data.getString("term_keyword"));

        ImageView term_image = view.findViewById(R.id.term_image);
        String download_URL = Var.images_url + data.getString("image_name") + ".jpg";

        Glide.with(getApplicationContext())
                .load(download_URL)
                .into(term_image);

        ImageView translate = view.findViewById(R.id.translate);

        translate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),Translate.class);
                intent.putExtra("term_id",term_id);

                startActivity(intent);
            }
        });


        linear_relative_term.addView(view);
    }


}