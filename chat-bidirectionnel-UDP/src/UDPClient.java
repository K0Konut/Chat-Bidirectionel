/** Costa MASKULOV THOME Blanche **/
/** Groupe: 19 **/
/** Port: 41000 **/

// UDPClient.java
import java.net.*;  // Contient DatagramSocket, DatagramPacket, InetAddress
import java.io.*;   // Pour IOException
import java.util.Scanner;

public class UDPClient {
    public static void main(String[] args) {

        // Vérifier qu'on a bien passé l'adresse IP et le port en arguments
        if (args.length != 2) {
            System.out.println("Usage : java UDPClient <adresse_IP_serveur> <port>");
            return;
        }

        try {
            // Récupération des arguments
            String serverIP = args[0];
            int serverPort = Integer.parseInt(args[1]);

            // Création du socket client
            DatagramSocket clientSocket = new DatagramSocket();
            InetAddress serverAddress = InetAddress.getByName(serverIP);

            System.out.println("Client UDP prêt. Envoi vers " + serverIP + ":" + serverPort);

            Scanner sc = new Scanner(System.in);

            while (true) {
                System.out.print("Vous : ");
                String message = sc.nextLine();

                // Conversion en octets et envoi au serveur
                byte[] sendData = message.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, serverPort);
                clientSocket.send(sendPacket);

                // Si l'utilisateur tape "QUIT", on sort
                if (message.equalsIgnoreCase("QUIT")) {
                    System.out.println("Fermeture du client...");
                    break;
                }

                // Préparer la réception de la réponse du serveur
                byte[] receiveBuffer = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                clientSocket.receive(receivePacket);

                // Afficher la réponse
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
