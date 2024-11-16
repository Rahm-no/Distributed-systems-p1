package TCP;

import java.io.Serializable;

public class Request implements Serializable {

    private String command;
    private Object[] parameters;

    public Request(String command, Object... parameters) {
        this.command = command;
        this.parameters = parameters;
    }

    public String getCommand() {
        return command;
    }

    public Object[] getParameters() {
        return parameters;
    }
}