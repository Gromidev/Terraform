package de.steger.terraform.types;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Stellt Inputs des Users
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class FormatInputs {
    @NotNull
    @Min(1)
    @Max(8)
    private Integer cpuCount = 1;
    @NotNull
    @Min(1024)
    @Max(16 * 1024)
    private Integer memorySize = 1024;
    @NotEmpty
    private String diskLabel = "disk0";
    @NotNull
    @Min(20)
    @Max(100)
    private Integer diskSize = 20;
    @Nullable
    private String secondaryDiskLabel = "disk1";
    @NotNull
    @PositiveOrZero
    @Max(100)
    private Integer secondaryDiskSize = 0;

}