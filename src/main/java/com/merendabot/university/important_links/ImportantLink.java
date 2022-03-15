package com.merendabot.university.important_links;

import com.merendabot.GuildManager;
import com.merendabot.Merenda;
import com.merendabot.university.subjects.Subject;
import com.sun.xml.bind.v2.schemagen.xmlschema.Import;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public interface ImportantLink {

    GuildManager getGuild();

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
    String getUrl();

    Subject getSubject();

    static List<ImportantLink> getLinks(Session session) {
        List links;
        links = session.createQuery("from ImportantLinkClass").list();
        return links;
    }
}
