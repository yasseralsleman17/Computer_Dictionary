package com.example.dictionary.User;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

public class TermsList extends AppCompatActivity {


    String category_id;
    LinearLayout linear_term;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_list);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            category_id = extras.getString("category_id");

        }
        linear_term=findViewById(R.id.linear_term);

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
                        } catch (JSONException e) { e.printStackTrace(); } }},
                new Response.ErrorListener() {@Override public void onErrorResponse(VolleyError error) { }
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
        Glide.with(getApplicationContext()).load(download_URL).into(term_image);
        ImageView translate = view.findViewById(R.id.translate);
        translate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),Translate.class);
                intent.putExtra("term_id",term_id);
                startActivity(intent);
            }
        });



        linear_term.addView(view);
    }



}