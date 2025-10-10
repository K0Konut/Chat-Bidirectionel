Pour lancer en TCP : 


# Compiler
javac src/TCPServer.java src/TCPClient.java

# Lancer le serveur (PC A)
java -cp src TCPServer
# Lancer le client (PC B)
java -cp src TCPClient IP 41000
