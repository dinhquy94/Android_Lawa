package com.example.root.lawa;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import org.json.JSONObject;
import io.socket.client.Socket;

public class LaserFragment extends Fragment {
    private SeekBar laser1;
    private SeekBar laser2;
    private SeekBar laser3;
    private SeekBar laser4;
    public SharedPreferences.Editor editor;
    String token_key;
    Socket mSocket;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        token_key = this.getArguments().getString("token_key");
        return inflater.inflate(R.layout.fragment_laser, container, false);

    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSocket = SocketIO.getSocket();
        //you can set the title for your toolbar here for different fragments different titles
        laser1 = (SeekBar) view.findViewById(R.id.laservalue1);
        laser2 = (SeekBar) view.findViewById(R.id.laservalue2);
        laser3 = (SeekBar) view.findViewById(R.id.laservalue3);
        laser4 = (SeekBar) view.findViewById(R.id.laservalue4);
        laser1.incrementProgressBy(2);laser2.incrementProgressBy(2);laser3.incrementProgressBy(2);laser4.incrementProgressBy(2);
        laser1.setMax(10);laser2.setMax(10);laser3.setMax(10);laser4.setMax(10);
        laser1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // Here call your code when progress will changes
                progressChange(seekBar);
            }
        });
        laser4.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // Here call your code when progress will changes
                progressChange(seekBar);
            }
        });
        laser3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // Here call your code when progress will changes
                progressChange(seekBar);
            }
        });
        laser2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // Here call your code when progress will changes
                progressChange(seekBar);
            }
        });
        SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode in this application
        editor = pref.edit();

        int laser1Value = pref.getInt("laser1", 0);
        int laser2Value = pref.getInt("laser2", 0);
        int laser3Value = pref.getInt("laser3", 0);
        int laser4Value = pref.getInt("laser4", 0);
        laser1.setProgress(laser1Value);
        laser2.setProgress(laser2Value);
        laser3.setProgress(laser3Value);
        laser4.setProgress(laser4Value);

        getActivity().setTitle("Công suất laser");
    }
    void progressChange(SeekBar seekBar) {
        int seekbarValue = seekBar.getProgress();
        int value = seekBar.getProgress()*3;
        if (value == 0) value = 1;
        JSONObject data = new JSONObject();
        try {
            data.put("token_key", token_key);
            if(seekBar.getId() == R.id.laservalue1) {
                data.put("laser_num", 1);
                data.put("value", value);
                editor.putInt("laser1", seekbarValue);
                //Log.e("Value1", Integer.toString(value));
            }
            if(seekBar.getId() == R.id.laservalue2) {
                data.put("laser_num", 2);
                data.put("value", value);
                editor.putInt("laser2", seekbarValue);
                //Log.e("Value1", Integer.toString(value));
            }
            if(seekBar.getId() == R.id.laservalue3) {
                data.put("laser_num", 3);
                data.put("value", value);
                editor.putInt("laser3", seekbarValue);
                //Log.e("Value1", Integer.toString(value));
            }
            if(seekBar.getId() == R.id.laservalue4) {
                data.put("laser_num", 4);
                data.put("value", value);
                editor.putInt("laser4", seekbarValue);
                //Log.e("Value1", Integer.toString(value));
            }
            editor.commit();
            mSocket.emit("changeLaserEmitter", data);
            Log.e("Test data", data.toString());
        }catch (Exception e) {

        }

    }

}
