/**Costa MASKULOV THOME Blanche **/
/**Groupe: 19 */
/*Port: 41000 */

// UDPServer.java
import java.net.*;  // Contient DatagramSocket, DatagramPacket, InetAddress
import java.io.*;   // Pour IOException
import java.util.Scanner;

public class UDPServer {
    public static void main(String[] args) {
        final int PORT = 5000;
        byte[] buffer = new byte[1024]; // Tampon pour recevoir les messages
        Scanner sc = new Scanner(System.in);

        try {
            // Création du socket serveur sur le port 5000
            DatagramSocket serverSocket = new DatagramSocket(PORT);
            System.out.println("Serveur UDP démarré sur le port " + PORT + "...");

            while (true) {
                // Préparer un paquet pour réception
                DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);

                // Attendre un message (bloquant)
                serverSocket.receive(receivedPacket);

                // Extraire le message reçu
                String message = new String(receivedPacket.getData(), 0, receivedPacket.getLength());
                InetAddress clientAddress = receivedPacket.getAddress();
                int clientPort = receivedPacket.getPort();

                System.out.println("\nClient [" + clientAddress + ":" + clientPort + "] dit : " + message);

                // Vérifier si le client veut fermer la connexion
                if (message.equalsIgnoreCase("QUIT")) {
                    System.out.println("Client a demandé la fermeture. Arrêt du serveur...");
                    break;
                }

                // Lire la réponse à envoyer
                System.out.print("Votre réponse : ");
                String response = sc.nextLine();

                // Envoyer la réponse au client
                byte[] sendData = response.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
                serverSocket.send(sendPacket);
            }

            serverSocket.close();
            sc.close();
        } catch (IOException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }
}
