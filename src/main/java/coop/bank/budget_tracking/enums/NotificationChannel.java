package coop.bank.budget_tracking.enums;

public enum NotificationChannel {
    SMS("SMS"),
    EMAIL("Email"),
    PUSH("Push Notification"),
    IN_APP("In-App Notification");

    private final String displayName;

    NotificationChannel(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
