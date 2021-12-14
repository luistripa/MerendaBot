package com.merendabot.university.subjects;

import com.merendabot.Merenda;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public interface Professor {

    /**
     * Gets the id of the professor.
     * This is equal to the primary key inside the database.
     *
     * @return The professor's id
     */
    int getId();

    String getGuildId();

    /**
     * Gets the name of the professor.
     *
     * @return The name of the professor
     */
    String getName();

    /**
     * Gets the email of the professor.
     *
     * @return The email of the professor
     */
    String getEmail();

    /**
     * Gets the subject id linked with the professor.
     *
     * @return The subject id
     */
    int getSubjectId();

    /**
     * Gets a Professor object from a given ResultSet.
     * The ResultSet.next() method shoulb be called before calling this method.
     *
     * @param rs A ResultSet object
     * @return A Professor object
     * @throws SQLException if an SQL Error occurs
     */
    static Professor getProfessorFromRS(ResultSet rs) throws SQLException {
        return new ProfessorClass(
                rs.getInt(1),       // ID
                rs.getString(2),       // GUILD ID
                rs.getString(3),    // NAME
                rs.getString(4),    // EMAIL
                rs.getInt(5)        // SUBJECT ID
        );
    }

    /**
     * Gets a Professor by id.
     *
     * @param id The id of the professor
     * @return A Professor object if found, null otherwise
     * @throws SQLException if an SQL Error occurs
     */
    static Professor getProfessorById(int id) throws SQLException {
        try (PreparedStatement statement = Merenda.getInstance().getConnection().prepareStatement(
                "select *\n" +
                        "from guild_professor\n" +
                        "where id = ?;"
        )) {
            ResultSet rs = statement.executeQuery();
            statement.setInt(1, id);
            if (rs.next())
                return getProfessorFromRS(rs);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Gets all professors in the database.
     *
     * @return A List of Professors
     * @throws SQLException if an SQL Error occurs
     */
    static List<Professor> getProfessors() throws SQLException {
        List<Professor> professors = new ArrayList<>();
        try (PreparedStatement statement = Merenda.getInstance().getConnection().prepareStatement(
                "select *\n" +
                        "from guild_professor gp\n" +
                        "inner join guild_subject gs on gp.subject_id = gs.id\n" +
                        "order by gs.id;"
        )) {
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                professors.add(getProfessorFromRS(rs));
            }
            return professors;
        }
    }
}
