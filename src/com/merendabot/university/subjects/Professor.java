package com.merendabot.university.subjects;

import com.merendabot.university.Merenda;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface Professor {

    int getId();

    String getName();

    String getEmail();

    int getSubjectId();

    Subject getSubject();

    void setSubject(Subject subject);

    static Professor getProfessorFromRS(ResultSet rs) throws SQLException {
        return new ProfessorClass(
                rs.getInt(1),
                rs.getString(2),
                rs.getString(3),
                rs.getInt(4));
    }

    static Professor getProfessorById(int id) throws SQLException {

        ResultSet rs;
        try (PreparedStatement statement = Merenda.connection.prepareStatement(
                "select *\n" +
                        "from university_professor\n" +
                        "where id = ?;"
        )) {
            rs = statement.executeQuery();
            statement.setInt(1, id);
        }
        if (rs.next())
            return Professor.getProfessorFromRS(rs);
        return null;
    }

    static ResultSet getProfessors() throws SQLException {
        try (PreparedStatement statement = Merenda.connection.prepareStatement(
                "select *\n" +
                        "from university_professor up\n" +
                        "inner join public.university_subject us on up.subject_id = us.id\n" +
                        "order by us.id;"
        )) {
            return statement.executeQuery();
        }
    }
}
