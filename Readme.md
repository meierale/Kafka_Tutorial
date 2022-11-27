# Kafka Tutorial mit lokalem docker-compose und Spring Boot
Dies ist ein bewusst sehr kurz & einfach gehaltenes mini Tutorial zu Apache Kafka. Es basiert auf einer lokalen und minimalen, Docker basierten Kafka Installation.

Dieses Repository ist als Monorepo aufgebaut. Es besteht aus mehreren Unterordnern:

* kafka-docker-env - Enthält 2 verschiedene Varianten für die lokale Kafka Installation. Entweder ein single-node-container von Landoop, oder eine Mini Installation mehrere einzelner Kafka Komponenten.
* Mehrere Kafka Demo Unterordner mit Spring Boot Demo Applikationen (Producer und Consumer)

Die Tutorials setzen voraus, dass du etwas Erfahrung mit Java Spring Boot Apps mitbringst. Die einzelnen Tutorials sind nicht im Detail erläutert, sondern eher dafür gedacht, dass du dir selbst die Files etwas anschaust und versuchst zu verstehen, wie die Applikation aufgebaut ist und funktioniert.

## Start der Umgebung

Wenn du das Repo lokal geklont hast, der Docker Daemon auf deinem System läuft, dann starte bitte die ganze Umgebung aus dem `kafka-docker-env` Ordner in einem Terminal mit:
```
docker-compose up
```

Dadurch wird das Landoop single-node `fast-data-dev` image verwendet und hochgefahren.

Das landoop image stellt ein Web-UI unter http://localhost:3030/ zur Verfügung 

### Alternative zu Landoop fast-data-dev

Alternativ steht auch ein weiteres docker-compose file zur Verfügung. Dieses verwendet einzelne Docker images/container der Komponenten eines Kafka Clusters:
* 1 Broker Container
* 1 Zookeeper Container
* 1 Schema Registry Container
* 1 Kafka-Connect Container
* 1 ksql Container
* 1 Kafka-UI Container

Das `kafka-ui.yml` ist eine Kopie von https://github.com/FilipeNavas/kafka-local. Es basiert auf den Demos von Confluent auf Github. Als UI verwendet es anstelle des Confluent Control Center das Kafka-UI von provectus (https://github.com/provectus/kafka-ui)

Der Startup Prozess kann ein paar Minuten dauern, da die einzelnen Container ihre Prozesse hochfahren und dann untereinander synchronisieren müssen.

Das Kafka-UI ist ebenfalls unter http://localhost:3030/ erreichbar. 

Hinweis: Starte nicht beide files gleichzeitig in unterschiedlichen Terminals. Verwende nur entweder das Default `docker-conpose.yml` für Landoop ODER das `kafka-ui.yml` für das alternative Setup.


# Kafka Tutorial Part 1

In diesem ersten Teil des Kafka Tutorial produzieren und konsumieren wir Nachrichten auf dem Broker direkt, mittels CLI Commands.

Diese Nachrichten schauen wir dann im Web UI an und ebenfalls auf dem Broker im Filesystem.

## Tutorial 1 Konsolen Beispiele

Dieses Tutorial geht davon aus, dass du den Landoop single node Container verwendest. Falls du das alternative Setup aktiv laufen hast, verwende die entsprechenden einzelnen Nodes (mit `docker ps` ID oder Name suchen).

### CLI Producer und Consumer
Kafka hat selbst cli Tools um Messages zu schreiben/produzieren:
1. `docker exec -it kafka-all /bin/bash`
1. `kafka-topics --list --bootstrap-server localhost:9092`
1. `kafka-console-producer --topic KafkaDemo-Topic --bootstrap-server localhost:9092`

Das erste kafka-topics Command hat nur die internen Topics angezeigt, die schon "upon startup" existieren. Wenn du nach dem Schreiben erster Nachrichten nochmals das `kafka-topics` Command ausführst, wird das automatisch erzeugte `KafkaDemo-Topic` ebenfalls angezeigt.

Diese Nachrichten können auch wiederum per CLI Tool gelesen werden:
1. `docker exec -it kafka-all /bin/bash`
1. `kafka-topics --list --bootstrap-server localhost:9092`
1. `kafka-console-consumer --topic KafkaDemo-Topic --group demo-group --from-beginning --bootstrap-server localhost:9092`

Das `kafka-console-consumer` wird bewusst mit dem `--from-beginning` Parameter gestartet. Würde dieser initial weggelassen, werden nur "ab jetzt" einkommende Nachrichten konsumiert.

### Nachrichten auf dem Broker im File System einsehen

Die Nachrichten und die Topics sind auf dem Broker ersichtlich, denn sie liegen ja in 'log' Files:

* Hinweis: Je nach verwendetem Docker Image ist der Speicherort etwas anders.

