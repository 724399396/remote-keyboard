package com.weili.remotekeyboardclient;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class SocketService extends Service {
    private final SocketBinder binder = new SocketBinder();
    volatile Socket socket = null;

    public class SocketBinder extends Binder {
        SocketService getService() {
            return SocketService.this;
        }
    }

    public void send(String command) {
        new Thread(new SocketRunnable(command)).start();
    }

    @Override
    public void onDestroy() {
        if (socket != null) {
            try {
                Log.i("socket service", "close socket");
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    class SocketRunnable implements Runnable {
        String command;

        public SocketRunnable(String command) {
            this.command = command;
        }

        @Override
        public void run() {
            try {
                if (socket == null) {
                    socket = new Socket("", );
                }
                OutputStream outputStream = socket.getOutputStream();
                outputStream.write(command.getBytes());
                outputStream.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
