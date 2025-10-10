# Compiler
rm -f src/*.class

javac -cp "src:lib/bcprov-jdk18on-1.77.jar:lib/bcpkix-jdk18on-1.77.jar:lib/bctls-jdk18on-1.77.jar:lib/bcutil-jdk18on-1.77.jar:lib/bcprov-ext-jdk18on-1.77.jar" src/*.java

# Serveur
java -cp "src:lib/bcprov-jdk18on-1.77.jar:lib/bcpkix-jdk18on-1.77.jar:lib/bctls-jdk18on-1.77.jar:lib/bcutil-jdk18on-1.77.jar:lib/bcprov-ext-jdk18on-1.77.jar" DTLSPskServer

# Client
java -cp "src:lib/bcprov-jdk18on-1.77.jar:lib/bcpkix-jdk18on-1.77.jar:lib/bctls-jdk18on-1.77.jar:lib/bcutil-jdk18on-1.77.jar:lib/bcprov-ext-jdk18on-1.77.jar" DTLSPskClient IP 41000