1. `docker exec kafka-all /bin/bash`
1. `cd /data/kafka/logdir/`
1. `ls -ltr`
1. Schau dich etwas um, siehst du das vorhin angelegte Topic?
  1. Handelt es sich dabei um ein File, oder einen Folder?
1. `cd KafkaDemo-Topic-0/`
1. Was haben wir jetzt gerade getan? Resp. wo genau sind wir jetzt?
1. `cat 00000000000000000000.log`

# Tutorial 2 - Java Code Beispiele ohne Schema

In diesem Teil des Tutorial sind die Beispiele bereits "fertig" in den entsprechenden Demo Unterordnern abgelegt. Sie verwenden Java 11 und wurden mit IntelliJ erstellt.

Es geht in den Beispielen also weniger darum, selbst Code zu schreiben, sondern darum nachzuvollziehen, was der Code macht. 

## kafka-demo-consumer mit Spring Boot - klassisch, plaintext
Dieser Demo Consumer wurde als Spring Boot App mit Spring-Kafka Dependency erstellt. Er verwendet eine KafkaConfig Klasse zur Konfiguration.

1. Auf  https://start.spring.io/ wurde der Rumpf für eine Spring Boot App mit Kafka erstellt
  1. Als Dependency Spring for Apache Kafka ausgewählt
1. Die Datei `KafkaConfig.java` (Package org.meierale.kafkademoconsumer.config) definiert die Kafka Konfiguration für die ConsumerFactory eines KafkaListeners.
1. Die Datei `KafkaConsumer.java` (org.meierale.kafkademoconsumer.consumer) Verwendet einen KafkaListener und schreibt die plaintext Nachrichten auf die Konsole.
1. Wird die Applikation gestartet, werden (neue) Nachrichten ausgegeben. (Schreibe dazu z.B. neue Nachrichten im CLI Console Producer in dasselbe Topic).

## Kafka-Demo-JSON-Producer für Plaintext und JSON mit Spring Boot - mit POJO als Plaintext/String
Dieser Demo Producer kann sowohl Plaintext Nachrichten in das `KafkaDemo-Topic` schreiben, wie auch JSON Nachrichten in ein `KafkaDemo-JSON-Topic`. 

Um Nachrichten zu schreiben stellt der Producer zwei REST Endpoints zur Verfügung:
* Ein `@GetMapping("/publish/{message}")` für Plaintext Nachrichten
* Ein `@PostMapping("/publish")` für JSON Nachrichten von "Büchern" (bookName, isbn)
  * Für Post Mapping wird ein POJO verwendet 

1. Auf  https://start.spring.io/ wurde der Rumpf für eine Spring Boot App mit Kafka UND Spring Web erstellt.
1. Die Datei `KafkaConfig.java` (Package org.meierale.kafkademoproducer) definiert die Kafka Konfiguration für die ProducerFactory des KafkaTemplates.
1. `Book.java` beschreibt das Buch POJO
1. Der `DemoController.java` verwendet 2 KafkaTemplates zum schreiben der Nachrichten, welche über die 2 Endpunkte empfangen werden.
1. In den `application.properties` wird der Port angeben, z.B. `server.port=9090`
1. Wird die Applikation gestartet, können mit Postman (oder curl) Plaintext bzw. JSON Bücher Nachrichten an die Applikation geschickt werden. Diese werden in das entsprechende Topic geschrieben.
   1. Beispiel für Plaintext: `curl --location --request GET 'http://localhost:9090/publish/Hallo%20zusammen%20so%20macht%20Kafka%20Spass!%20Soooo%20Cool!'`
   1. Beispiel für eine Buch Nachricht: 
   ```
   curl --location --request POST 'http://localhost:9090/publish' \
   --header 'Content-Type: application/json' \
   --data-raw '{
       "bookName": "Data Mesh",
       "isbn": "978-1-492-09239-1"
   }'
   ```

## kafka-demo-consumer2 mit Spring Boot - klassisch, plaintext
Dieser Demo Consumer wurde ebenfalls als Spring Boot App mit Spring-Kafka Dependency erstellt. Anstelle einer eigenen KafkaConfig übernimmt der Consumer aber 
die Konfiguration aus dem `application.yaml` File und verwendet die "Spring Boot Magie" zur Konfiguration des KafkaListeners ;-)

1. Auf  https://start.spring.io/ wurde der Rumpf für eine Spring Boot App mit Kafka erstellt
  1. Als Dependency Spring for Apache Kafka ausgewählt
1. In `application.yaml` (src/main/resources/) definiert die Kafka Konfiguration für die ConsumerFactory eines KafkaListeners.
1. Die Datei `KafkaConsumer.java` (org.meierale.kafkademoconsumer.consumer) Verwendet einen KafkaListener und schreibt die JSON Nachrichten auf die Konsole.
1. Wird die Applikation gestartet, werden alle Nachrichten ausgegeben, auch schon bestehende. (Erkennst du warum? Schau dir dazu den KafkaConsumer nochmals an).

