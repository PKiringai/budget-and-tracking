package coop.bank.budget_tracking.enums;

public enum AlertType {

    THRESHOLD_80("80% budget reached", 80),
    THRESHOLD_100("Budget limit reached", 100),
    EXCEEDED("Budget exceeded", 101);

    private final String description;
    private final int thresholdPercentage;

    AlertType(String description, int thresholdPercentage) {
        this.description = description;
        this.thresholdPercentage = thresholdPercentage;
    }

    public String getDescription() {
        return description;
    }

    public int getThresholdPercentage() {
        return thresholdPercentage;
    }
}
