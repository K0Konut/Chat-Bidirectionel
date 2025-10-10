/** Costa MASKULOV THOME Blanche bintou fané**/
/** Groupe: 19 **/
/** Port: 41000 **/

import java.net.*; 
import java.io.*;
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
            DatagramSocket clientSocket = new DatagramSocket();  // Ouvre un socket UDP côté client 
            InetAddress serverAddress = InetAddress.getByName(serverIP); // Résout l’IP en objet InetAddress

            System.out.println("Client UDP prêt. Envoi vers " + serverIP + ":" + serverPort);

            Scanner sc = new Scanner(System.in); // Prépare la lecture au clavier

            while (true) {
                System.out.print("Vous : ");
                String message = sc.nextLine();

                // Conversion en octets et envoi au serveur
                byte[] sendData = message.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, serverPort);
                clientSocket.send(sendPacket); // Envoie le paquet UDP sur le réseau

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

        } catch (IOException e) { // Attrape toute erreur d’E/S (résolution IP, socket, envoi, réception)
            System.out.println("Erreur : " + e.getMessage());
        }
    }
}
