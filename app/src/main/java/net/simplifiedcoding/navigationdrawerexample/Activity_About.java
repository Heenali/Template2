package net.simplifiedcoding.navigationdrawerexample;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Heenali on 15/4/2019.
 */

public class Activity_About extends AppCompatActivity {

    ImageView back_btn;
    KProgressHUD hud;
    WebView web;
    SessionManager sm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            getWindow().setStatusBarColor(ContextCompat.getColor(Activity_About.this, R.color.appcolor2));
        }
        back_btn=(ImageView)findViewById(R.id.back_btn);
        web=(WebView) findViewById(R.id.tv_about);
        web.getSettings();
        web.setBackgroundColor(Color.TRANSPARENT);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        sm = new SessionManager(Activity_About.this);
        initWebView();
        refresh();
       // reg_call();



    }
    private void initWebView() {
        web.setWebChromeClient(new MyWebChromeClient(this));
        web.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                /**
                 * Check for the url, if the url is from same domain
                 * open the url in the same activity as new intent
                 * else pass the url to browser activity
                 * */

                openInAppBrowser(url);


                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // progressBar.setVisibility(View.GONE);
            }
        });
        web.clearCache(true);
        web.clearHistory();
        web.getSettings().setJavaScriptEnabled(true);
        web.setHorizontalScrollBarEnabled(false);

    }
    private void openInAppBrowser(String url)
    {
        try
        {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        }
        catch (Exception e)
        {


        }

    }
    private class MyWebChromeClient extends WebChromeClient {
        Context context;

        public MyWebChromeClient(Context context) {
            super();
            this.context = context;
        }


    }
    public  void refresh()
    {
        hud = KProgressHUD.create(Activity_About.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setWindowColor(getResources().getColor(R.color.appcolor1))
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        RequestQueue queue = Volley.newRequestQueue(Activity_About.this);

        String url = "http://templateapp.talenhosting.com/api/user/refresh";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the response string.
                        // _response.setText(response);
                        hud.dismiss();
                        try
                        {
                            JSONObject jobj = new JSONObject(response);
                            String status = jobj.getString("status");


                            if(status.equalsIgnoreCase("true"))
                            {
                                JSONObject object = jobj.getJSONObject("data");
                                String mess = object.getString("token");
                                sm.createLoginSession(mess);
                                showJSON();
                                //Toast.makeText(getApplicationContext(),mess,Toast.LENGTH_LONG).show();

                                //mess(mess);
                                // finish();

                            }
                            else
                            {
                                JSONObject object = jobj.getJSONObject("data");
                                try
                                {
                                    String mess = object.getString("message");
                                    sm.logoutUser();
                                    finish();
                                    Intent i=new Intent(getApplicationContext(),Activity_Login.class);
                                    startActivity(i);
                                    Toast.makeText(getApplicationContext(),mess,Toast.LENGTH_LONG).show();
                                }
                                catch (Exception e)
                                {

                                }



                            }
                        }
                        catch (Exception e)
                        {

                           // mess("Something Wrong");
                        }


                    }
                }, new Response.ErrorListener() {



            @Override
            public void onErrorResponse(VolleyError error)
            {
                //_response.setText("That didn't work!");
                hud.dismiss();
               // mess("That didn't work!");
            }
        }) {
            //adding parameters to the request
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("Authorization","Bearer "+sm.getUserDetails());
                return params;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> params = new HashMap<>();

                return params;
            }
        };
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void showJSON()
    {
        hud = KProgressHUD.create(Activity_About.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setWindowColor(getResources().getColor(R.color.appcolor1))
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        RequestQueue queue = Volley.newRequestQueue(Activity_About.this);
        JsonObjectRequest jRequest = new JsonObjectRequest(Request.Method.GET, "http://templateapp.talenhosting.com/api/setting/", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        hud.dismiss();
                        Log.e("fffff",response.toString());
                        //Toast.makeText(getApplicationContext(),response.toString(),Toast.LENGTH_LONG).show();
                        try
                        {
                            JSONObject jobj = new JSONObject(response+"");
                            String status = response.getString("status");


                            if(status.equalsIgnoreCase("true"))
                            {
                                JSONArray jsonArray = jobj.optJSONArray("data");
                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                String mess = jsonObject.getString("value");
                                String message ="<font color='white'>"+""+ "" +"<font color='white'>"+"<font size='3'>"+mess+"</font>";
                               web.loadDataWithBaseURL("", message, "text/html", "UTF-8", "");

                                //Toast.makeText(getApplicationContext(),mess,Toast.LENGTH_LONG).show();

                                //mess(mess);
                                // finish();

                            }
                            else
                            {
                                JSONObject object = jobj.getJSONObject("data");
                                try
                                {
                                    String mess = object.getString("message");
                                    sm.logoutUser();
                                    finish();
                                    Intent i=new Intent(getApplicationContext(),Activity_Login.class);
                                    startActivity(i);
                                    Toast.makeText(getApplicationContext(),mess,Toast.LENGTH_LONG).show();
                                }
                                catch (Exception e)
                                {

                                }



                            }
                        }
                        catch (Exception e)
                        {

                            // mess("Something Wrong");
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                })

        {
            @Override
            protected VolleyError parseNetworkError(VolleyError volleyError)
            {
                hud.dismiss();
                Log.d("", "volleyError" + volleyError.getMessage());
                return super.parseNetworkError(volleyError);
            }


            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String text = "key";

                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization","Bearer "+sm.getUserDetails());

                return headers;
            }
        };
        queue.add(jRequest);
       // Toast.makeText(getApplicationContext(),json+"",Toast.LENGTH_LONG).show();
    }
}

