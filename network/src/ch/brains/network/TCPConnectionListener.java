package ch.brains.network;

/**
 * TCPConnectionListener.
 *
 * @author Maxim Vanny
 * @version 5.0
 * @since 8/21/2020
 */
public interface TCPConnectionListener {
    void onConnectionReady(TCPConnection tcpConnection);

    void onReceiveString(TCPConnection tcpConnection, String value);

    void onDisconnect(TCPConnection tcpConnection);

    void onException(TCPConnection tcpConnection, Exception e);
}
