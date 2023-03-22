package de.steger.terraform.types;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Oberklasse für Optionen, die dem Nutzer bei Formatierung einer Konfiguration zur Auswahl gestellt werden
 */
@AllArgsConstructor
@Getter
@Setter
public abstract class FormatInputOption {
    /**
     * Typ der Option (text, radio, ...) zur Unterscheidung im HTML-Template
     */
    private String type;
    /**
     * Name der Option zum Zurücksenden an den Server
     */
    private String name;
    /**
     * Label der Option zur Anzeige auf der Webseite
     */
    private String label;
}
