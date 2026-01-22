package lk.sliit.vehiclerental.vehiclerental.model;

public class Truck extends Vehicle {
    private double cargoCapacity;

    public Truck(String id, String make, String model, int year, double price, double cargoCapacity) {
        super(id, make, model, year, price);
        this.cargoCapacity = cargoCapacity;
    }

    public Truck(String id, String make, String model, double price, double cargoCapacity) {
        super(id, make, model, price);
        this.cargoCapacity = cargoCapacity;
    }

    @Override public String getType() { return "Truck"; }

    @Override
    public String toFileString() {
        return String.join("|",
                getId(), getType(), getMake(), getModel(),
                String.valueOf(getDailyRate()), String.valueOf(cargoCapacity)
        );
    }

    @Override
    public String getDisplayDetails() {
        return String.format("Truck: %s %s (%.1f tons capacity) - $%.2f/day",
                getMake(), getModel(), cargoCapacity, getDailyRate());
    }

    // Getters & Setters
    public double getCargoCapacity() { return cargoCapacity; }
    public void setCargoCapacity(double cargoCapacity) { this.cargoCapacity = cargoCapacity; }
}
