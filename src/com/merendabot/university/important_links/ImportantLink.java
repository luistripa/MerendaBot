package com.merendabot.university.important_links;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface ImportantLink {

    int getId();

    /**
     * Gets the display name of the link.
     *
     * @return The display name of the link
     */
    String getName();

    /**
     * Gets the URL
     *
     * @return The URL
     */
    String getLink();

    int getSubjectId();

    static ResultSet getLinks(Connection connection) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(
                "select * " +
                        "from university_link " +
                        "left outer join university_subject us on university_link.subject_id = us.id;"
        );
        return statement.executeQuery();
    }

    static ImportantLink getLinkFromRS(ResultSet rs) throws SQLException {
        return new ImportantLinkClass(
                rs.getInt(1),
                rs.getString(2),
                rs.getString(3),
                rs.getInt(4)
        );
    }
}
