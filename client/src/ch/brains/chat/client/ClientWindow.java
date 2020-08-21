package ch.brains.chat.client;

import ch.brains.network.TCPConnection;
import ch.brains.network.TCPConnectionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * ClientWindow.
 *
 * @author Maxim Vanny
 * @version 5.0
 * @since 8/21/2020
 */
public class ClientWindow extends JFrame implements ActionListener, TCPConnectionListener {
    private static final String IP_ADDR = "127.0.0.1";
    private static final int PORT = 8000;
    private static final int WIDTH = 600;
    private static final int HEIGHT = 400;
    private final JTextArea log = new JTextArea();
    private final JTextField fieldNickName = new JTextField("Max");
    private final JTextField fieldInput = new JTextField();
    private TCPConnection connection;
    private ClientWindow() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);
        setVisible(true);
        log.setEditable(false);
        log.setLineWrap(true);
        add(log, BorderLayout.CENTER);
        fieldInput.addActionListener(this);
        add(fieldInput, BorderLayout.SOUTH);
        add(fieldNickName, BorderLayout.NORTH);
        try {
            this.connection = new TCPConnection(this, IP_ADDR, PORT);
        } catch (IOException e) {
            this.printMsg("Connection exception: " + e);
        }
    }

    public static void main(final String[] args) {
        SwingUtilities.invokeLater(ClientWindow::new);

    }

    @Override
    public void actionPerformed(final ActionEvent e) {
        final String text = this.fieldInput.getText();
        if (text.equals("")) {
            return;
        }
        this.fieldInput.setText(null);
        this.connection.sendString(this.fieldNickName.getText() + ":" + text);
    }

    @Override
    public void onConnectionReady(final TCPConnection tcpConnection) {
        this.printMsg("Connection ready...");
    }

    @Override
    public void onReceiveString(final TCPConnection tcpConnection, final String value) {
        this.printMsg(value);
    }

    @Override
    public void onDisconnect(final TCPConnection tcpConnection) {
        this.printMsg("Connection close");
    }

    @Override
    public void onException(final TCPConnection tcpConnection, final Exception e) {
        this.printMsg("Connection exception: " + e);
    }

    private synchronized void printMsg(final String msg) {
        SwingUtilities.invokeLater(() -> {
            this.log.append(msg + "\n");
            this.log.setCaretPosition(this.log.getDocument().getLength());
        });
    }
}
