package com.example.root.lawa;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import java.net.URI;
import java.net.URISyntaxException;


import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    public ProgressBar Loging;
    private Button btnLogin;
    private EditText txtUsername;
    private EditText txtPassword;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //pref
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode in this application
        editor = pref.edit();
        editor.putString("token_key", "");
        editor.putBoolean("isLogin", false);
        editor.commit();
        mSocket.on(Socket.EVENT_CONNECT,onConnect);
        Loging = (ProgressBar) findViewById(R.id.progressBar);
        btnLogin = (Button) findViewById(R.id.buttonlogin);
        txtUsername = (EditText) findViewById(R.id.txtUsername);
        txtPassword = (EditText) findViewById(R.id.txtPassword );
        //doSomethingRepeatedly();

        txtUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        txtPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtUsername.getText().toString().length() < 4) {
                    txtUsername.setError("Username minimize 4 characters");
                } else if (txtPassword.getText().toString().length() < 6) {
                    txtPassword.setError("Password minimize 5 charaters");
                }else {
                    Toast.makeText(getApplicationContext(), "Logging...",
                            Toast.LENGTH_LONG).show();
                    Loging.setVisibility(View.VISIBLE);
                    JSONObject obj = new JSONObject();
                    try {
                        obj.put("username", txtUsername.getText().toString());
                        obj.put("password", txtPassword.getText().toString());
                        obj.put("clientid", SocketIO.getSocketId());
                        Log.i("SocketID", SocketIO.getSocketId());
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    mSocket.emit("Logindata", obj);
                }
            }
        });
        Loging.setVisibility(View.GONE);

    }

    public Socket getSocket() {
        return mSocket;
    }
    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

        }
    };
    private Emitter.Listener onLogin = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
           //Log.i("tests", args[0].toString());
            new Thread() {
                public void run() {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (args[0].toString().length() < 5) {
                                    Loging.setVisibility(View.GONE);
                                    Toast.makeText(getApplicationContext(), "Login Fail !",
                                            Toast.LENGTH_LONG).show();
                                }else {
                                     //hideKeyboard(findViewById(R.id.txtPassword));
                                     Toast.makeText(getApplicationContext(), "Login success !",
                                            Toast.LENGTH_LONG).show();
                                     Intent intent=new Intent(MainActivity.this,Dashboard.class); // redirecting to LoginActivity.
                                     startActivity(intent);
                                        Log.i("tess", "112233");
                                     editor.putString("token_key", args[0].toString());
                                     editor.putBoolean("isLogin", true);
                                     editor.commit();
                                     finish();

                                }
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), "Error [404]",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }.start();
        }
    };
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    private final static Socket mSocket = SocketIO.getSocket();
    {
        mSocket.on("login_result", onLogin);
     }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocket.off("login_result", onLogin);
    }

}
