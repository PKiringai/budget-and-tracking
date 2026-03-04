package coop.bank.budget_tracking.enums;

public enum PeriodUnit {
    DAILY("Daily"),
    WEEKLY("Weekly"),
    MONTHLY("Monthly"),
    QUARTERLY("Quarterly"),
    YEARLY("Yearly"),
    CUSTOM("Custom");

    private final String displayName;

    PeriodUnit(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
