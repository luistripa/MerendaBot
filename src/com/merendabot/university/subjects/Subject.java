package com.merendabot.university.subjects;

import com.merendabot.university.Merenda;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface Subject {

    int getId();

    String getName();

    String getShortName();

    List<Professor> getProfessors();

    void addProfessor(Professor professor);

    static Subject getSubjectFromRS(ResultSet rs_subjects) throws SQLException {
        return new SubjectClass(
                rs_subjects.getInt(1),
                rs_subjects.getString(2),
                rs_subjects.getString(3)
        );
    }

    static Subject getSubjectFromRS(ResultSet rs_subjects, int start) throws SQLException {
        return new SubjectClass(
                rs_subjects.getInt(start),
                rs_subjects.getString(start+1),
                rs_subjects.getString(start+2)
        );
    }

    static Subject getSubjectById(int id) throws SQLException {
        PreparedStatement statement = Merenda.connection.prepareStatement(
                "select *\n" +
                        "from university_subject\n" +
                        "where id=?;"
        );
        statement.setInt(1, id);
        ResultSet rs = statement.executeQuery();
        if (rs.next())
            return Subject.getSubjectFromRS(rs);
        else
            return null;
    }

    static ResultSet getSubjects() throws SQLException {
        PreparedStatement statement = Merenda.connection.prepareStatement(
                "select *\n"+
                        "from university_subject;"
        );
        return statement.executeQuery();
    }
}
