package de.steger.terraform.options;

import java.util.List;

import de.steger.terraform.types.FormatInputOption;
import lombok.Getter;
import lombok.Setter;

/**
 * Option zum Inputs in eine Konfiguration, die als Radio-Group dargestellt werden
 */
@Getter
@Setter
public class RadioGroupOption extends FormatInputOption {
    /**
     * Menge von möglichen Auswahlen (= Radio Buttons) für diese Option
     */
    private List<RadioGroupOptionChoice> choices;
    public RadioGroupOption(String label, String name, List<RadioGroupOptionChoice> choices) {
        super("radio", label, name);
        this.choices = choices;
    }
}
