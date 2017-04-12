package com.example.root.lawa;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.content.SharedPreferences;

import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class Dashboard extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    Socket mSocket;
    private String token_key;
    private Boolean isLogin;
    private Timer timer;
    private int currentFragment;
    public SharedPreferences.Editor editor;

    void Dashboard() {
        mSocket.on("isloginresult", processLogin);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        timer = new Timer();
        mSocket = SocketIO.getSocket();
        setContentView(R.layout.activity_dashboard);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode in this application
        editor = pref.edit();
        try {
            token_key = pref.getString("token_key", null);
            isLogin = pref.getBoolean("isLogin", false);
        } catch (Exception e) {
            Log.i("Err",e.getMessage().toString());
        }
        checkLoginRepeatedly();
       // Log.i("isLogin", isLogin.toString());
       // Log.i("token_key", token_key.toString());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
         setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
     //   Fragment fragment = (Fragment) new DashboadFragment();
       // FragmentManager  fragmentManager = getSupportFragmentManager();
      //  fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
        currentFragment = pref.getInt("currentFragment", R.id.nav_dashboard);
        replaceFragment(currentFragment);
        navigationView.getMenu().getItem(0).setChecked(true);
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @Override

    public boolean onCreateOptionsMenu(Menu menu) {
// Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mymenu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (item.getItemId()) {
            case android.R.id.home:
                //Write your logic here
                this.finish();
                return true;
            case R.id.logout_menu:
                AlertDialog.Builder builder = new AlertDialog.Builder(Dashboard.this);
                builder.setTitle("Xác nhận");
                builder.setMessage("Bạn có muốn đăng xuất ?");
                builder.setPositiveButton(" Đúng", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        requireLogin();
                        Toast.makeText(getApplicationContext(),"Login required !", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        replaceFragment(id);
        item.setChecked(true);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    void replaceFragment(int id) {
        Fragment fragment = null;
        Class fragmentClass;
        fragmentClass = DashboadFragment.class;
        editor.putInt("currentFragment", id);
        editor.commit();
        if (id == R.id.nav_dashboard) {
            fragmentClass = DashboadFragment.class;
        } else if (id == R.id.nav_camera) {
            fragmentClass = CameraFragment.class;
        } else if (id == R.id.nav_laser) {
            fragmentClass = LaserFragment.class;
        } else if (id == R.id.nav_analystic) {
            fragmentClass = AnalysticFragment.class;
        }else if(id == R.id.nav_led) {
            fragmentClass = LedPanelFragment.class;
        }else {
            fragmentClass = DashboadFragment.class;
        }
        // Handle the action
        try {
            fragment = (Fragment) fragmentClass.newInstance();
            FragmentManager  fragmentManager = getSupportFragmentManager();
            Bundle bundle = new Bundle();
            bundle.putString("token_key", token_key );
            fragment.setArguments(bundle);
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    void requireLogin(){
        Intent intent=new Intent(Dashboard.this,MainActivity.class); // redirecting to LoginActivity.
        startActivity(intent);
        editor.putString("token_key", "");
        editor.putBoolean("isLogin", false);
        editor.commit();
        finish();
    }
    private Emitter.Listener processLogin = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if (args[0] == null) {
                requireLogin();
            }
        }
    };
    private void checkLoginRepeatedly() {
        timer.scheduleAtFixedRate( new TimerTask() {
            public void run() {
                JSONObject obj = new JSONObject();
                try{
                        obj.put("token_key", token_key);
                        mSocket.emit("isLogin", obj);
                }
                catch (Exception e) {

                }

            }
        }, 0, 2000);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocket.off("isloginresult", processLogin);
    }
}