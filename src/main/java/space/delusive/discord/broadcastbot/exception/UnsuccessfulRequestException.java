package space.delusive.discord.broadcastbot.exception;

public class UnsuccessfulRequestException extends RuntimeException {
    public UnsuccessfulRequestException() {
        super();
    }

    public UnsuccessfulRequestException(String message) {
        super(message);
    }

    public UnsuccessfulRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsuccessfulRequestException(Throwable cause) {
        super(cause);
    }
}
