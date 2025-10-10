/** Costa MASKULOV THOME Blanche bintou fané**/
/** Groupe: 19 **/
/** Port: 41000 **/

import java.net.*;
import java.io.*;
import java.util.Scanner;

public class ServerTCPGroupe19 {
    public static void main(String[] args) {
        final int PORT = 41000; // Port fixe comme ta version UDP
        Scanner console = new Scanner(System.in);

        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("Serveur TCP démarré sur le port " + PORT + "...");
            System.out.println("En attente d’une connexion client...");

            try (Socket link = server.accept()) {
                System.out.println("Client connecté: " + link.getRemoteSocketAddress());

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(link.getInputStream())
                );
                PrintWriter out = new PrintWriter(link.getOutputStream(), true); // auto-flush

                while (true) {
                    String message = in.readLine(); // bloquant, null si client fermé
                    if (message == null) {
                        System.out.println("Client fermé la connexion.");
                        break;
                    }

                    System.out.println("\nMessage reçu de " + link.getRemoteSocketAddress());
                    System.out.println("Contenu : " + message);

                    if (message.equalsIgnoreCase("QUIT")) {
                        System.out.println("Client a demandé la fermeture. Arrêt du serveur...");
                        break;
                    }

                    System.out.print("Votre réponse : ");
                    String response = console.nextLine();
                    out.println(response); // renvoi au client
                }
            }
        } catch (IOException e) {
            System.out.println("Erreur : " + e.getMessage());
        } finally {
            console.close();
        }
    }
}
