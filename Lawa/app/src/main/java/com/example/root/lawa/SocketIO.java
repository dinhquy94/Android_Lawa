package com.example.root.lawa;

/**
 * Created by root on 07/04/2017.
 */
import android.util.Log;

import java.net.URISyntaxException;
import java.util.Timer;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
public class SocketIO {
    private static final SocketIO ourInstance = new SocketIO();
    private static Socket mSocket;

    public static SocketIO getInstance() {
        return ourInstance;
    }

    private static String _socketId;

    public static String getSocketId() {
        return _socketId;
    }

    private SocketIO() {
        try {
            IO.Options options = new IO.Options();
            mSocket = IO.socket("http://125.212.207.51:18082");
            mSocket.on(Socket.EVENT_CONNECT,onConnect);
            mSocket.connect();
            //Log.e("testLoi", mSocket.id().toString());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static Socket getSocket() {
        return mSocket;
    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
           _socketId = mSocket.id().toString();
        }
    };
}
