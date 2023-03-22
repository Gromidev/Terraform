package de.steger.terraform.options;

import de.steger.terraform.types.FormatInputOption;
import lombok.Getter;
import lombok.Setter;

/**
 * Option zum Inputs in eine Konfiguration, die als Textfeld dargestellt werden
 */
@Getter
@Setter
public class TextOption extends FormatInputOption {
    /**
     * Platzhalter für die Option, solange der Nutzer keinen Wert eingibt
     */
    private String placeholder;
    public TextOption(String label, String name, String placeholder) {
        super("text", label, name);
        this.placeholder = placeholder;
    }
}