# PSR-Lab-Sklady-Klucz-Wartosc

## Uruchamianie aplikacji
Aplikacje można uruchomić w środowisku IntelliJ IDEA lub za pomocą poleceń maven:
- **Hazelcast:** `mvn clean && mvn package && mvn exec:java -D exec.mainClass=LigaSportowaHazelcast.Program`
- **CosmosDB:** `mvn clean && mvn package && mvn exec:java -D exec.mainClass=LigaSportowaCosmosDB.Program`

## LigaSportowaHazelcast
Możliwości:
- Utworzenie ligi sportowej
- Wyświetlenie informacji o wszystkich utworzonych ligach
- Wyświetlenie informacji o pojedynczej lidze
- Wyświetlenie posortowanej (sortowanie po punktach) tabeli wyników danej ligi
- Zaktualizowanie informacji o lidze
- Usunięcie pojedynczej ligi
- Utworzenie terminarza
- Wyświetlenie informacji o wszystkich utworzonych terminarzach
- Wyświetlenie informacji o pojedynczym terminarzu
- Zaktualizowanie informacji o terminarzu
- Usunięcie pojedynczego terminarza

## LigaSportowaCosmosDB
Możliwości:
- Utworzenie ligi sportowej
- Wyświetlenie informacji o wszystkich utworzonych ligach
- Wyświetlenie informacji o pojedynczej lidze
- Wyświetlenie posortowanej (sortowanie po punktach) tabeli wyników danej ligi
- Zaktualizowanie informacji o lidze
- Usunięcie pojedynczej ligi
- Utworzenie terminarza
- Wyświetlenie informacji o wszystkich utworzonych terminarzach
- Wyświetlenie informacji o pojedynczym terminarzu
- Wyświetlenie informacji o planowanych spotkaniach drużyny jako gospodarz
- Wyświetlenie informacji o planowanych spotkaniach drużyny jako gość
- Wyświetlenie informacji o zakończonych spotkaniach drużyny jako gospodarz
- Wyświetlenie informacji o zakończonych spotkaniach drużyny jako gość
- Zaktualizowanie informacji o terminarzu
- Usunięcie pojedynczego terminarza

Do stworzenia aplikacji zostały wykorzystane biblioteki takie jak:
- Text-IO: `https://mvnrepository.com/artifact/org.beryx/text-io/3.0.0`
- GSON (LigaSportowaCosmosDB): `https://mvnrepository.com/artifact/com.google.code.gson/gson`
- Azure DocumentDB (LigaSportowaCosmosDB): `https://mvnrepository.com/artifact/com.microsoft.azure/azure-documentdb`
