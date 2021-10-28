package university.important_links;

public class ImportantLinkClass implements ImportantLink {

    private int id;
    private String name;
    private String link;
    private int subject_id;

    public ImportantLinkClass(int id, String name, String link, int subject_id) {
        this.id = id;
        this.name = name;
        this.link = link;
        this.subject_id = subject_id;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getLink() {
        return link;
    }

    @Override
    public int getSubjectId() {
        return subject_id;
    }
}
