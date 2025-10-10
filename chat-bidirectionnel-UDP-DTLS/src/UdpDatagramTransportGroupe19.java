/** Costa MASKULOV THOME Blanche bintou fané**/
/** Groupe: 19 **/
/** Port: 41000 **/

import org.bouncycastle.tls.DatagramTransport;
import java.io.IOException;
import java.net.*;

public class UdpDatagramTransportGroupe19 implements DatagramTransport {
    private static final int MTU = 1500;  // Taille “max” d’un datagramme
    private final DatagramSocket socket;     // Socket UDP Java
    private SocketAddress peer;  // Adresse:port du pair

    public UdpDatagramTransportGroupe19(DatagramSocket socket, SocketAddress peer) throws SocketException {
        this.socket = socket;
        this.peer = peer;               // côté client: déjà connu // côté serveur: sera fixé au 1er paquet
        this.socket.setSoTimeout(0); 
    }

    @Override public int getReceiveLimit() { return MTU; } // Limite recommandée en réception pour BC
    @Override public int getSendLimit()    { return MTU; } // Limite recommandée en émission pour BC

    @Override
    public int receive(byte[] buf, int off, int len, int waitMillis) throws IOException { 
        if (waitMillis > 0) socket.setSoTimeout(waitMillis); // Si demandé, applique un timeout temporaire
        DatagramPacket p = new DatagramPacket(buf, off, len); // Prépare un paquet tamponné sur buf[off..off+len[
        socket.receive(p);
        if (peer == null) {
            peer = p.getSocketAddress(); // côté serveur: découvre le pair au 1er paquet
        }
        return p.getLength(); // Nombre d’octets utiles reçus
    }

    @Override
    public void send(byte[] buf, int off, int len) throws IOException {
        if (peer == null) throw new IOException("Peer not set"); // Sécurité: impossible d’envoyer si on ne connaît pas le pair
        DatagramPacket p = new DatagramPacket(buf, off, len, peer);   // Construit le datagramme à destination du pair
        socket.send(p); // Envoie
    }

    @Override
    public void close() { socket.close(); }  // Ferme la socket UDP
}
