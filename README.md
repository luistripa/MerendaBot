# Merenda Bot
In a pandemic-driven year, we faced a major constraint: professors used several online platforms to publish their Zoom
meeting URL's and other important information and this made it very confusing for the not so organised, but highly technical, students :)

With this, MerendaBot came to life. It has multiple functions that make organizing online classes a piece of cake.

Some functionality includes:

- Storing information about classes, assignments and tests, including their links and other info
- Messaging a default channel when a class, test or assignment is to be done
- A voting system, that includes simple for-against votes or multiple choice polls

# Requirements

The bot requires `applications.commands` and `bot` scopes in the OAuth2 URL generator.

# Environment Variables

<table>
    <tr>
        <th>Variable Name</th>
        <th>Required?</th>
        <th>Variable Value Description</th>
    </tr>
    <tr style="text-align: center;">
        <td>MERENDABOT_TOKEN</td>
        <td>Yes</td>
        <td>The discord bot token.</td>
    </tr>
    <tr style="text-align: center;">
        <td>MERENDABOT_DB_NAME</td>
        <td>Yes</td>
        <td>The name of the database to connect to.</td>
    </tr>
    <tr style="text-align: center;">
        <td>MERENDABOT_DB_USER</td>
        <td>Yes</td>
        <td>The user for the database connection.</td>
    </tr>
    <tr style="text-align: center;">
        <td>MERENDABOT_DB_PASSWORD</td>
        <td>Yes</td>
        <td>The password for the database connection.</td>
    </tr>
    <tr style="text-align: center;">
        <td>MERENDABOT_DEBUG</td>
        <td>No (Default FALSE)</td>
        <td>Enable/Disable debug mode (TRUE or FALSE respectively)</td>
    </tr>
</table>

# Database connection
The bot was programmed to support connection to a PostgreSQL database.

You must create an **empty** database and set the `MERENDABOT_DB_NAME` environment table with the database's name.

After running the bot for the first time, all necessary tables will be created and the bot may be used normally.


# Commands

Commands are available only through slash commands. These propagate instantaneously to guilds since they are guild commands instead of global commands.

The bot possesses the following commands:
- [x] list of all classes
- [x] list of classes happening now
- [x] list of tests
- [x] list of assignments
- [x] list of professors (and their emails)
- [x] list of important links
- [x] voting
- [x] multi-choice polls
- [x] list/add/edit/remove class/assignment/test/subject/professor entries


# Timers (periodic alerts)

The bot possesses the following event timers:
- [x] class alert (alerts when a class is about to start)
- [x] weekly reports (reports all tests and assignments for the week)
- [x] test alert (alerts when a test is to be done soon)
- [ ] assignment alert (alerts when an assignment is to be delivered soon)
