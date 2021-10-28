package university.subjects;

import university.important_links.ImportantLink;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SubjectClass implements Subject {

    private int id;
    private final String name;
    private String short_name;

    private final List<Professor> professorList;
    private final List<ImportantLink> importantLinkList;

    public SubjectClass(int id, String name, String short_name) {
        this.id = id;
        this.name = name;
        this.short_name = short_name;
        this.professorList = new ArrayList<>();
        this.importantLinkList = new ArrayList<>();
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
    public String getShortName() {
        return short_name;
    }

    @Override
    public List<Professor> getProfessors() {
        return this.professorList;
    }

    @Override
    public void addProfessor(Professor professor) {
        this.professorList.add(professor);
    }
}
