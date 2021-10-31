package com.merendabot.university.subjects;

import com.merendabot.university.Merenda;

import javax.xml.transform.Result;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
        try (PreparedStatement statement = Merenda.getInstance().getConnection().prepareStatement(
                "select *\n" +
                        "from university_professor\n" +
                        "where id = ?;"
        )) {
            ResultSet rs = statement.executeQuery();
            statement.setInt(1, id);
            if (rs.next())
                return getProfessorFromRS(rs);
            else
                return null;
        }
    }

    static List<Professor> getProfessors() throws SQLException {
        List<Professor> professors = new ArrayList<>();
        try (PreparedStatement statement = Merenda.getInstance().getConnection().prepareStatement(
                "select *\n" +
                        "from university_professor up\n" +
                        "inner join public.university_subject us on up.subject_id = us.id\n" +
                        "order by us.id;"
        )) {
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                professors.add(getProfessorFromRS(rs));
            }
            return professors;
        }
    }
}
