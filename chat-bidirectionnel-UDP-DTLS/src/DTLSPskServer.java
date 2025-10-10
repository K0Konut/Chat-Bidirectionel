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
import java.util.Arrays;

public class DTLSPskServer {
    private static final int PORT = 41000;

    static class SimplePskIdentityManager implements TlsPSKIdentityManager {
        private final byte[] allowedId = "client1".getBytes(StandardCharsets.UTF_8);
        private final byte[] sharedKey  = "secret123".getBytes(StandardCharsets.UTF_8);
        @Override public byte[] getPSK(byte[] identity) { return Arrays.equals(identity, allowedId) ? sharedKey : null; }
        @Override public byte[] getHint() { return "psk_hint".getBytes(StandardCharsets.UTF_8); }
    }

    static class MyPskServer extends PSKTlsServer {
        public MyPskServer(TlsCrypto crypto, TlsPSKIdentityManager idMgr) { super(crypto, idMgr); }
        @Override protected int[] getSupportedCipherSuites() { return new int[]{ CipherSuite.TLS_PSK_WITH_AES_128_CCM_8 }; }
        @Override protected ProtocolVersion[] getSupportedVersions() { return new ProtocolVersion[]{ ProtocolVersion.DTLSv12 }; }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("DTLS PSK Server sur UDP " + PORT);

        // 1) Socket UDP + transport DTLS
        DatagramSocket udp = new DatagramSocket(new InetSocketAddress(PORT));
        UdpDatagramTransport udpTransport = new UdpDatagramTransport(udp, null);

        // 2) Crypto + handshake
        SecureRandom rng = new SecureRandom();
        TlsCrypto crypto = new BcTlsCrypto(rng);
        DTLSServerProtocol serverProtocol = new DTLSServerProtocol();
        TlsServer server = new MyPskServer(crypto, new SimplePskIdentityManager());
        DTLSTransport dtls = serverProtocol.accept(server, udpTransport);
        System.out.println("Handshake DTLS réussi (PSK).");

        // 3) Boucle tour par tour : RECV -> (affiche) -> ASK CLAVIER -> SEND
        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
        byte[] buf = new byte[2048];

        while (true) {
            // RECV
            int n = dtls.receive(buf, 0, buf.length, 0); // bloquant
            if (n < 0) break;
            String msg = new String(buf, 0, n, StandardCharsets.UTF_8);
            System.out.println("[Client] " + msg);

            // gestion fin
            if ("QUIT".equalsIgnoreCase(msg)) {
                byte[] bye = "BYE".getBytes(StandardCharsets.UTF_8);
                dtls.send(bye, 0, bye.length);
                System.out.println("(Quit demandé par le client)");
                break;
            }

            // ASK + SEND
            System.out.print("[Vous]  ");
            String response = console.readLine();
            if (response == null) break;
            byte[] out = response.getBytes(StandardCharsets.UTF_8);
            dtls.send(out, 0, out.length);

            if ("QUIT".equalsIgnoreCase(response)) {
                System.out.println("(Quit demandé par le serveur)");
                break;
            }
        }

        dtls.close();
        udp.close();
        System.out.println("Serveur DTLS arrêté.");
    }
}
