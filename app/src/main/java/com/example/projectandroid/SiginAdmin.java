package com.example.projectandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class SiginAdmin extends AppCompatActivity {
    private EditText Password, Email;
    private Intent intent;
    private String usernameh;
    private Button signin;
    private String pass, mail;
    public static final String NAME = "NAMEAdmin";
    public static final String PASS = "PASSAdmin";
    public static final String FLAG = "FLAGAdmin";
    private boolean flag = false;
    private CheckBox chk;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_sigin_admin);
        setup();
        setupSharedPrefs();
        checkPrefs();

    }

    private void checkPrefs() {
        flag = prefs.getBoolean(FLAG, false);

        if(flag){
            String name = prefs.getString(NAME, "");
            String password = prefs.getString(PASS, "");
            Email.setText(name);
            Password.setText(password);
            chk.setChecked(true);
        }
    }

    private void setupSharedPrefs() {
        prefs= PreferenceManager.getDefaultSharedPreferences(this);
        editor = prefs.edit();
    }

    public void btnLoginOnClick12(View view) {
        String name = Email.getText().toString();
        String password = Password.getText().toString();

        if(chk.isChecked()){
            if(!flag) {
                editor.putString(NAME, name);
                editor.putString(PASS, password);
                editor.putBoolean(FLAG, true);
                editor.commit();
            }

        }

    }


    public void SingUpAdmin(View view) {
        Intent intent = new Intent(this, SiginUpAdmin.class);
        startActivity(intent);
    }

    public void gotoUser(View view){
        Intent intent = new Intent(this, SignIn.class);
        startActivity(intent);

    }

    public void setup() {
        Password = findViewById(R.id.passwordadmin);
        Email = findViewById(R.id.adminemail);
        signin = findViewById(R.id.signinadmin);
        chk = findViewById(R.id.chk2);
    }

    public void loginuser(View view) {
        mail = Email.getText().toString();
        pass = Password.getText().toString();
        if (valdate()) {
            LoginTask loginTask = new LoginTask();
            loginTask.execute(mail, pass);
        }
    }

    public boolean valdate() {
        String Em = Email.getText().toString();
        String Pass = Password.getText().toString();

        if (Em.isEmpty()) {
            Email.setError("Email is required");
            return false;
        }
        if (Pass.isEmpty()) {
            Password.setError("Password is required");
            return false;
        }

        return true;
    }

    private class LoginTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String email = params[0];
            String password = params[1];
            String url = "http://10.0.2.2:80/android/loginAdmin.php" + "?email=" + email + "&password=" + password;

            RequestQueue requestQueue = Volley.newRequestQueue(SiginAdmin.this);
            final String[] responseMessage = new String[1];
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            responseMessage[0] = response;

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            responseMessage[0] = error.toString();
                        }
                    });

            requestQueue.add(stringRequest);

            while (responseMessage[0] == null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return responseMessage[0];
        }

        @Override
        protected void onPostExecute(String response ) {
            Toast.makeText(SiginAdmin.this, response, Toast.LENGTH_SHORT).show();
            if (response.equals("Login successful.")) {
                intent = new Intent(SiginAdmin.this, AdminPage.class);
                intent.putExtra("username", getusername(mail));
                startActivity(intent);
            }else {
                Toast.makeText(SiginAdmin.this, "Login failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class GetUsernameTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String email = params[0];

            String url = "http://10.0.2.2:80/android/adminusername.php" + "?email=" + email;

            RequestQueue requestQueue = Volley.newRequestQueue(SiginAdmin.this);
            final String[] result = new String[1];
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            result[0] = response.trim();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Handle errors
                            result[0] = error.toString();
                        }
                    });

            requestQueue.add(stringRequest);

            // Wait for the response
            while (result[0] == null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return result[0];
        }

        @Override
        protected void onPostExecute(String response) {
            if (response != null && !response.isEmpty()) {
                usernameh = response;
                System.out.println("Username: " + usernameh);
                intent.putExtra("username", usernameh);
                startActivity(intent);
            } else {
                Toast.makeText(SiginAdmin.this, "Failed to get username", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public String getusername(String email) {
        GetUsernameTask getUsernameTask = new GetUsernameTask();
        getUsernameTask.execute(email);
        return usernameh;
    }
}
