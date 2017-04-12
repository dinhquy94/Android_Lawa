package com.example.root.lawa;

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

public class LedPanelFragment extends Fragment {

    String token_key;
    Socket mSocket;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        //change R.layout.yourlayoutfilename for each of your fragments
        token_key = this.getArguments().getString("token_key");
        return inflater.inflate(R.layout.fragment_led, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSocket = SocketIO.getSocket();
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Điều khiển biển led");
    }


}
