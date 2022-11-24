package frontend;

public class Logger {
    private final StringBuilder errors;

    public Logger() {
        this.errors = new StringBuilder();
    }

    public void log(char type, int line) {
        errors.append(line).append(" ").append(type).append('\n');
    }

    public String getLog() { return errors.toString(); }
}
