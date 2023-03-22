package de.steger.terraform.services;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Properties, die zur Steuerung der Anwendung beim Start hereingegeben werden können
 */
// prefix, mit dem die properties beim start angegeben werden (Beispiel: "--terraform.output-folder=./output")
@ConfigurationProperties("terraform")
@Component
@Getter
@Setter
public class TerraformProperties {

    /**
     * Ordner, in dem erzeugte Konfigurationen gespeichert werden
     */
    private String outputFolder = "./out/";
    /**
     * Name der erzeugten Konfigurationsdatei
     */
    private String outputFileName = "test_output";
    /**
     * Template, das zur Erzeugung von Konfigurationsdateien verwendet wird
     */
    private String templateFile = "template.tf";
}
