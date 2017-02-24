package com.example.pulsar_san.mywebtest;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    EditText emailText;
    TextView responseView;
    ProgressBar progressBar;
    static final String API_KEY = "YOUR_API_KEY"; // Remplacer YOUR_API_KEY par la clé qu'on voua donné
    static final String API_DOM = "https://www.fanfic-fr.org/";
    /*static final String API_URL = "https://www.fanfic-fr.org/api/livre/20894/chapitres";*/
    static CookieManager cookieManager = new CookieManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        responseView = (TextView) findViewById(R.id.responseView);
//        emailText = (EditText) findViewById(R.id.emailText);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        Button loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LogInTask().execute();
            }
        });

        Button logoutButton = (Button) findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LogOutTask().execute();
            }
        });

        Button quitterButton = (Button) findViewById(R.id.quitterButton);
        quitterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new quitterTask().execute();
            }
        });

        Button queryButton = (Button) findViewById(R.id.queryButton);
        queryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RetrieveCategoriesTask().execute();
            }
        });
    }

    class LogInTask extends AsyncTask<Void, Void, String> {
        private Exception exception;
        static final String COOKIES_HEADER = "Set-Cookie";
        static final String API_URL = "https://www.fanfic-fr.org/api/ident";

        EditText identifiant = (EditText)findViewById(R.id.identifiant);
        String ident = identifiant.getText().toString();
        EditText password = (EditText)findViewById(R.id.password);
        String mdp = password.getText().toString();
        String postParameters = "ident="+ident+"&mdp="+mdp;

        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            responseView.setText("");
        }

        protected String doInBackground(Void... urls) {

            try {
                URL url = new URL(API_URL + "?apik=" + API_KEY);

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
                CookieHandler.setDefault(cookieManager);

                if (cookieManager.getCookieStore().getCookies().size() > 0) {
                    Log.d("COOKIES1", String.valueOf(cookieManager.getCookieStore().getCookies()));
                    // While joining the Cookies, use ',' or ';' as needed. Most of the servers are using ';'
                    urlConnection.setRequestProperty("Cookie", TextUtils.join(";",  cookieManager.getCookieStore().getCookies()));
                }

                urlConnection.setFixedLengthStreamingMode(postParameters.getBytes().length);
                PrintWriter out = new PrintWriter(urlConnection.getOutputStream());

                out.print(postParameters);
                out.close();
                urlConnection.connect();
                Map<String, List<String>> headerFields = urlConnection.getHeaderFields();
                List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);

                if (cookiesHeader != null) {
                    for (String cookie : cookiesHeader) {
                        cookieManager.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
                    }
                    Log.d("COOKIES0", String.valueOf(cookieManager.getCookieStore().getCookies()));
                }
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            if(response == null) {
                response = "THERE WAS AN ERROR";
            }
            progressBar.setVisibility(View.GONE);
            Log.i("INFO", response);
            responseView.setText(response);
            // TODO: check this.exception
            // TODO: do something with the feed

//            try {
//                JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
//                String requestID = object.getString("requestId");
//                int likelihood = object.getInt("likelihood");
//                JSONArray photos = object.getJSONArray("photos");
//                .
//                .
//                .
//                .
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
        }
    }

    class LogOutTask extends AsyncTask<Void, Void, String> {
        private Exception exception;
        static final String COOKIES_HEADER = "Set-Cookie";
        static final String API_URL = "https://www.fanfic-fr.org/api/logout";

        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            responseView.setText("");
        }

        protected String doInBackground(Void... urls) {
            try {
                URL url = new URL(API_URL + "?apik=" + API_KEY);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
                CookieHandler.setDefault(cookieManager);

                if (cookieManager.getCookieStore().getCookies().size() > 0) {
                    Log.d("COOKIES1", String.valueOf(cookieManager.getCookieStore().getCookies()));
                    // While joining the Cookies, use ',' or ';' as needed. Most of the servers are using ';'
                    urlConnection.setRequestProperty("Cookie", TextUtils.join(";",  cookieManager.getCookieStore().getCookies()));
                }
                Map<String, List<String>> headerFields = urlConnection.getHeaderFields();
                List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);

                if (cookiesHeader != null) {
                    for (String cookie : cookiesHeader) {
                        cookieManager.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
                    }
                    Log.d("COOKIES0", String.valueOf(cookieManager.getCookieStore().getCookies()));
                }
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            if(response == null) {
                response = "THERE WAS AN ERROR";
            }
            progressBar.setVisibility(View.GONE);
            Log.i("INFO", response);
            responseView.setText(response);
            // TODO: check this.exception
            // TODO: do something with the feed

//            try {
//                JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
//                String requestID = object.getString("requestId");
//                int likelihood = object.getInt("likelihood");
//                JSONArray photos = object.getJSONArray("photos");
//                .
//                .
//                .
//                .
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
        }
    }

    class quitterTask extends AsyncTask<Void, Void, String> {
        private Exception exception;
        static final String COOKIES_HEADER = "Set-Cookie";
        static final String API_URL = "https://www.fanfic-fr.org/api/quitter";

        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            responseView.setText("");
        }

        protected String doInBackground(Void... urls) {
            try {
                URL url = new URL(API_URL + "?apik=" + API_KEY);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("DELETE");
                cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
                CookieHandler.setDefault(cookieManager);

                if (cookieManager.getCookieStore().getCookies().size() > 0) {
                    Log.d("COOKIES1", String.valueOf(cookieManager.getCookieStore().getCookies()));
                    // While joining the Cookies, use ',' or ';' as needed. Most of the servers are using ';'
                    urlConnection.setRequestProperty("Cookie", TextUtils.join(";",  cookieManager.getCookieStore().getCookies()));
                }
                Map<String, List<String>> headerFields = urlConnection.getHeaderFields();
                List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);

                if (cookiesHeader != null) {
                    for (String cookie : cookiesHeader) {
                        cookieManager.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
                    }
                    Log.d("COOKIES0", String.valueOf(cookieManager.getCookieStore().getCookies()));
                }
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                    if (cookieManager.getCookieStore().getCookies().size() > 0) {
                        Log.d("COOKIES_NULL", String.valueOf(cookieManager.getCookieStore().getCookies()));
                    }
                    final CookieHandler cookieHandler = CookieHandler.getDefault();
                    if (cookieHandler instanceof CookieManager) {
                        ((CookieManager)cookieHandler).getCookieStore().removeAll();
                    }
                    //if (cookieManager.getCookieStore().getCookies().size() > 0) {
                    Log.d("COOKIES_NULL_AFTER", String.valueOf(cookieManager.getCookieStore().getCookies()));
                    //}
                    finish();
                }
                //return "";
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            if(response == null) {
                response = "THERE WAS AN ERROR";
            }
            progressBar.setVisibility(View.GONE);
            Log.i("INFO", response);
            responseView.setText(response);
            // TODO: check this.exception
            // TODO: do something with the feed
        }
    }

    class RetrieveCategoriesTask extends AsyncTask<Void, Void, String> {

        private Exception exception;
        static final String COOKIES_HEADER = "Set-Cookie";
        static final String API_URL = "https://www.fanfic-fr.org/api/categories";

        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            responseView.setText("");
        }

        protected String doInBackground(Void... urls) {

            try {
                URL url = new URL(API_URL + "?apik=" + API_KEY);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
                CookieHandler.setDefault(cookieManager);

                if (cookieManager.getCookieStore().getCookies().size() > 0) {
                    Log.d("COOKIES1", String.valueOf(cookieManager.getCookieStore().getCookies()));
                    // While joining the Cookies, use ',' or ';' as needed. Most of the servers are using ';'
                    urlConnection.setRequestProperty("Cookie", TextUtils.join(";",  cookieManager.getCookieStore().getCookies()));
                }
                Map<String, List<String>> headerFields = urlConnection.getHeaderFields();
                List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);

                if (cookiesHeader != null) {
                    for (String cookie : cookiesHeader) {
                        cookieManager.getCookieStore().add(null, HttpCookie.parse(cookie).get(0));
                    }
                    Log.d("COOKIES0", String.valueOf(cookieManager.getCookieStore().getCookies()));
                }
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            if(response == null) {
                response = "THERE WAS AN ERROR";
            }
            progressBar.setVisibility(View.GONE);
            Log.i("INFO", response);
            responseView.setText(response);
            // TODO: check this.exception
            // TODO: do something with the feed

//            try {
//                JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
//                String requestID = object.getString("requestId");
//                int likelihood = object.getInt("likelihood");
//                JSONArray photos = object.getJSONArray("photos");
//                .
//                .
//                .
//                .
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
        }
    }
}