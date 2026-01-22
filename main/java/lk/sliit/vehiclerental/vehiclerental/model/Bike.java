package lk.sliit.vehiclerental.vehiclerental.model;

public class Bike extends Vehicle {
    private String engineCC;

    public Bike(String id, String make, String model, int year, double price, String engineCC) {
        super(id, make, model, year, price);
        this.engineCC = engineCC;
    }

    public Bike(String id, String make, String model, double price, String engineCC) {
        super(id, make, model, price);
        this.engineCC = engineCC;
    }

    @Override public String getType() { return "Bike"; }

    @Override
    public String toFileString() {
        return String.join("|",
                getId(), getType(), getMake(), getModel(),
                String.valueOf(getDailyRate()), engineCC
        );
    }

    @Override
    public String getDisplayDetails() {
        return String.format("Bike: %s %s (%s CC) - $%.2f/day",
                getMake(), getModel(), engineCC, getDailyRate());
    }

    // Getters & Setters
    public String getEngineCC() { return engineCC; }
    public void setEngineCC(String engineCC) { this.engineCC = engineCC; }
}