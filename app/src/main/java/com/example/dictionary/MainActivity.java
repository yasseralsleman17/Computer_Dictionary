package com.example.dictionary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.dictionary.Admin.AdminCategory;
import com.example.dictionary.Admin.AdminTerm;
import com.example.dictionary.User.UserActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private EditText userName, passWord;
    private TextView signIn;

    private String user_name_txt, password_txt;
    ImageButton button_Password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userName = (EditText) findViewById(R.id.editTextUsername);
        passWord = (EditText) findViewById(R.id.editTextPassword);


        button_Password =  findViewById(R.id.button_Password);

        button_Password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {

                    passWord.setTransformationMethod(PasswordTransformationMethod.getInstance());


                }
                else if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    passWord.setTransformationMethod(HideReturnsTransformationMethod.getInstance());



                }
                return false;            }
        });


        Button logIn = (Button) findViewById(R.id.buttonLogIn);
        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean flag = true;
                user_name_txt = userName.getText().toString();   password_txt = passWord.getText().toString();
                if (user_name_txt.isEmpty()) {
                    userName.setError("This field is required"); flag = false;  }
                if (password_txt.isEmpty()) {
                    passWord.setError("This field is required"); flag = false;  }
                if (!isValidPassword(password_txt)) {
                    passWord.setError("invalid password");flag = false;  }
                if (!flag) { return;  }
                if (user_name_txt.equals("admin@gmail.com") && password_txt.equals("admin123")) {
                    Intent i = new Intent(getApplicationContext(), AdminCategory.class); i.putExtra("admin_name", "admin"); startActivity(i);
                } else {
                    final String REQUEST_URL = Var.Signin_url;
                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                    StringRequest jsonRequest = new StringRequest( Request.Method.POST,  REQUEST_URL,
                            new Response.Listener<String>() { @Override
                                public void onResponse(String response) {
                                    try {
                                        JSONObject obj = new JSONObject(response);
                                        Log.d("response", obj.getString("code"));
                                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_LONG).show();
                                        if (obj.getString("code").equals("-1")) {
                                            Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_LONG).show();
                                        } else {
                                            JSONObject data = new JSONObject(obj.getString("data"));
                                            if (data.getString("user_type").equals("2")) {
                                                Intent i = new Intent(getApplicationContext(), UserActivity.class);
                                                i.putExtra("username", data.getString("name")); startActivity(i);
                                            } else if (data.getString("user_type").equals("1")) {
                                                Intent i = new Intent(getApplicationContext(),AdminCategory.class);
                                                i.putExtra("id", data.getString("id")); startActivity(i);  }  }
                                    } catch (JSONException e) {
                                        e.printStackTrace(); Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show(); } }
                            },  new Response.ErrorListener() { @Override  public void onErrorResponse(VolleyError error) { }}
                    ) {
                        @Override
                        protected Response<String> parseNetworkResponse(NetworkResponse response) {
                            int mStatusCode = response.statusCode;
                            Log.d("mStatusCode", String.valueOf(mStatusCode));


                            return super.parseNetworkResponse(response);

                        }
                        protected Map<String, String> getParams() {
                            Map<String, String> MyData = new HashMap<String, String>();
                            MyData.put("email", user_name_txt);
                            MyData.put("password", password_txt);
                            return MyData;
                        }
                    };
                    requestQueue.add(jsonRequest);


                }


            }

        });


        signIn = (TextView) findViewById(R.id.textSignIn);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(MainActivity.this, "signIn clicked", Toast.LENGTH_SHORT);
                Intent i = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(i);

            }
        });

    }

    private boolean isValidPassword(String password_txt) {
        Pattern PASSWORD_PATTERN
                = Pattern.compile(
                "[a-zA-Z0-9]{3,24}");

        return !TextUtils.isEmpty(password_txt) && PASSWORD_PATTERN.matcher(password_txt).matches();
    }

}