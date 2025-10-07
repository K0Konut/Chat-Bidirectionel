/** Costa MASKULOV THOME Blanche **/
/** Groupe: 19 **/
/** Port: 41000 **/

import java.net.*;
import java.io.*;
import java.util.Scanner;

public class UDPServer {
    public static void main(String[] args) {
        final int PORT = 41000; // Port fixe pour recevoir
        byte[] buffer = new byte[1024];
        Scanner sc = new Scanner(System.in);

        try {
            // Création du socket serveur sur le port 41000
            DatagramSocket serverSocket = new DatagramSocket(PORT);
            System.out.println("Serveur UDP démarré sur le port " + PORT + "...");

            while (true) {
                // Préparer un paquet pour réception
                DatagramPacket receivedPacket = new DatagramPacket(buffer, buffer.length);

                // Attendre un message (bloquant)
                System.out.println("En attente d’un message...");
                serverSocket.receive(receivedPacket);

                // Extraire le message reçu
                String message = new String(receivedPacket.getData(), 0, receivedPacket.getLength());
                InetAddress clientAddress = receivedPacket.getAddress();
                int clientPort = receivedPacket.getPort();

                System.out.println("\nMessage reçu de " + clientAddress + ":" + clientPort);
                System.out.println("Contenu : " + message);

                if (message.equalsIgnoreCase("QUIT")) {
                    System.out.println("Client a demandé la fermeture. Arrêt du serveur...");
                    break;
                }

                System.out.print("Votre réponse : ");
                String response = sc.nextLine();

                byte[] sendData = response.getBytes();
                DatagramPacket sendPacket =
                        new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
                serverSocket.send(sendPacket);
            }

            serverSocket.close();
            sc.close();
        } catch (IOException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }
}
