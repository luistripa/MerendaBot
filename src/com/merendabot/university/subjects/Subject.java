package com.merendabot.university.subjects;

import com.merendabot.university.Merenda;
import org.jetbrains.annotations.Nullable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public interface Subject {

    /**
     * Gets the id of the subject.
     *
     * @return The id of the subject
     */
    int getId();

    /**
     * Gets the name of the subject.
     *
     * @return The name of the subject
     */
    String getName();

    /**
     * Gets a short version of the subject's name.
     *
     * Example: Software Engineering becomes SE
     *
     * @return The subject's short name
     */
    String getShortName();

    /**
     * Gets a Subject object from a given ResultSet.
     * The ResultSet.next() method should be called before calling this method.
     *
     * @param rs A ResultSet object
     * @return A Subject object
     * @throws SQLException if an SQL Error occurs
     */
    static Subject getSubjectFromRS(ResultSet rs) throws SQLException {
        return new SubjectClass(
                rs.getInt(1),
                rs.getString(2),
                rs.getString(3)
        );
    }

    /**
     * Gets a subject by id from the database.
     *
     * @param id The subject's id
     * @return A Subject object if found, null otherwise
     */
    @Nullable static Subject getSubjectById(int id) {
        try (PreparedStatement statement = Merenda.getInstance().getConnection().prepareStatement(
                "select *\n" +
                        "from university_subject\n" +
                        "where id=?;"
        )) {
            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();
            if (rs.next())
                return getSubjectFromRS(rs);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Gets all subject from the database.
     *
     * @return A List of Subjects
     * @throws SQLException if an SQL Error occurs
     */
    static List<Subject> getSubjects() throws SQLException {
        List<Subject> subjects = new ArrayList<>();
        try (PreparedStatement statement = Merenda.getInstance().getConnection().prepareStatement(
                "select *\n" +
                        "from university_subject;"
        )) {
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                subjects.add(getSubjectFromRS(rs));
            }
            return subjects;
        }
    }
}
