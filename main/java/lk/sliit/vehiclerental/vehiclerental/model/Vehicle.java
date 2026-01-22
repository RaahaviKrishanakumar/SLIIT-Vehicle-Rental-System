package lk.sliit.vehiclerental.vehiclerental.model;

public abstract class Vehicle {
    protected String id;
    protected String make;
    protected String model;
    protected int year;
    protected double dailyRate;
    protected boolean isAvailable;

    public Vehicle(String id, String make, String model, int year, double dailyRate) {
        this.id = id;
        this.make = make;
        this.model = model;
        this.year = year;
        this.dailyRate = dailyRate;
        this.isAvailable = true;
    }

    public Vehicle(String id, String make, String model, double dailyRate) {
        this.id = id;
        this.make = make;
        this.model = model;
        this.year = java.time.Year.now().getValue(); // Default to current year
        this.dailyRate = dailyRate;
        this.isAvailable = true;
    }

    // Getters and Setters
    public String getId() { return id; }
    public String getMake() { return make; }
    public String getModel() { return model; }
    public int getYear() { return year; }
    public double getDailyRate() { return dailyRate; }
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }

    public abstract String getType();
    public abstract String toFileString();
    public abstract String getDisplayDetails();
}
