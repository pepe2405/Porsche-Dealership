package bg.sofia.uni.fmi.mjt.dealership.server.command;

import bg.sofia.uni.fmi.mjt.dealership.server.DataContainer;

public interface Command {
    DataContainer execute();
}
