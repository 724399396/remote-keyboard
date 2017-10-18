package com.weili.remotekeyboardclient;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by weili on 17-10-17.
 */

public class SocketSender implements Runnable {
    private final String command;

    public SocketSender(String command) {
        this.command = command;
    }

    @Override
    public void run() {
        try (Socket socket = new Socket("192.168.1.12", 6666)) {
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(command.getBytes());
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
