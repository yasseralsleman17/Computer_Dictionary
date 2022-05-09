package com.example.dictionary.User;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;

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

public class UserActivity extends AppCompatActivity {

    GridLayout gridLayout;
    EditText editText_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        editText_search = findViewById(R.id.editText_search);
        editText_search.setImeOptions(EditorInfo.IME_ACTION_DONE);

        editText_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                if (i == EditorInfo.IME_ACTION_DONE) {
                    String term_txt = editText_search.getText().toString().trim();
                    Intent intent = new Intent(getApplicationContext(), SearchTerm.class);
                    intent.putExtra("term_keyword", term_txt);

                    startActivity(intent);
                    return true;
                }
                return false;

            }
        });

        gridLayout = findViewById(R.id.gridLayout);

        get_all_category();
    }

    void get_all_category() {

        final String REQUEST_URL = Var.get_category_url;
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest jsonRequest = new StringRequest(
                Request.Method.POST,
                REQUEST_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray obj = new JSONArray(response);
                            Log.d("response", String.valueOf(obj));

                            for (int i = 0; i < obj.length(); i++) {
                                show_category(obj.getJSONObject(i));
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
                return MyData;
            }
        };
        requestQueue.add(jsonRequest);


    }


    private void show_category(JSONObject jsonObject) throws JSONException {

        JSONObject data = new JSONObject(jsonObject.getString("data"));

        String category_id = data.getString("id");


        View view = getLayoutInflater().inflate(R.layout.category_card, null);
        ImageView category_image = view.findViewById(R.id.category_image);
        TextView category_name = view.findViewById(R.id.category_name);


        category_name.setText(data.getString("name"));

        String download_URL = Var.images_url + data.getString("image_name") + ".jpg";

        Glide.with(getApplicationContext())
                .load(download_URL)
                .into(category_image);


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), TermsList.class);


                intent.putExtra("category_id", category_id);
                startActivity(intent);
            }
        });


        gridLayout.addView(view);
    }


}