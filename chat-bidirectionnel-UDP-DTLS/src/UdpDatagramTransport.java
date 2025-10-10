/** Costa MASKULOV THOME Blanche bintou fané**/
/** Groupe: 19 **/
/** Port: 41000 **/

import org.bouncycastle.tls.DatagramTransport;
import java.io.IOException;
import java.net.*;

public class UdpDatagramTransport implements DatagramTransport {
    private static final int MTU = 1500;
    private final DatagramSocket socket;
    private SocketAddress peer;

    public UdpDatagramTransport(DatagramSocket socket, SocketAddress peer) throws SocketException {
        this.socket = socket;
        this.peer = peer;               // côté client: déjà connu ; côté serveur: sera fixé au 1er paquet
        this.socket.setSoTimeout(0);    // bloquant par défaut
    }

    @Override public int getReceiveLimit() { return MTU; }
    @Override public int getSendLimit()    { return MTU; }

    @Override
    public int receive(byte[] buf, int off, int len, int waitMillis) throws IOException {
        if (waitMillis > 0) socket.setSoTimeout(waitMillis);
        DatagramPacket p = new DatagramPacket(buf, off, len);
        socket.receive(p);
        if (peer == null) {
            peer = p.getSocketAddress(); // côté serveur: découvre le pair au 1er paquet
        }
        return p.getLength();
    }

    @Override
    public void send(byte[] buf, int off, int len) throws IOException {
        if (peer == null) throw new IOException("Peer not set");
        DatagramPacket p = new DatagramPacket(buf, off, len, peer);
        socket.send(p);
    }

    @Override
    public void close() { socket.close(); }
}
