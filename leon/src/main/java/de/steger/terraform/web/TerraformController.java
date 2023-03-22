package de.steger.terraform.web;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import de.steger.terraform.options.RadioGroupOption;
import de.steger.terraform.options.RadioGroupOptionChoice;
import de.steger.terraform.options.TextOption;
import de.steger.terraform.services.TerraformFormatService;
import de.steger.terraform.types.FormatInputOption;
import de.steger.terraform.types.FormatInputs;
import jakarta.validation.Valid;

/**
 * Controller für Interaktionen des Browsers (= der Website) mit dem Server
 * <p>
 * Stellt auf Anfragen des Users hin ein Model bereit, das Daten der Antwort enthält, sowie den Namen einer View, der
 * zur Darstellung dieses Models verwendet wird
 */
@Controller
@RequestMapping("terraform")
public class TerraformController {

    /**
     * Referenz auf Format-Service
     */
    @Autowired
    TerraformFormatService service;

    /**
     * Konstanten für View-Namen
     * <p>
     * Entsprechen HTML-Dateien in /src/main/ressources/templates
     */
    public static final String
            HOME_VIEW = "index",
            CONFIGURATION_VIEW = "config-form",
            RESULT_VIEW = "result";

    /**
     * Optionen, die dem Nutzer im Formular zur Auswahl gestellt werden
     */
    public static final List<FormatInputOption> INPUT_OPTIONS = List.of(
            // CPUs
            new RadioGroupOption("cpuCount", "Anzahl der CPUs", List.of(
                    new RadioGroupOptionChoice("1", "1"),
                    new RadioGroupOptionChoice("2", "2"),
                    new RadioGroupOptionChoice("4", "4"),
                    new RadioGroupOptionChoice("8", "8"))),
            // RAM
            new RadioGroupOption("memorySize", "Größe des Arbeitsspeichers", List.of(
                    new RadioGroupOptionChoice("1024", "1 GB"),
                    new RadioGroupOptionChoice("4096", "4 GB"),
                    new RadioGroupOptionChoice("16384", "16 GB"))),
            // Disk1
            new RadioGroupOption("diskSize", "Größe der ersten Festplatte", List.of(
                    new RadioGroupOptionChoice("20", "20 GB"),
                    new RadioGroupOptionChoice("100", "100 GB"))),
            new TextOption("diskLabel", "Bezeichnung der ersten Festplatte", "disk-0"),
            // Disk2
            new RadioGroupOption("secondaryDiskSize", "Größe der ersten Festplatte", List.of(
                    new RadioGroupOptionChoice("0", "Keine zweite Festplatte"),
                    new RadioGroupOptionChoice("20", "20 GB"),
                    new RadioGroupOptionChoice("100", "100 GB"))),
            new TextOption("secondaryDiskLabel", "Bezeichnung der zweiten Festplatte", "disk-1")
    );

    @ModelAttribute("options") // stellt Optionen in allen Views unter dem Namen "options" bereit
    public List<FormatInputOption> getInputOptions() {
        return INPUT_OPTIONS;
    }

    /**
     * wie auf Anfragen nach /terraform reagiert wird
     */
    @GetMapping
    public String getHomeView() {
        // Home-View anzeigen
        return HOME_VIEW;
    }

    /**
     * Reichert ein Model um die Daten an, die zur Darstellung der Ergebnis-View notwendig sind
     *
     * @param model   Model, das um Werte zur Darstellung der Ergebnis-View erweitert werden soll
     * @param success ob die Operation erfolgreich war
     * @param message die Fehler-/Hinweismeldung
     */
    private void withResult(Model model, boolean success, String message) {
        // Ob Erzeugung der Konfiguration erfolgreich war
        model.addAttribute("success", success);
        // Meldung
        model.addAttribute("message", message);
    }

    /**
     * wie auf Anfragen nach /terraform/config regiert wird
     *
     * @param model Model (anhand dessen die View dargestellt wird). Wird von Spring befüllt anhand von
     *              {@link ModelAttribute} Methoden im Controller
     */
    @GetMapping("config")
    public String getConfigView(Model model) {
        // Standard-Werte belegen
        model.addAttribute("inputs", new FormatInputs());
        // Konfigurations-View anzeigen
        return CONFIGURATION_VIEW;
    }

    /**
     * Wie auf Absenden des Formulars nach /terraform/config reagiert wird
     *
     * @param model         Model (anhand dessen die View dargestellt wird). Wird von Spring befüllt anhand von
     *                      {@link ModelAttribute} Methoden im Controller und den Inputs des Users
     * @param userInputs    Inputs des Users, die im Formular abgesendet wurden. Werden durch {@link ModelAttribute}
     *                      der View unter dem Namen "inputs" bereitgestellt
     * @param bindingResult Ergebnis der Validierung der Inputs
     */
    @PostMapping("config")
    public String postConfigView(Model model,
                                 // Es werden valide FormatInputs als Formular-Parameter erwartet
                                 @ModelAttribute("inputs") @Valid FormatInputs userInputs,
                                 // Ob und welche Fehler dabei aufgetreten sind, kann untersucht werden
                                 BindingResult bindingResult) throws IOException {
        // auf Seite bleiben, falls Eingaben fehlerhaft
        if (bindingResult.hasErrors()) {
            return CONFIGURATION_VIEW;
        }

        // Formatieren und Speichern
        String configurationName = service.formatAndSave(userInputs);

        // Ergebnis belegen
        withResult(model, true, "Konfiguration erfolgreich erzeugt mit Name \"" + configurationName + "\"");
        // Ergebnis-View anzeigen
        return RESULT_VIEW;
    }

    /**
     * Wie auf Fehler bei der Bearbeitung reagiert wird
     *
     * @param model Model (anhand dessen die View dargestellt wird)
     */
    @ExceptionHandler(Exception.class)
    public String handleException(Model model, Exception exception) {
        // Fehler belegen
        withResult(model, false, "Fehler beim Verarbeiten ihrer Anfrage - " + exception.getMessage());
        // Ergebnis-View anzeigen
        return RESULT_VIEW;
    }
}
