package bg.sofia.uni.fmi.mjt.dealership.server;

import bg.sofia.uni.fmi.mjt.dealership.server.users.User;
import bg.sofia.uni.fmi.mjt.dealership.server.users.UserRole;

public record DataContainer(String message, UserRole role, User user) {
}
