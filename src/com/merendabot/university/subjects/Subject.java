package com.merendabot.university.subjects;

import com.merendabot.university.Merenda;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public interface Subject {

    int getId();

    String getName();

    String getShortName();

    List<Professor> getProfessors();

    void addProfessor(Professor professor);

    static Subject getSubjectFromRS(ResultSet rs) throws SQLException {
        return new SubjectClass(
                rs.getInt(1),
                rs.getString(2),
                rs.getString(3)
        );
    }

    static Subject getSubjectFromRS(ResultSet rs, int start) throws SQLException {
        return new SubjectClass(
                rs.getInt(start),
                rs.getString(start+1),
                rs.getString(start+2)
        );
    }

    static Subject getSubjectById(int id) {
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
