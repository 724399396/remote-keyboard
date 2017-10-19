package com.weili.remotekeyboardclient;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity {
    SocketService mService;
    boolean mBound = false;
    AtomicInteger count = new AtomicInteger(0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final HashMap<Integer, String> idStringHashMap = new HashMap<>();
        idStringHashMap.put(R.id.sBackward, "sBackward\n");
        idStringHashMap.put(R.id.mBackward, "mBackward\n");
        idStringHashMap.put(R.id.lBackward, "lBackward\n");

        idStringHashMap.put(R.id.sForward, "sForward\n");
        idStringHashMap.put(R.id.mForward, "mForward\n");
        idStringHashMap.put(R.id.lForward, "lForward\n");

        idStringHashMap.put(R.id.previous, "previous\n");
        idStringHashMap.put(R.id.play, "play\n");
        idStringHashMap.put(R.id.next, "next\n");

        idStringHashMap.put(R.id.full, "full\n");

        for (final Integer id: idStringHashMap.keySet()) {
            findViewById(id).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mBound) {
                        mService.send(idStringHashMap.get(id));
                    }
                }
            });
        }
        findViewById(R.id.shutdown).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBound && count.addAndGet(1) > 5) {
                    mService.send("shutdown\n");
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, SocketService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            SocketService.SocketBinder binder = (SocketService.SocketBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };
}
