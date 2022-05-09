package com.example.dictionary.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.example.dictionary.User.Translate;
import com.example.dictionary.Var;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AdminTerm extends AppCompatActivity {


    LinearLayout linear_terms;
    ImageButton button_add_term;

    String category_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_term);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            category_id = extras.getString("category_id");
        }
        button_add_term = findViewById(R.id.button_add_term);

        button_add_term.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddTerm.class);
                intent.putExtra("category_id", category_id);
                startActivity(intent);
            }
        });
        linear_terms = findViewById(R.id.linear_terms);

        get_all_term();


    }

    public void onRestart() {
        super.onRestart();
        linear_terms.removeAllViews();
        get_all_term();
    }

    private void get_all_term() {
        final String REQUEST_URL = Var.get_term_url;


        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest jsonRequest = new StringRequest(
                Request.Method.POST,
                REQUEST_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("response", response);

                        try {
                            JSONArray obj = new JSONArray(response);
                            Log.d("response", String.valueOf(obj));


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

        View view = getLayoutInflater().inflate(R.layout.manage_term_card, null);

        TextView term_keyword = view.findViewById(R.id.term_keyword);
        term_keyword.setText(data.getString("term_keyword"));

        ImageView term_image = view.findViewById(R.id.term_image);
        String download_URL = Var.images_url + data.getString("image_name") + ".jpg";

        Glide.with(getApplicationContext())
                .load(download_URL)
                .into(term_image);

        ImageView translate = view.findViewById(R.id.translate);
        Button button_edit = view.findViewById(R.id.button_edit);
        Button button_delete = view.findViewById(R.id.button_delete);
        translate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Translate.class);
                intent.putExtra("term_id", term_id);

                startActivity(intent);

            }
        });

        button_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), AdminEditterm.class);

                intent.putExtra("term_id", term_id);
                startActivity(intent);

            }
        });

        button_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String REQUEST_URL = Var.delete_term_url;
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                StringRequest jsonRequest = new StringRequest(
                        Request.Method.POST, REQUEST_URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("response", response);
                                try {
                                    JSONObject obj = new JSONObject(response);
                                    Log.d("response", obj.getString("code"));
                                    if (obj.getString("code").equals("-1")) {
                                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_LONG).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                            }
                        }
                ) {
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


                linear_terms.removeView(view);

            }
        });


        linear_terms.addView(view);
    }


}