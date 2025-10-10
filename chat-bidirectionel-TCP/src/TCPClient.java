/** Costa MASKULOV THOME Blanche **/
/** Groupe: 19 **/
/** Port: 41000 **/

import java.net.*;
import java.io.*;
import java.util.Scanner;

public class TCPClient {
    public static void main(String[] args) {
        // Vérifier qu'on a bien passé l'adresse IP et le port en arguments
        if (args.length != 2) {
            System.out.println("Usage : java TCPClient <adresse_IP_serveur> <port>");
            return;
        }

        try {
            String serverIP = args[0];
            int serverPort = Integer.parseInt(args[1]);

            try (Socket sock = new Socket(serverIP, serverPort);
                 BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
                 PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
                 Scanner sc = new Scanner(System.in)) {

                System.out.println("Client TCP prêt. Connecté à " + serverIP + ":" + serverPort);
                System.out.println("Tape un message (QUIT pour quitter).");

                while (true) {
                    System.out.print("Vous : ");
                    String message = sc.nextLine();

                    out.println(message); // envoi au serveur

                    if (message.equalsIgnoreCase("QUIT")) {
                        System.out.println("Fermeture du client...");
                        break; // on ne lit pas de réponse dans ce cas, comme ta version UDP
                    }

                    String response = in.readLine(); // bloquant ; null si serveur a fermé
                    if (response == null) {
                        System.out.println("(Serveur a fermé la connexion)");
                        break;
                    }
                    System.out.println("Serveur : " + response);
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Port invalide : " + args[1]);
        } catch (IOException e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }
}
