package com.weili.remotekeyboardclient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

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
                    new Thread(new SocketSender(idStringHashMap.get(id))).start();
                }
            });
        }
    }
}
