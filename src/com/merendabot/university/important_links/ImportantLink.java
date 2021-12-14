package com.merendabot.university.important_links;

import com.merendabot.Merenda;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public interface ImportantLink {

    /**
     * Gets the id of an ImportantLink.
     * The id is equal to the primary key inside the database.
     *
     * @return The id of the ImportantLink
     */
    int getId();

    String getGuildId();

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

    /**
     * Gets all important links from the database.
     *
     * @return A List of ImportantLinks
     * @throws SQLException if an SQL Error occurs.
     */
    static List<ImportantLink> getLinks() throws SQLException {
        List<ImportantLink> importantLinks = new ArrayList<>();
        try (PreparedStatement statement = Merenda.getInstance().getConnection().prepareStatement(
                "select * " +
                        "from guild_link " +
                        "left outer join guild_subject gs on guild_link.subject_id = gs.id;"
        )) {
            ResultSet rs = statement.executeQuery();
            while (rs.next())
                importantLinks.add(getLinkFromRS(rs));
            return importantLinks;
        }
    }

    /**
     * Gets an ImportantLink object from a ResultSet.
     * The ResultSet.next() method should be called before calling this method.
     *
     * @param rs A ResultSet object
     * @return An ImportantLink object
     * @throws SQLException if an SQL Error occurs
     */
    static ImportantLink getLinkFromRS(ResultSet rs) throws SQLException {
        return new ImportantLinkClass(
                rs.getInt(1),           // ID
                rs.getString(2),        // GUILD ID
                rs.getString(3),        // NAME
                rs.getString(4),           // LINK
                rs.getInt(5)            // SUBJECT ID
        );
    }
}
