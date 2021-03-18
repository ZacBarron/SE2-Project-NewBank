package newbank.server;

import java.util.ArrayList;

public class Help {

    private String command;
    private String example;
    private String description;

    public Help(String helpCommand, String example, String description) {
        this.command = helpCommand;
        this.example = example;
        this.description = description;
    }

    public String toString() {
        return (command + "\n" + example + "\n" + description);
    }

}
