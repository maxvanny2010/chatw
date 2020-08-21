package ch.brains.chat.server;

import ch.brains.network.TCPConnection;
import ch.brains.network.TCPConnectionListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

/**
 * ChatServer.
 *
 * @author Maxim Vanny
 * @version 5.0
 * @since 8/21/2020
 */
public class ChatServer implements TCPConnectionListener {
    private final ArrayList<TCPConnection> connections = new ArrayList<>();

    private ChatServer() {
        System.out.println("Server running...");
        try (final ServerSocket server = new ServerSocket(8000)) {
            while (true) {
                try {
                    new TCPConnection(ChatServer.this, server.accept());
                } catch (IOException e) {
                    System.out.println("TCPConnection exception");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(final String[] args) {
        new ChatServer();
    }

    @Override
    public synchronized void onConnectionReady(final TCPConnection tcpConnection) {
        this.connections.add(tcpConnection);
        this.sentToAllConnection("Client connected: " + tcpConnection);
    }

    @Override
    public synchronized void onReceiveString(final TCPConnection tcpConnection, final String value) {
        this.sentToAllConnection(value);

    }

    @Override
    public synchronized void onDisconnect(final TCPConnection tcpConnection) {
        this.connections.remove(tcpConnection);
        this.sentToAllConnection("Client disconnected: " + tcpConnection);

    }

    @Override
    public synchronized void onException(final TCPConnection tcpConnection, final Exception e) {

    }

    private void sentToAllConnection(final String value) {
        System.out.println(value);
        this.connections.forEach(i -> i.sendString(value));
    }
}
