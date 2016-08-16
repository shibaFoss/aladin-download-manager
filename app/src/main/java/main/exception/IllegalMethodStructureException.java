package main.exception;

public class IllegalMethodStructureException extends Exception {

    public IllegalMethodStructureException(String firstMethodName, String secondMethodName) {
        super("You must call " + firstMethodName + "first before calling " + secondMethodName);
    }
}
