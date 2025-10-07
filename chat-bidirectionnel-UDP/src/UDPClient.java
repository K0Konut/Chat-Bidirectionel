// UDPClient.java
import java.net.*;  // Contient DatagramSocket, DatagramPacket, InetAddress
import java.io.*;   // Pour IOException
import java.util.Scanner;

public class UDPClient {
    public static void main(String[] args) {
        final int SERVER_PORT = 5000;
        Scanner sc = new Scanner(System.in);

        try {
            // Création du socket client
            DatagramSocket clientSocket = new DatagramSocket();
            InetAddress serverAddress = InetAddress.getLocalHost(); // Serveur local

            while (true) {
                System.out.print("Vous : ");
                String message = sc.nextLine();

                // Convertir en tableau d’octets
                byte[] sendData = message.getBytes();

                // Créer et envoyer le paquet
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, SERVER_PORT);
                clientSocket.send(sendPacket);

                // Si on veut quitter, on sort de la boucle
                if (message.equalsIgnoreCase("QUIT")) {
                    System.out.println("Fermeture du client...");
                    break;
                }

                // Préparer un buffer pour la réponse
                byte[] receiveBuffer = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);

                // Attente réponse du serveur
                clientSocket.receive(receivePacket);
                String response = new String(receivePacket.getData(), 0, receivePacket.getLength());
                System.out.println("Serveur : " + response);
            }

            clientSocket.close();
            sc.close();
        } catch (IOException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }
}
