package bg.sofia.uni.fmi.mjt.dealership.server.command;

import bg.sofia.uni.fmi.mjt.dealership.server.model.DataContainer;

/**
 * The Command interface provides the functionality for executing a command.
 * It provides a method to execute the command and return a DataContainer with the result of the operation.
 */
public interface Command {

    /**
     * Executes the command and returns a DataContainer with the result of the operation.
     *
     * @return a DataContainer with the result of the operation
     */
    DataContainer execute();
}
