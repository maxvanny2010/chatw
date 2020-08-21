package ch.brains.network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.StringJoiner;

/**
 * TCPConnection.
 *
 * @author Maxim Vanny
 * @version 5.0
 * @since 8/21/2020
 */
public class TCPConnection {
    private final Socket socket;
    private final BufferedReader in;
    private final BufferedWriter out;
    private final TCPConnectionListener eventListener;
    private Thread rxThread;

    public TCPConnection(final TCPConnectionListener eventListener, final String ipAddr, final int port) throws IOException {
        this(eventListener, new Socket(ipAddr, port));
    }

    public TCPConnection(final TCPConnectionListener eventListener, final Socket socket) throws IOException {
        this.socket = socket;
        this.eventListener = eventListener;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        this.rxThread = new Thread(() -> {
            try {
                this.eventListener.onConnectionReady(TCPConnection.this);
                while (!this.rxThread.isInterrupted()) {
                    this.eventListener.onReceiveString(TCPConnection.this, this.in.readLine());
                }
                this.in.readLine();
            } catch (IOException e) {
                this.eventListener.onException(TCPConnection.this, e);
            } finally {
                this.eventListener.onDisconnect(TCPConnection.this);
            }
        });
        this.rxThread.start();
    }

    public synchronized void sendString(final String value) {
        try {
            this.out.write(value + "\r\n");
            this.out.flush();
        } catch (IOException e) {
            this.eventListener.onException(TCPConnection.this, e);
            this.disconnect();
        }
    }

    public synchronized void disconnect() {
        this.rxThread.interrupt();
        try {
            this.socket.close();
        } catch (IOException e) {
            this.eventListener.onException(TCPConnection.this, e);
        }

    }

    @Override
    public String toString() {
        return new StringJoiner("::", TCPConnection.class.getSimpleName()
                + "[", "]")
                .add(this.socket.getInetAddress().toString())
                .add(String.valueOf(this.socket.getPort()))
                .toString();
    }
}
