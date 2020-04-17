package space.delusive.discord.broadcastbot.exception;

public class NoCurrentStreamFoundException extends RuntimeException {
    public NoCurrentStreamFoundException() {
        super();
    }

    public NoCurrentStreamFoundException(String message) {
        super(message);
    }

    public NoCurrentStreamFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoCurrentStreamFoundException(Throwable cause) {
        super(cause);
    }
}
