package uz.hikmatullo.loadtesting.model.entity;

import lombok.*;
import uz.hikmatullo.loadtesting.model.enums.LoadType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoadProfile {

    private LoadType type;

    private int virtualUsers;      // Number of simulated users
    private int durationSeconds;   // How long test runs
    private int rampUpSeconds;     // How quickly users start
    private int targetRps;         // Only used for FIXED_RPS type

}
