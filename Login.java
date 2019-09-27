package com.example.thegreatmugwump.taskmanager;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    private String server_response = "";
    private TextView name;
    private TextView password;
    private Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Define UI elements
        name = findViewById(R.id.LoginName);
        password = findViewById(R.id.LoginPassword);
        login = findViewById(R.id.LoginButton);

        login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                new submitAuth().execute();
            }
        });
    }

    private void validate(String sr){// Checks for default password

        if (sr.equals("not valid"))return;

        if (password.getText().toString().equals("1234")){//Default password detected, send user to EditPassword

            Intent i = new Intent(this, EditPassword.class);
            i.putExtra("employeeID",sr);
            startActivity(i);

        }else {//send user to TaskList

            Intent i = new Intent(this, TaskList.class);
            i.putExtra("employeeID", sr);
            startActivity(i);
        }
    }

    private class submitAuth extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {

            URL url;
            Uri.Builder builder = new Uri.Builder();
            String encodedParams;
            HttpURLConnection urlConnection;
            Map<String, String> httpParams = new HashMap<>();

            httpParams.put("user", name.getText().toString());
            httpParams.put("pass", password.getText().toString());

            for (Map.Entry<String, String> entry : httpParams.entrySet()) {
                builder.appendQueryParameter(entry.getKey(), entry.getValue());
            }

            encodedParams =  builder.build().getEncodedQuery();

            try {
                url = new URL("http://174.31.239.5/validateLogin.php/");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("Content-Length", String.valueOf(encodedParams.getBytes().length));
                urlConnection.getOutputStream().write(encodedParams.getBytes());

                int responseCode = urlConnection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {

                    server_response = readStream(urlConnection.getInputStream());
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            super.onPostExecute(s);
            validate(server_response);
        }

        private String readStream(InputStream in) {

            BufferedReader reader = null;
            StringBuffer response = new StringBuffer();

            try {
                reader = new BufferedReader(new InputStreamReader(in));
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

            } catch (IOException e) {
                e.printStackTrace();

            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return response.toString();
        }
    }
}
