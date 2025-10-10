// UdpDatagramTransport.java
import org.bouncycastle.tls.DatagramTransport;
import java.io.IOException;
import java.net.*;

public class UdpDatagramTransport implements DatagramTransport {
    private static final int MTU = 1500; // limite typique Ethernet
    private final DatagramSocket socket;
    private SocketAddress peer; // fixé côté serveur à la 1ère réception, côté client on le connaît déjà

    public UdpDatagramTransport(DatagramSocket socket, SocketAddress peer) throws SocketException {
        this.socket = socket;
        this.peer = peer;
        // 0 = bloquant (on peut ajuster via receive(waitMillis))
        this.socket.setSoTimeout(0);
    }

    @Override public int getReceiveLimit() { return MTU; }
    @Override public int getSendLimit()    { return MTU; }

    @Override
    public int receive(byte[] buf, int off, int len, int waitMillis) throws IOException {
        if (waitMillis > 0) socket.setSoTimeout(waitMillis);
        DatagramPacket p = new DatagramPacket(buf, off, len);
        socket.receive(p); // BLOQUANT jusqu’à réception
        if (peer == null) {
            // CAS SERVEUR : 1er paquet = on découvre le pair (client)
            peer = p.getSocketAddress();
        }
        // Renvoie la taille utile
        return p.getLength();
    }

    @Override
    public void send(byte[] buf, int off, int len) throws IOException {
        if (peer == null) throw new IOException("Peer not set yet");
        DatagramPacket p = new DatagramPacket(buf, off, len, peer);
        socket.send(p);
    }

    @Override
    public void close() {
        socket.close();
    }
}
