// DTLSPskServer.java
import org.bouncycastle.tls.*;
import org.bouncycastle.tls.crypto.TlsCrypto;
import org.bouncycastle.tls.crypto.impl.bc.BcTlsCrypto;

import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;

public class DTLSPskServer {
    private static final int PORT = 41000;

    // PSK manager: identité autorisée + clé pré-partagée
    static class SimplePskIdentityManager implements TlsPSKIdentityManager {
        private final byte[] allowedId = "client1".getBytes(StandardCharsets.UTF_8);
        private final byte[] sharedKey = "secret123".getBytes(StandardCharsets.UTF_8);

        @Override
        public byte[] getPSK(byte[] identity) {
            return Arrays.equals(identity, allowedId) ? sharedKey : null; // null => handshake échoue
        }

        @Override
        public byte[] getHint() {
            // Optionnel: peut aider le client à choisir l'identité
            return "psk_hint".getBytes(StandardCharsets.UTF_8);
        }
    }

    static class MyPskServer extends PSKTlsServer {
        public MyPskServer(TlsCrypto crypto, TlsPSKIdentityManager idMgr) {
            super(crypto, idMgr);
        }
        @Override
        protected int[] getSupportedCipherSuites() {
            return new int[] { CipherSuite.TLS_PSK_WITH_AES_128_CCM_8 };
        }
        @Override
        protected ProtocolVersion[] getSupportedVersions() {
            // 1.78.1: retourner un tableau
            return new ProtocolVersion[] { ProtocolVersion.DTLSv12 };
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("DTLS PSK Server sur UDP " + PORT);

        DatagramSocket udp = new DatagramSocket(new InetSocketAddress(PORT));
        UdpDatagramTransport udpTransport = new UdpDatagramTransport(udp, null);

        // Crypto alimentée par SecureRandom, utilisée via BcTlsCrypto
        SecureRandom rng = new SecureRandom();
        TlsCrypto crypto = new BcTlsCrypto(rng);

        // NOTE: constructeurs sans argument en 1.78.1
        DTLSServerProtocol serverProtocol = new DTLSServerProtocol();

        TlsServer server = new MyPskServer(crypto, new SimplePskIdentityManager());

        // Handshake DTLS (bloquant)
        DTLSTransport dtls = serverProtocol.accept(server, udpTransport);
        System.out.println("Handshake DTLS réussi (PSK). Prêt à échanger.");

        byte[] buf = new byte[2048];
        while (true) {
            int n = dtls.receive(buf, 0, buf.length, 0); // 0 = bloquant
            if (n < 0) break;
            String msg = new String(buf, 0, n, StandardCharsets.UTF_8);
            System.out.println("Reçu: " + msg);

            if ("***CLOSE***".equalsIgnoreCase(msg) || "QUIT".equalsIgnoreCase(msg)) {
                byte[] bye = "BYE".getBytes(StandardCharsets.UTF_8);
                dtls.send(bye, 0, bye.length);
                break;
            }

            String resp = "ECHO: " + msg.toUpperCase();
            byte[] out = resp.getBytes(StandardCharsets.UTF_8);
            dtls.send(out, 0, out.length);
        }

        dtls.close();
        udp.close();
        System.out.println("Serveur DTLS arrêté.");
    }
}
