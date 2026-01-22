package lk.sliit.vehiclerental.vehiclerental.model;

public class Car extends Vehicle {
    private int seats;

    public Car(String id, String make, String model, int year, double dailyRate, int seats) {
        super(id, make, model, year, dailyRate);
        this.seats = seats;
    }

    public Car(String id, String make, String model, double price, int seats) {
        super(id, make, model, price);
        this.seats = seats;
    }

    @Override
    public String getType() { 
        return "Car"; 
    }

    @Override
    public String toFileString() {
        return String.join("|",
                getId(), getType(), getMake(), getModel(),
                String.valueOf(getDailyRate()), String.valueOf(seats)
        );
    }

    @Override
    public String getDisplayDetails() {
        return String.format("Car: %s %s (%d seats) - $%.2f/day",
                getMake(), getModel(), seats, getDailyRate());
    }

    public int getSeats() { 
        return seats; 
    }

    public void setSeats(int seats) { 
        this.seats = seats; 
    }
}
