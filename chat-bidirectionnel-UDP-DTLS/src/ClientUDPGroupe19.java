/** Costa MASKULOV THOME Blanche bintou fané**/
/** Groupe: 19 **/
/** Port: 41000 **/

import org.bouncycastle.tls.*;
import org.bouncycastle.tls.crypto.TlsCrypto;
import org.bouncycastle.tls.crypto.impl.bc.BcTlsCrypto;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

public class ClientUDPGroupe19 {
    static class MyPskClient extends PSKTlsClient {
        public MyPskClient(TlsCrypto crypto, TlsPSKIdentity pskIdentity) { super(crypto, pskIdentity); } // passe crypto + identité PSK au parent
        @Override protected int[] getSupportedCipherSuites() { return new int[]{ CipherSuite.TLS_PSK_WITH_AES_128_CCM_8 }; }  // suite choisie
        @Override protected ProtocolVersion[] getSupportedVersions() { return new ProtocolVersion[]{ ProtocolVersion.DTLSv12 }; }  // DTLS 1.2
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("Usage: java DTLSPskClient <server_ip> <port>");
            return;
        }
        String host = args[0]; // IP/nom du serveur
        int port = Integer.parseInt(args[1]); // Port du serveur

        // 1) Socket UDP + transport DTLS
        DatagramSocket udp = new DatagramSocket(); // port local éphémère
        udp.connect(new InetSocketAddress(host, port)); // Verrouille le pair côté UDP
        UdpDatagramTransportGroupe19 udpTransport = new UdpDatagramTransportGroupe19(udp, new InetSocketAddress(host, port));

        // 2) Crypto + handshake
        SecureRandom rng = new SecureRandom(); // Aléa pour DTLS
        TlsCrypto crypto = new BcTlsCrypto(rng);  // Implémentation crypto BC
        byte[] id  = "client1".getBytes(StandardCharsets.UTF_8);    // ID PSK (côté client)
        byte[] key = "secret123".getBytes(StandardCharsets.UTF_8);  // Clé partagée (même des 2 côtés)
        TlsPSKIdentity identity = new BasicTlsPSKIdentity(id, key); // Objet “identité PSK”

        DTLSClientProtocol clientProtocol = new DTLSClientProtocol();
        TlsClient client = new MyPskClient(crypto, identity);
        DTLSTransport dtls = clientProtocol.connect(client, udpTransport);

        System.out.println("Connecté DTLS (PSK) à " + host + ":" + port);
        System.out.println("Tour par tour : vous envoyez d’abord, puis vous recevez la réponse.");
        System.out.println("Tapez QUIT pour terminer.");

        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
        byte[] inBuf = new byte[2048];

        while (true) {
            // SEND
            System.out.print("[Vous]  ");
            String line = console.readLine();
            if (line == null) break;
            byte[] data = line.getBytes(StandardCharsets.UTF_8);
            dtls.send(data, 0, data.length);

            if ("QUIT".equalsIgnoreCase(line)) {
                // on attend "BYE" éventuel
                int n = dtls.receive(inBuf, 0, inBuf.length, 3000);
                if (n > 0) {
                    String resp = new String(inBuf, 0, n, StandardCharsets.UTF_8);
                    System.out.println("[Serveur] " + resp);
                }
                break;
            }

            // RECU (bloquant)
            int n = dtls.receive(inBuf, 0, inBuf.length, 0);
            if (n < 0) break;
            String resp = new String(inBuf, 0, n, StandardCharsets.UTF_8);
            System.out.println("[Serveur] " + resp);

            if ("BYE".equalsIgnoreCase(resp)) {
                System.out.println("(Quit demandé par le serveur)");
                break;
            }
        }

        dtls.close();
        udp.close();
        System.out.println("Client DTLS arrêté.");
    }
}
