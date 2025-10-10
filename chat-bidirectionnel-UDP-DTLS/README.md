# compiler
javac -cp .:bcprov-jdk18on-1.78.1.jar:bctls-jdk18on-1.78.1.jar src/*.java

# lancer le serveur (terminal A)
java -cp .:bcprov-jdk18on-1.78.1.jar:bctls-jdk18on-1.78.1.jar DTLSPskServer

# lancer le client (terminal B)
java -cp .:bcprov-jdk18on-1.78.1.jar:bctls-jdk18on-1.78.1.jar DTLSPskClient 127.0.0.1 41000




REM compiler
javac -cp .;bcprov-jdk18on-1.78.1.jar;bctls-jdk18on-1.78.1.jar src\*.java

REM serveur
java -cp .;bcprov-jdk18on-1.78.1.jar;bctls-jdk18on-1.78.1.jar DTLSPskServer

REM client
java -cp .;bcprov-jdk18on-1.78.1.jar;bctls-jdk18on-1.78.1.jar DTLSPskClient 127.0.0.1 41000
