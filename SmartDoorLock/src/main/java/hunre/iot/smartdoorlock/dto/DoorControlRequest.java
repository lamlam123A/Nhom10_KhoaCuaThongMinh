package hunre.iot.smartdoorlock.dto;

public class DoorControlRequest {
    private boolean isOpen;

    public boolean isOpen() {
        return isOpen;
    }

    public void setIsOpen(boolean isOpen) {
        this.isOpen = isOpen;
    }
}