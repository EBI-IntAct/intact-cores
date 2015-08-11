package uk.ac.ebi.intact.core.persistence.svc;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import uk.ac.ebi.intact.model.user.Preference;
import uk.ac.ebi.intact.model.user.Role;
import uk.ac.ebi.intact.model.user.User;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;

/**
 * User Service.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.5
 */
public interface UserService {

    void importUsers( Collection<User> users, boolean updateExistingUsers );

    Document buildUsersDocument( Collection<User> users ) throws UserServiceException;

    Collection<User> readUsersDocument( Document document );

    void marshallUsers( Collection<User> users, OutputStream os ) throws UserServiceException;

    Collection<User> parseUsers( InputStream userInputStream ) throws UserServiceException;
}
