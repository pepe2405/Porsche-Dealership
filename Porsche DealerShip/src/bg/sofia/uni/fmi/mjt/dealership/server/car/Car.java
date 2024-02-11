package bg.sofia.uni.fmi.mjt.dealership.server.car;

public record Car(String vin, String model, int year, String drive, int cylinders, double displacement,
                  String transmission) {
}
