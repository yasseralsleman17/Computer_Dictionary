package com.example.dictionary;

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

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.dictionary.User.UserActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private EditText fullName, email, passWord, conPassWord, phoneNum;
    private Button signIn;
    private TextView logIn;
    String full_name_txt, email_txt, password_txt, con_pass_txt, phone_txt;

    ImageButton button_Password, button_Confirm_Password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fullName = (EditText) findViewById(R.id.editTextFullname);
        email = (EditText) findViewById(R.id.editTextEmail);
        passWord = (EditText) findViewById(R.id.editTextPassword);
        conPassWord = (EditText) findViewById(R.id.editTextConPassword);
        phoneNum = (EditText) findViewById(R.id.editTextPhone);

        button_Password = findViewById(R.id.button_Password);
        button_Confirm_Password = findViewById(R.id.button_Confirm_Password);

        button_Confirm_Password.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {

                    conPassWord.setTransformationMethod(PasswordTransformationMethod.getInstance());


                } else if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    conPassWord.setTransformationMethod(HideReturnsTransformationMethod.getInstance());


                }
                return false;
            }
        });

        button_Password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {

                    passWord.setTransformationMethod(PasswordTransformationMethod.getInstance());


                } else if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    passWord.setTransformationMethod(HideReturnsTransformationMethod.getInstance());


                }
                return false;
            }
        });

        signIn = (Button) findViewById(R.id.buttonSignIn);
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                full_name_txt = fullName.getText().toString();
                email_txt = email.getText().toString();
                password_txt = passWord.getText().toString();
                con_pass_txt = conPassWord.getText().toString();
                phone_txt = phoneNum.getText().toString();

                boolean flag = true;

                if (full_name_txt.isEmpty()) {
                    fullName.setError("This field is required");
                    flag = false;
                }
                if (email_txt.isEmpty()) {
                    email.setError("This field is required");
                    flag = false;
                }
                if (password_txt.isEmpty()) {
                    passWord.setError("This field is required");
                    flag = false;
                }

                if (!isValidPassword(password_txt)) {
                    passWord.setError("invalid password");
                    flag = false;
                }
                if (con_pass_txt.isEmpty()) {
                    conPassWord.setError("This field is required");
                    flag = false;
                }
                if (!(password_txt.equals(con_pass_txt))) {
                    passWord.setError("Passwords must match");
                    conPassWord.setError("Passwords must match");
                    flag = false;
                }
                if (phone_txt.isEmpty()) {
                    phoneNum.setError("This field is required");
                    flag = false;
                }
                if (flag == false) {
                    return;
                }

                final String REQUEST_URL = Var.Signup_url;
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                StringRequest jsonRequest = new StringRequest(
                        Request.Method.POST,   REQUEST_URL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject obj = new JSONObject(response);
                                    Log.d("response", obj.getString("code"));
                                    Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_LONG).show();
                                    if (obj.getString("code").equals("-1")) {
                                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_LONG).show();
                                    } else {
                                        Intent i = new Intent(getApplicationContext(), UserActivity.class);
                                        startActivity(i); }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                } }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {  }}
                ) {
                    @Override
                    protected Response<String> parseNetworkResponse(NetworkResponse response) {
                        int mStatusCode = response.statusCode;
                        Log.d("mStatusCode", String.valueOf(mStatusCode));
                        return super.parseNetworkResponse(response);
                    }
                    protected Map<String, String> getParams() {
                        Map<String, String> MyData = new HashMap<String, String>();
                        MyData.put("name", full_name_txt);
                        MyData.put("phone", phone_txt);
                        MyData.put("email", email_txt);
                        MyData.put("password", password_txt);
                        return MyData;
                    }
                };
                requestQueue.add(jsonRequest);


            }
        });

        logIn = (TextView) findViewById(R.id.textLogIn);
        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

    }

    private boolean isValidPassword(String password_txt) {
        Pattern PASSWORD_PATTERN
                = Pattern.compile(
                "[a-zA-Z0-9]{2,24}");

        return !TextUtils.isEmpty(password_txt) && PASSWORD_PATTERN.matcher(password_txt).matches();
    }


}