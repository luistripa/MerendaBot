# Merenda Bot
A bot to manage tests, assignments and classes,
and to warn when an event is about to happen.

# Environment Variables

<table>
    <tr>
        <th>Variable Name</th>
        <th>Required?</th>
        <th>Variable Value Description</th>
    </tr>
    <tr style="text-align: center;">
        <td>TOKEN</td>
        <td>Yes</td>
        <td>The discord bot token.</td>
    </tr>
    <tr style="text-align: center;">
        <td>DATABASE_USER</td>
        <td>Yes</td>
        <td>The user for the database connection.</td>
    </tr>
    <tr style="text-align: center;">
        <td>DATABASE_PASSWORD</td>
        <td>Yes</td>
        <td>The password for the database connection.</td>
    </tr>
    <tr style="text-align: center;">
        <td>DEBUG</td>
        <td>No (Default FALSE)</td>
        <td>Enable/Disable debug mode (TRUE or FALSE respectively)</td>
    </tr>
</table>

# Database connection
The bot was programmed to support connection to a PostgreSQL database.

You must create a database called `merendabot` and run the `create.sql` file to create all tables.

**WARNING!** The file `create.sql` deletes **<u>all tables, constraints and types</u>** with the same names before creating the tables.
Make sure you're executing it on an empty database.


# Commands

The bot possesses the following commands:
- [x] list of all classes
- [x] list of classes happening now
- [x] list of tests
- [x] list of assignments
- [x] list of professors (and their emails)
- [x] list of important links
- [x] voting
- [ ] multi-choice polls


# Timers (periodic alerts)

The bot possesses the following timers:
- [x] class alert (alerts when a class is about to start)
- [x] weekly reports (reports all tests and assignments for the week)
- [x] test alert (alerts when a test is to be done soon)
- [ ] assignment alert (alerts when an assignment is to be delivered soon)
