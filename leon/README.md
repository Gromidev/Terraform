# Spring Terraform Demo

## Anforderungen

* "Fachlogik"
  * Konfiguration formatieren
  * Konfiguration speichern
* Website
  * Startseite
  * Formular zur Erstellung von Konfigurationen
    * Eigenschaften eingeben
    * Validierungsfehler zu Eigenschaften
    * zu Terraform-Konfigurationsdatei formatieren
  * Ergebnisseite (Erfolg/Fehler)
* Test
  * kein Website-Test, da statisch == "händisch getestet"
  * E2E-Tests
    * Formular abgesendet -> gespeichert
      * prüfen, dass Inhalt valide
      * Formular-inhalt variieren
  * Unit-Test
    * Fachlogik
      * Formatierung der Konfig-Datei?
  * Format-Validierung gegen "terraform validate"

## Umgesetzt

Spring-Boot Projekt, dass mit Maven gebaut wird
* ermöglicht einfache Einbindung von Abhängigkeiten
* pom.xml beschreibt Abhängigkeiten

Spring-Web Anwendung, die HTTP-Server startet und auf Anfragen reagiert
* Controller in de.puente.terraform.web beschreiben, wie reagiert wird
* Ein Mapping in Controller, pro Endpoint auf den reagiert wird
* Ein ExceptionHanding in Controller, pro (server-seitigen Fehler) auf den reagiert wird

Thymeleaf, um HTML-Seiten zur Verwendung durch den Nutzer zu Erzeugen
* Eine ModelAndView in Controller, pro Website die dynamisch erzeugt und dargestellt wird
  * View verweist auf eine HTML-Vorlage, die gefüllt wird
  * Model sind Daten, die vom Controller zum Füllen der Vorlage bereitgestellt werden
* Vorlagen für diese Seiten in src/main/resources/templates

Gemeinsame Header (inkl. Navbar) und Footer für alle Seiten
* Per Verweis aus anderen Seiten heraus eingebettet

Startseite

Konfigurations-Seite
* Optionen, die Nutzer zur Auswahl hat, sind dynamisch - können ohne Änderung der Webseite erweitert werden

Ergebnis-Seite
* Meldungen / ob Erfolg/Fehler ist dynamisch

Reguläre Ausdrücke zum Erzeugen der Terraform-Konfiguration auf Basis einer Vorlage
* Optionen / Auswahl des Users wird zu Menge von Variablen übersetzt
* Vorlage wird eingelesen
* Vorkommnisse der Variablen werden durch passende Werte / formatierte Abschnitte ersetzt

Konfiguration der Anwendung auf Basis von Kommandozeilenparametern / Dateien
* so ziemlich alle Aspekte von Spring
* Woher Terraform-Vorlage gelesen wird
* Wohin Terraform-Konfigurationen geschrieben werden
* Wie Konfigurationen benannt werden

## Benutzung

Bauen:
```bash
mvn clean package -DskipTests
```
* führt Maven aus
* clean: erzwingt erneutes Kompilieren
* package: erzeugt die fertige Anwendung
* (optional) skipTests: überspringt Tests
* erzeugt die Anwendung unter `target/terraform-api.jar`

Normaler Start:
```bash
java -jar target/terraform-api
```
* startet einen Webserver unter Port 8080
* die Webseite kann unter `http://localhost:8080/terraform` erreicht werden

Angabe von Properties bei Start (dokumentiert in de.puente.terraform.services.TerraformProperties):
```bash
java -jar target/terraform-api --terraform.template-file="otherTemplate.tf" --terraform.output-folder="otherOutput"
```

Properties aus Datei
```bash
java -jar target/terraform-api --spring.config.import=DATEI
```

Mit Properties im YAML-Format (z.B. terraform-api.yaml)
```yaml
terraform:
  template-file: "otherTemplate.tf"
  output-folder: "otherOutput"
```

Mit Properties im Properties-Format (z.B. terraform-api.properties)
```properties
terraform.template-file=otherTemplate.tf
terraform.output-folder=otherOutput
```

Beispiel für Spring-Properties: Anderer Port
```bash
java -jar target/terraform-api --server.port=8081
```

## Referenzen

Spring Boot als Anwendungs-Framework
* https://spring.io/why-spring
* Anwendung wurde erstellt mit: https://start.spring.io/

Lombok, um simplen Coden (Getter, Setter, Konstruktoren) zu generieren
* https://www.baeldung.com/intro-to-project-lombok#constructors
* https://www.baeldung.com/intro-to-project-lombok#core

Spring WebMVC & Thymeleaf zum Erzeugen und Darstellen von Webseiten
* https://www.baeldung.com/thymeleaf-in-spring-mvc
* Thymeleaf Doku: https://www.thymeleaf.org/doc/tutorials/3.0/thymeleafspring.html
* Thymeleaf Cheatsheet: https://github.com/engma/thymeleaf-cheat-sheet
* WebMVC Doku:
  * @Controller: https://docs.spring.io/spring-framework/docs/6.0.4/reference/html/web.html#mvc-controller
  * @ExceptionHandler: https://docs.spring.io/spring-framework/docs/6.0.4/reference/html/web.html#mvc-ann-exceptionhandler
  * @ModelAttribute: https://docs.spring.io/spring-framework/docs/6.0.4/reference/html/web.html#mvc-ann-modelattrib-methods

Spring Validation, um User-Eingaben zu validieren
* https://www.baeldung.com/spring-boot-bean-validation

Autowiring von Komponenten in Spring-Anwendungen
* https://www.baeldung.com/spring-autowire

Spring Properties
* https://www.baeldung.com/configuration-properties-in-spring-boot

Reguläre Ausdrücke in Java
* Ersetzen von Variablen im Terraform-Template: https://www.baeldung.com/regular-expressions-java#123-replacement-methods
* Allgemein: https://www.baeldung.com/regular-expressions-java

Simpler Umgang mit Dateien
* read: https://docs.oracle.com/en/java/javase/18/docs/api/java.base/java/nio/file/Files.html#readString(java.nio.file.Path)
* write: https://docs.oracle.com/en/java/javase/18/docs/api/java.base/java/nio/file/Files.html#writeString(java.nio.file.Path,java.lang.CharSequence,java.nio.file.OpenOption...)
* Pfad konstruieren: https://docs.oracle.com/en/java/javase/18/docs/api/java.base/java/nio/file/Path.html#of(java.lang.String,java.lang.String...)