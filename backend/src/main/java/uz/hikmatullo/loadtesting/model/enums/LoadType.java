package uz.hikmatullo.loadtesting.model.enums;

public enum LoadType {
    FIXED,        // Constant load
    RAMP_UP,      // Users gradually increase
    SPIKE,        // Sudden spike
    BURST,        // Stress test
    FIXED_RPS     // Maintain specific RPS
}
