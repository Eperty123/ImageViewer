package bll;

public class Logger {
    private static Logger instance;

    public static Logger getInstance() {
        if (instance == null) instance = new Logger();
        return instance;
    }

    public void log(String txt) {
        System.out.println(String.format("[%s]: %s", getClass().getSimpleName(), txt));
    }
}
