package bg.sofia.uni.fmi.mjt.dealership.server.model;

/**
 * The Car record represents a car in the dealership system.
 * It contains details about the car such as VIN, model, year of manufacture, drive type,
 * number of cylinders, engine displacement, and transmission type.
 */
public record Car(String vin, String model, int year, String drive,
                  int cylinders, double displacement, String transmission) {
}
