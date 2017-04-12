package com.example.root.lawa;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class DashboadFragment extends Fragment {
        private Timer timer;
        private Socket mSocket;
        private Button buttonThuongLuuState;
        private Button buttonHaLuuState;
        private ImageButton buttonStateIcon;
        private TextView textState;
        private TextView mucnuocTextview;
        private LinearLayout generalState;
        private Switch switch1;
        private Switch switch2;
        private TextView textViewUpdateTime;
        private TextView textViewCau;
    private SharedPreferences.Editor editor;
        private String token_key;
        private void loadDataRepeatly() {
            timer = new Timer();
            timer.scheduleAtFixedRate( new TimerTask() {
                public void run() {
                    try{
                        JSONObject obj = new JSONObject();
                        obj.put("token_key", token_key);
                        mSocket.emit("requestCurrentLaserState", obj);
                        mSocket.emit("requestCurrentState", obj);
                        mSocket.emit("requestWarningStatus", obj);
                    }
                    catch (Exception e) {
                      Log.e("Loi cmnr", e.getMessage().toString());
                    }
                }
            }, 0, 2000);
        }

        @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
           // Log.i("Tham so",this.getArguments().getString("message"));
            token_key = this.getArguments().getString("token_key");
            mSocket = SocketIO.getSocket();
            mSocket.on("respondCurrentLaserState", processCurrentLaserState);
            mSocket.on("respondCurrentState", processCurrentState);
            mSocket.on("respondWarningStatus", processWarningStatus);
            return inflater.inflate(R.layout.fragment_dashboad, container, false);

    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        buttonThuongLuuState = (Button) view.findViewById(R.id.state_thuong_luu);
        buttonHaLuuState = (Button) view.findViewById(R.id.state_ha_luu);;
        buttonStateIcon = (ImageButton) view.findViewById(R.id.state_icon);;
        textState =(TextView) view.findViewById(R.id.state_label);
        generalState = (LinearLayout) view.findViewById(R.id.general_state);
        textViewCau = (TextView) view.findViewById(R.id.textViewCau);
        mucnuocTextview = (TextView) view.findViewById(R.id.mucnuoc_textview);
        switch1 = (Switch) view.findViewById(R.id.switch1);
        buttonHaLuuState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSetSafeButtonClick();
            }
        });
        buttonThuongLuuState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSetSafeButtonClick();
            }
        });
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                JSONObject obj = new JSONObject();
                JSONObject data = new JSONObject();
                try {
                    obj.put("token_key", token_key);
                    data.put("warning_number",1);
                    data.put("value",isChecked);
                    obj.put("data", data);
                    mSocket.emit("requestWarningChangeStatus", obj);
                } catch (Exception e) {

                }
            }
        });
        switch2 = (Switch) view.findViewById(R.id.switch2);
        switch2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                JSONObject obj = new JSONObject();
                JSONObject data = new JSONObject();
                try {
                    obj.put("token_key", token_key);
                    data.put("warning_number", 2);
                    data.put("value",isChecked);
                    obj.put("data", data);
                    mSocket.emit("requestWarningChangeStatus", obj);
                } catch (Exception e) {

                }
            }
        });
        textViewUpdateTime = (TextView) view.findViewById(R.id.textViewUpdateTime);

        loadDataRepeatly();        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Bảng điều khiển");

    }
    private Emitter.Listener processCurrentLaserState = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if (args[0] != null) {
                //Log.e("Testrun", "Tesst here");
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        JSONObject data = (JSONObject) args[0];

                        public void run() {
                            try {
                                boolean Laser1 = data.getBoolean("laser1");
                                boolean Laser2 = data.getBoolean("laser2");
                                boolean Laser3 = data.getBoolean("laser3");
                                boolean Laser4 = data.getBoolean("laser4");
                                boolean generalLaser = true;
                                if (Laser1 && Laser2) {
                                    buttonThuongLuuState.setBackgroundResource(R.color.safeColor);
                                } else {
                                    buttonThuongLuuState.setBackgroundResource(R.color.warnColor);
                                    generalLaser = false;
                                }
                                if (Laser3 && Laser4) {
                                    buttonHaLuuState.setBackgroundResource(R.color.safeColor);
                                } else {
                                    buttonHaLuuState.setBackgroundResource(R.color.warnColor);
                                    generalLaser = false;
                                }
                                if (generalLaser) {
                                    generalState.setBackgroundResource(R.color.safeColor);
                                    textState.setText(getString(R.string.safe_notif));
                                    buttonStateIcon.setBackgroundResource(R.drawable.ic_success);
                                } else {
                                    generalState.setBackgroundResource(R.color.warnColor);
                                    textState.setText(getString(R.string.warn_notif));
                                    buttonStateIcon.setBackgroundResource(R.drawable.ic_error);
                                }
                            } catch (Exception e) {
                                Log.e("Loi", e.getMessage());
                            }
                        }
                    });
                }
            }
        }
    };
    private Emitter.Listener processCurrentState = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if (args[0] != null) {
                Log.e("Testrun", "Tesst here");
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        JSONObject data = (JSONObject) args[0];

                        public void run() {
                            try {
                                JSONObject sys_data = data.getJSONObject("data");
                                String mucnuoc = sys_data.getString("mucnuoc");
                                String tenCau = data.getString("locationname");
                                mucnuocTextview.setText(mucnuoc + " m");
                                textViewCau.setText(tenCau);
                                String time = data.getString("time");
                                //convert unix epoch timestamp (seconds) to milliseconds
                                long timestamp = Long.parseLong(time) * 1000L;
                                textViewUpdateTime.setText(getDate(timestamp));
                            } catch (Exception e) {
                                Log.e("Loi", e.getMessage());
                            }
                        }
                    });
                }
            }
        }
    };
    private Emitter.Listener processWarningStatus = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if (args[0] != null) {
                Log.e("Testrun", "Tesst here");
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        JSONObject data = (JSONObject) args[0];

                        public void run() {
                            try {
                                boolean warningState1 = data.getBoolean("WarningStatus1");
                                boolean warningState2 = data.getBoolean("WarningStatus2");
                                switch1.setChecked(warningState1);
                                switch2.setChecked(warningState2);
                            } catch (Exception e) {
                                Log.e("Loi", e.getMessage());
                            }
                        }
                    });
                }
            }
        }
    };
    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
        mSocket.off("respondCurrentLaserState", processCurrentLaserState);
    }
    private String getDate(long timeStamp){

        try{
            DateFormat sdf = new SimpleDateFormat("MM/dd/yyyy h:mm a");
            Date netDate = (new Date(timeStamp));
            return sdf.format(netDate);
        }
        catch(Exception ex){
            return "xx";
        }
    }
    private void handleSetSafeButtonClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Xác nhận");
        builder.setMessage("Bạn có muốn đặt lại an toàn ?");
        builder.setPositiveButton(" Đúng", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("token_key", token_key);
                    mSocket.emit("setCurrentSafe", obj);
                } catch (Exception e) {
                    Log.e("SetSafeErr", e.getMessage().toString());
                }
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

    }
}
