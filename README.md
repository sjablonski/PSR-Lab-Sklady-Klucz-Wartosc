# PSR-Lab-Sklady-Klucz-Wartosc

## Uruchamianie aplikacji
Aplikacje można uruchomić w środowisku IntelliJ IDEA lub za pomocą poleceń maven:
- **Hazelcast:** `mvn clean && mvn package && mvn exec:java -D exec.mainClass=LigaSportowaHazelcast.Program`
- **CosmosDB:** `mvn clean && mvn package && mvn exec:java -D exec.mainClass=LigaSportowaCosmosDB.Program`
