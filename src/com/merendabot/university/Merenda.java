package com.merendabot.university;

import com.merendabot.Main;
import com.merendabot.commands.Command;
import com.merendabot.commands.CommandHandler;
import com.merendabot.university.polls.Poll;
import com.merendabot.university.polls.PollHandler;
import com.merendabot.university.timers.ScheduleTimer;
import com.merendabot.university.timers.TimerHandler;
import net.dv8tion.jda.api.JDA;

import java.sql.*;
import java.util.*;


public class Merenda  {

    private static Merenda instance = null;
    private static JDA jda = null;

    private static final String URL = "jdbc:postgresql://localhost/merendabot";

    private Connection connection;
    private CommandHandler commandHandler;
    private PollHandler pollHandler;
    private TimerHandler timerHandler;

    private Merenda() {

    }

    public static Merenda getInstance() {
        if (instance == null)
            instance = new Merenda();
        return instance;
    }

    public static void setJDA(JDA newJDA) {
        if (instance != null)
            jda = newJDA;
    }

    public static JDA getJDA() {
        return jda;
    }

    public void setup() {
        new Thread(() -> {
            try {
                Merenda.getJDA().awaitReady();
                Properties properties = new Properties();
                properties.setProperty("user", System.getenv("DATABASE_USER"));
                properties.setProperty("password", System.getenv("DATABASE_PASSWORD"));

                connection = DriverManager.getConnection(URL, properties);
                commandHandler = new CommandHandler();
                pollHandler = new PollHandler();
                timerHandler = new TimerHandler();
            } catch (SQLException | InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
                Main.logger.severe("Error seting up merenda. System won't be able to process commands or timers.");
            }
        }).start();
    }

    /*
    SQL CONNECTION -----------------------------------------------------------------------------------------------------
     */
    public Connection getConnection() {
        return connection;
    }


    /*
    COMMANDS -----------------------------------------------------------------------------------------------------------
     */
    public boolean hasCommand(String commandId) {
        return commandHandler.hasCommand(commandId);
    }

    public Command getCommand(String commandId) {
        return commandHandler.getCommand(commandId);
    }

    public Set<String> getCommandCategories() {
        return commandHandler.getCommandCategories();
    }

    public List<Command> getCommandsByCategory(String categoryName) {
        return commandHandler.getCommandsByCategory(categoryName);
    }


    /*
    TIMERS -------------------------------------------------------------------------------------------------------------
     */
    public ScheduleTimer getTimer(String timerId) {
        return timerHandler.getTimer(timerId);
    }


    /*
    POLLS --------------------------------------------------------------------------------------------------------------
     */
    public void addPoll(Poll poll) {
        pollHandler.addPoll(poll);
    }

    public Poll getPoll(String pollId) {
        return pollHandler.getPoll(pollId);
    }

    public void closePoll(String pollId) {
        pollHandler.endPoll(pollId);
    }
}