# Tutorial 3 - Java Code Beispiele mit AVRO

Auch in diesem Teil des Tutorial sind die Beispiele bereits "fertig" in den entsprechenden Demo Unterordnern abgelegt. Sie verwenden wiederum Java 11 und wurden mit IntelliJ erstellt.

Im Gegesatz zu den Beispielen in Tutorial 2 werden nun Nachrichten mit Avro Schema verschickt:
* Dazu wird initial im Producer ein Avro Schema definiert `customer.avsc`
* Der Producer registriert dieses Schema in der Schema Registry
* Ein Consumer kann das registrierte Schema von der Schema-Registry beziehen (einfachste Variante: Copy/Paste aus dem UI)
* Mit diesem Schema kann der Consumer die Nachrichten aus dem Topic auslesen

## kafka-demo-avro-producer
Dieser Avro Demo Producer schreibt Nachrichten in das `KafkaDemo-AVRO-Topic`.
Es handelt sich dabei um Nachrichten vom Typ/Schema "Customer". 

Der Producer stellt einen POST Endpunkt zur Verfügung, welcher mit Postman / Curl aufgerufen werden kann.

Damit aus dem Avro Schema POJOs erstellt werden, wird das `avro-maven-plugin` verwendet.

1. Auf  https://start.spring.io/ wurde der Rumpf für eine Spring Boot App mit Kafka UND Spring Web erstellt.
1. Das `pom.xml` wurde zudem wie folgt angepasst:
   * Das Confluent Maven Repository wurde hinzugefügt (Z. 17 ff.)
   * Avro und Confluent Avro Versionen wurden definiert (Z. 24 ff.)
   * Avro und Kafka-Avro Serializer wurden als Dependency hinzugefügt (Z. 36 - 45)
   * Das Verhalten des Avro Maven Plugin wurde definiert (Z. 69 - 88)
     * Dabei sind insbesondere Z. 82 - 84 wichtig... warum? ;-)
1. In `application.yaml` werden Server Port, Topic und die Spring Kafka Properties für den Producer definiert.
1. Der `DemoController.java` stellt den POST Endpunkt zur Verfügung.
   * Dieser nimmt zu schreibende `Customer` entgegen
   * Und leitet sie zum schreiben ins Topic an den AvroProducer weiter
1. Der `AvroProducer.java` schreibt Nachrichten vom Typ `Customer` via KafkaTemplate in das Avro Topic

Damit die Applikation ausgeführt werden kann, muss zunächst das POJO aus dem Avro Schema erstellt werden: `mvn generate-sources`

Allenfalls muss danach das maven Projekt neu geladen werden (Maven > Reload Project)

Wird die Applikation gestartet, können mit Postman / curl Customer Nachrichten geschickt und ins Avor Topic geschrieben werden:
```
curl --location --request POST 'http://localhost:9090/publish' \
--header 'Content-Type: application/json' \
--data-raw '{
    "firstName": "Alex",
    "lastName": "Meier",
    "age": 43
}'
```

Im Kafka UI sind dann die so geschriebenen Nachrichten im Avro Topic ersichtlich. Wobei das UI nur String 'automatisch' erkennen und darstellen kann, der Integer Wert kann nicht 'erraten' werden.
Zudem ist in der Schema Registry das Customer Schema registriert. Es kann nun von dort von Consumer Applikationen bezogen werden.

## kafka-demo-avro-consumer
Dieser Avro Demo Consumer liest Nachrichten aus dem `KafkaDemo-AVRO-Topic`.
Es handelt sich dabei um die vorhin produzierten Nachrichten vom Typ/Schema "Customer". 

Damit aus dem Topic die Avro Schema Nachrichten ausgelesen werden können, wird wiederum das `avro-maven-plugin` verwendet.

1. Auf  https://start.spring.io/ wurde der Rumpf für eine Spring Boot App mit Kafka erstellt.
1. Das `pom.xml` wurde zudem wie beim Producer angepasst:
   * Das Confluent Maven Repository wurde hinzugefügt (Z. 17 ff.)
   * Avro und Confluent Avro Versionen wurden definiert (Z. 24 ff.)
   * Avro und Kafka-Avro Serializer wurden als Dependency hinzugefügt (Z. 36 - 45)
   * Das Verhalten des Avro Maven Plugin wurde definiert (Z. 69 - 88)
     * Dabei sind insbesondere Z. 82 - 84 wichtig... warum? ;-)
1. In `application.yaml` werden Server Port, Topic und die Spring Kafka Properties für den Consumer definiert.
1. Der `KafkaAvroConsumer.java` liest die Daten aus dem Topic aus und schreibt sie auf die Konsole.