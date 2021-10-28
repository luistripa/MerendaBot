package university.subjects;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProfessorClass implements Professor {

    private int id;
    private String name;
    private String email;
    private int subject_id;
    private Subject subject;

    public ProfessorClass(int id, String name, String email, int subject_id) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.subject_id = subject_id;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getEmail() {
        return this.email;
    }

    @Override
    public int getSubjectId() {
        return subject_id;
    }

    @Override
    public Subject getSubject() {
        return subject;
    }

    @Override
    public void setSubject(Subject subject) {
        this.subject = subject;
    }
}
