package com.merendabot.university;

import com.merendabot.commands.CommandHandler;
import com.merendabot.commands.exceptions.CommandNameAlreadyExistsException;
import com.merendabot.university.polls.PollHandler;
import com.merendabot.university.timers.TimerHandler;

import java.sql.*;
import java.util.*;


public class Merenda  {
    private static final String URL = "jdbc:postgresql://localhost/merendabot";

    public static Connection connection;

    private CommandHandler commandHandler;
    private PollHandler pollHandler;
    private TimerHandler timerHandler;

    public Merenda() throws CommandNameAlreadyExistsException, SQLException {
        Properties properties = new Properties();
        properties.setProperty("user", System.getenv("DATABASE_USER"));
        properties.setProperty("password", System.getenv("DATABASE_PASSWORD"));

        connection = DriverManager.getConnection(URL, properties);

        commandHandler = new CommandHandler();
        pollHandler = new PollHandler();
        timerHandler = new TimerHandler();
    }

    public Connection getConnection() {
        return connection;
    }

    public CommandHandler getCommandHandler() {return commandHandler;}

    public PollHandler getPollHandler() {return pollHandler;}

    public TimerHandler getTimerHandler() {return timerHandler;}
}
