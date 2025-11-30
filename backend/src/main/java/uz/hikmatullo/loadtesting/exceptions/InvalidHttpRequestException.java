package uz.hikmatullo.loadtesting.exceptions;

public class InvalidHttpRequestException extends RuntimeException{
    public InvalidHttpRequestException(String message) {
        super(message);
    }
}
