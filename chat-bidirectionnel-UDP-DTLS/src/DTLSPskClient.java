// DTLSPskClient.java
import org.bouncycastle.tls.*;
import org.bouncycastle.tls.crypto.TlsCrypto;
import org.bouncycastle.tls.crypto.impl.bc.BcTlsCrypto;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

public class DTLSPskClient {
    static class MyPskClient extends PSKTlsClient {
        public MyPskClient(TlsCrypto crypto, TlsPSKIdentity pskIdentity) {
            super(crypto, pskIdentity);
        }
        @Override
        protected int[] getSupportedCipherSuites() {
            return new int[] { CipherSuite.TLS_PSK_WITH_AES_128_CCM_8 };
        }
        @Override
        protected ProtocolVersion[] getSupportedVersions() {
            return new ProtocolVersion[] { ProtocolVersion.DTLSv12 };
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("Usage: java DTLSPskClient <server_ip> <port>");
            return;
        }
        String host = args[0];
        int port = Integer.parseInt(args[1]);

        // UDP local, connecté au serveur
        DatagramSocket udp = new DatagramSocket();
        udp.connect(new InetSocketAddress(host, port));
        UdpDatagramTransport udpTransport = new UdpDatagramTransport(udp, new InetSocketAddress(host, port));

        // Crypto
        SecureRandom rng = new SecureRandom();
        TlsCrypto crypto = new BcTlsCrypto(rng);

        // Identité PSK client (doit matcher le serveur)
        byte[] id  = "client1".getBytes(StandardCharsets.UTF_8);
        byte[] key = "secret123".getBytes(StandardCharsets.UTF_8);
        TlsPSKIdentity identity = new BasicTlsPSKIdentity(id, key);

        // NOTE: constructeur sans argument en 1.78.1
        DTLSClientProtocol clientProtocol = new DTLSClientProtocol();
        TlsClient client = new MyPskClient(crypto, identity);

        // Handshake DTLS
        DTLSTransport dtls = clientProtocol.connect(client, udpTransport);
        System.out.println("Connecté DTLS (PSK) à " + host + ":" + port);

        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        byte[] inBuf = new byte[2048];

        System.out.println("Tape un message (***CLOSE*** ou QUIT pour arrêter).");
        while (true) {
            System.out.print("> ");
            String line = stdin.readLine();
            if (line == null) break;

            byte[] data = line.getBytes(StandardCharsets.UTF_8);
            dtls.send(data, 0, data.length);

            if ("***CLOSE***".equalsIgnoreCase(line) || "QUIT".equalsIgnoreCase(line)) {
                int n = dtls.receive(inBuf, 0, inBuf.length, 3000);
                if (n > 0) {
                    String resp = new String(inBuf, 0, n, StandardCharsets.UTF_8);
                    System.out.println("Serveur: " + resp);
                }
                break;
            }

            int n = dtls.receive(inBuf, 0, inBuf.length, 5000);
            if (n > 0) {
                String resp = new String(inBuf, 0, n, StandardCharsets.UTF_8);
                System.out.println("Serveur: " + resp);
            } else {
                System.out.println("(timeout en attente de réponse)");
            }
        }

        dtls.close();
        udp.close();
        System.out.println("Client DTLS arrêté.");
    }
}
