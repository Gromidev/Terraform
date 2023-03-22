package de.steger.terraform.services;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.steger.terraform.types.FormatInputs;

/**
 * Logik für das Formatieren von Nutzereingaben zu einer validen Terraform-Konfigurationsdatei
 */
@Component
public class TerraformFormatService {

    /**
     * Referenz auf die bei Start definierten Properties
     */
    @Autowired
    TerraformProperties properties;

    public static final Pattern EXPANSION_VARIABLE_PATTERN = Pattern.compile("\\$\\{[a-zA-Z0-9_]+}");

    /**
     * Formatiert und speichert die übergebenen Inputs in einer Konfigurationsdatei
     */
    public String formatAndSave(FormatInputs inputs) throws IOException {
        String formattedConfig = format(inputs);
        return save(formattedConfig);
    }

    /**
     * Formatiert die übergebenen Inputs in einem String
     * <p>
     * Hierzu wird eine Vorlage eingelesen, auf Vorkomnisse von ${VAR} (mit VAR einem bliebigen Variablennamen) überprüft
     * und dies durch die vom Nutzer übergebenen Variablenwerte ersetzt
     */
    private String format(FormatInputs inputs) throws IOException {
        // Nutzer-Inputs formatieren & Variablen-Namen der Vorlage zuordnen
        Map<String, String> variableValues = getTerraformVariableMap(inputs);
        // Vorlage einlesen
        String templateString = Files.readString(Path.of(properties.getTemplateFile()));
        // Matcher zur Erkennung von Variablen
        Matcher matcher = EXPANSION_VARIABLE_PATTERN.matcher(templateString);
        // StringBuilder zum Konstruieren des Ergebnisses
        StringBuilder result = new StringBuilder();
        // am Start der Vorlage beginnen
        int start = 0;
        // solange neue Variablen gefunden werden ...
        while (matcher.find(start)) {
            // bisherigen Text ains Ergebnis übernehmen
            result.append(templateString, start, matcher.start());
            // Variablenname auslesen
            String variable = matcher.group();
            String variableName = variable.substring(2, matcher.group().length() - 1);
            // (formatierten) Wert der Variable aus Nutzereingaben beziehen
            String variableValue = variableValues.get(variableName);
            // Variablenwert anstelle der Variable übernehmen
            result.append(variableValue);
            // hinter Variable weitersuchen
            start = matcher.end();
        }
        // Text nach letzter Variable übernehmen
        result.append(templateString, start, templateString.length());

        return result.toString();
    }

    /**
     * Formatiert die Inputs eines Users und ordnet sie den Namen von Variablen in der Terraform-Konfigurationsvorlage zu
     *
     * @return Map der Variablennamen und ihrem zugehörigen formatierten einzufügenden Inhalt
     */
    public Map<String, String> getTerraformVariableMap(FormatInputs inputs) {
        HashMap<String, String> map = new HashMap<>();
        map.put("CPU_COUNT", inputs.getCpuCount().toString());
        map.put("MEMORY_SIZE", inputs.getMemorySize().toString());
        map.put("DISKS", formatHardDrivesSection(inputs));
        return map;
    }

    /**
     * Formatiert die Inputs eines Users bezüglich der gewählten Festplatten (Name, Gräße) zu einem entsprechenden
     * Abschnitt der Konfiguration
     * <p>
     * Notwendig, da die zweite Festplatte optional ist
     */
    private String formatHardDrivesSection(FormatInputs inputs) {
        StringBuilder format = new StringBuilder();
        format.append(formatHardDriveSection(inputs.getDiskLabel(), inputs.getDiskSize()));
        if (inputs.getSecondaryDiskSize() > 0) {
            format.append("\t");
            format.append(formatHardDriveSection(inputs.getSecondaryDiskLabel(), inputs.getSecondaryDiskSize()));
        }
        return format.toString();
    }

    /**
     * Formatiert die Inputs eines Users bezüglich einer einzelnen gewählten Festplatte zu einem entsprechenden Abschnitt
     * der Konfiguration
     */
    private String formatHardDriveSection(String label, Integer size) {
        return String.format("""
                \tdisk {
                \t\tlabel = "%s"
                \t\tsize = %s
                \t}
                """, label, size);
    }

    /**
     * Speichert den übergebenen String in einer Datei mit festgelegtem Namen. Ist eine solche Datei bereits vorhanden,
     * wird ein Suffix hochgezählt, bis ein freier Name gefunden wurde.
     */
    private String save(String formattedConfig) throws IOException {
        int i = 0;
        // Ziel-Datei
        File outputFile = Path.of(properties.getOutputFolder(), properties.getOutputFileName() + ".tf").toFile();
        // ist Datei schon vorhanden
        while (outputFile.exists()) {
            // in dem Fall suffix verwenden & hochzählen
            outputFile = Path.of(properties.getOutputFolder(), properties.getOutputFileName() + "_" + i + ".tf").toFile();
            i++;
        }
        // ungenutzen Namen verwenden, um Datei zu schreiben
        Files.writeString(outputFile.toPath(), formattedConfig);
        // Namen zurückgeben
        return outputFile.getName();
    }
}
