package uk.ac.ebi.intact.core.persistence.svc.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.core.persistence.dao.user.UserDao;
import uk.ac.ebi.intact.core.persistence.svc.UserService;
import uk.ac.ebi.intact.core.persistence.svc.UserServiceException;
import uk.ac.ebi.intact.core.persister.CorePersister;
import uk.ac.ebi.intact.model.user.Preference;
import uk.ac.ebi.intact.model.user.Role;
import uk.ac.ebi.intact.model.user.User;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * A service allowing to import/update users in batches.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 2.5
 */
@Component
public class UserServiceImpl implements UserService {

    @Autowired
    private CorePersister corePersister;

    @Autowired(required = true)
    private UserDao userDao;

    public UserServiceImpl() {
    }

    /**
     * Import new users into the local database.
     * <p/>
     * Preferences are updated as follow: missing ones are created, existing ones are updated, those that were already
     * there are left as is.
     *
     * @param users the collection of users to import.
     * @param updateExistingUsers if false, only create new users, otherwise, override all attribute of existing users
     *                            with given ones.
     */
    @Override
    @Transactional
    public void importUsers( Collection<User> users, boolean updateExistingUsers ) {

        final EntityManager entityManager = IntactContext.getCurrentInstance().getDataContext().getDaoFactory().getEntityManager();
        final FlushModeType previousFlushMode = entityManager.getFlushMode();
        entityManager.setFlushMode( FlushModeType.COMMIT );

        for ( User newUser : users ) {
            final User existingUser = userDao.getByLogin( newUser.getLogin() );
            if( existingUser == null ) {
                corePersister.saveOrUpdate( newUser );
            } else {
                if( updateExistingUsers ) {

                    // update existing user
                    existingUser.setPassword( newUser.getPassword() );
                    existingUser.setDisabled( newUser.isDisabled() );
                    existingUser.setFirstName( newUser.getFirstName() );
                    existingUser.setLastName( newUser.getLastName() );
                    existingUser.setEmail( newUser.getEmail() );
                    existingUser.setOpenIdUrl( newUser.getOpenIdUrl() );
                    final Iterator<Preference> preferenceIterator = newUser.getPreferences().iterator();
                    while ( preferenceIterator.hasNext() ) {
                        Preference pref = preferenceIterator.next();

                        final Preference existingPref = existingUser.getPreference( pref.getKey() );
                        if( existingPref != null ) {
                            existingPref.setValue( pref.getValue() );
                        } else {
                            preferenceIterator.remove();
                            pref.setUser( existingUser );
                            existingUser.addPreference( pref );
                        }
                    }
                    existingUser.setRoles( newUser.getRoles() );

                    corePersister.saveOrUpdate( existingUser );
                }
            }
        }

        entityManager.setFlushMode( previousFlushMode );
    }

    ////////////////////////////
    // Import/Export to XML

    private static final String LOGIN_ATTRIBUTE = "login";
    private static final String PASSWORD_ATTRIBUTE = "password";
    private static final String EMAIL_ATTRIBUTE = "email";
    private static final String DISABLED_ATTRIBUTE = "disabled";
    private static final String FIRST_NAME_ATTRIBUTE = "firstName";
    private static final String LAST_NAME_ATTRIBUTE = "lastName";
    private static final String OPEN_ID_URL_ATTRIBUTE = "openIdUrl";
    private static final String KEY_ATTRIBUTE = "key";
    private static final String TRUE_VALUE = "true";

    private static final String USERS_TAG = "users";
    private static final String USER_TAG = "user";
    private static final String ROLES_TAG = "roles";
    private static final String ROLE_TAG = "role";
    private static final String PREFERENCES_TAG = "preferences";
    private static final String PREFERENCE_TAG = "preference";

    public Document buildUsersDocument( Collection<User> users ) throws UserServiceException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder parser = null;
        try {
            parser = factory.newDocumentBuilder();
        } catch ( ParserConfigurationException e ) {
            throw new UserServiceException( "Could not initialize XML parser", e );
        }
        Document doc = parser.newDocument();

        Element root = doc.createElement( USERS_TAG );
        doc.appendChild( root );

        for ( User user : users ) {
            Element xmlUser = doc.createElement( USER_TAG );
            root.appendChild( xmlUser );

            // user
            if ( user.getLogin() != null ) xmlUser.setAttribute( LOGIN_ATTRIBUTE, user.getLogin() );
            if ( user.getPassword() != null ) xmlUser.setAttribute( PASSWORD_ATTRIBUTE, user.getPassword() );
            if ( user.getEmail() != null ) xmlUser.setAttribute( EMAIL_ATTRIBUTE, user.getEmail() );
            if ( user.isDisabled() ) xmlUser.setAttribute( DISABLED_ATTRIBUTE, TRUE_VALUE );
            if ( user.getFirstName() != null ) xmlUser.setAttribute( FIRST_NAME_ATTRIBUTE, user.getFirstName() );
            if ( user.getLastName() != null ) xmlUser.setAttribute( LAST_NAME_ATTRIBUTE, user.getLastName() );
            if ( user.getOpenIdUrl() != null ) xmlUser.setAttribute( OPEN_ID_URL_ATTRIBUTE, user.getOpenIdUrl() );

            // roles
            if ( !user.getRoles().isEmpty() ) {
                Element xmlRoles = doc.createElement( ROLES_TAG );
                xmlUser.appendChild( xmlRoles );

                for ( Role role : user.getRoles() ) {
                    Element xmlRole = doc.createElement( ROLE_TAG );
                    xmlRoles.appendChild( xmlRole );
                    xmlRole.setTextContent( role.getName() );
                }
            }

            // preferences
            if ( !user.getPreferences().isEmpty() ) {
                Element xmlPrefs = doc.createElement( PREFERENCES_TAG );
                xmlUser.appendChild( xmlPrefs );

                for ( Preference pref : user.getPreferences() ) {
                    Element xmlPref = doc.createElement( PREFERENCE_TAG );
                    xmlPrefs.appendChild( xmlPref );
                    xmlPref.setAttribute( KEY_ATTRIBUTE, pref.getKey() );
                    xmlPref.setTextContent( pref.getValue() );
                }
            }
        }

        return doc;
    }

    public Collection<User> readUsersDocument( Document document ) {

        Collection<User> users = new ArrayList<User>();

        // convert document to users
        NodeList root = document.getElementsByTagName( USERS_TAG );
        final NodeList xmlUsers = root.item( 0 ).getChildNodes();
        for ( int i = 0; i < xmlUsers.getLength(); i++ ) {
            Node node = xmlUsers.item( i );

            if( node instanceof Element ) {
                final Element xmlUser = ( Element ) node;
                final User user = new User();
                users.add( user );

                user.setLogin( xmlUser.getAttribute( LOGIN_ATTRIBUTE ) );
                user.setPassword( xmlUser.getAttribute( PASSWORD_ATTRIBUTE ) );
                user.setEmail( xmlUser.getAttribute( EMAIL_ATTRIBUTE ) );
                final String disabled = xmlUser.getAttribute( DISABLED_ATTRIBUTE );
                user.setDisabled( ( disabled == null ? false : ( disabled.equals( TRUE_VALUE ) ? true : false ) ) );
                user.setFirstName( xmlUser.getAttribute( FIRST_NAME_ATTRIBUTE ) );
                user.setLastName( xmlUser.getAttribute( LAST_NAME_ATTRIBUTE ) );
                user.setOpenIdUrl( xmlUser.getAttribute( OPEN_ID_URL_ATTRIBUTE ) );

                // roles
                final NodeList rolesNode = xmlUser.getElementsByTagName( ROLES_TAG );
                if( rolesNode != null && rolesNode.getLength() > 0 ) {
                    final NodeList xmlRoles = rolesNode.item( 0 ).getChildNodes();
                    for ( int r = 0; r < xmlRoles.getLength(); r++ ) {
                        final Node roleNode = xmlRoles.item( r );
                        if( roleNode instanceof Element) {
                            assert ( roleNode.getNodeName().equals( ROLE_TAG ) );
                            String role = roleNode.getTextContent();
                            user.addRole( new Role( role ) );
                        }
                    }
                }

                // preferences
                final NodeList preferencesNode = xmlUser.getElementsByTagName( PREFERENCES_TAG );
                if ( preferencesNode != null && preferencesNode.getLength() > 0 ) {
                    final NodeList xmlPrefs = preferencesNode.item( 0 ).getChildNodes();
                    for ( int p = 0; p < xmlPrefs.getLength(); p++ ) {
                        Node pnode = xmlPrefs.item( p );
                        if( pnode instanceof Element ) {
                            final Element xmlPref = ( Element ) pnode;
                            assert ( xmlPref.getNodeName().equals( PREFERENCE_TAG ) );
                            String key = xmlPref.getAttribute( KEY_ATTRIBUTE );
                            String value = xmlPref.getTextContent();

                            Preference pr = new Preference( user, key );
                            pr.setValue( value );

                            user.getPreferences().add( pr );
                        }
                    }
                }
            }
        }

        return users;
    }

    public void marshallUsers( Collection<User> users, OutputStream os ) throws UserServiceException {

        Document doc = buildUsersDocument( users );
        try {
            TransformerFactory tFactory = TransformerFactory.newInstance();
            tFactory.setAttribute("indent-number", 2);
            Transformer transformer =  tFactory.newTransformer();
            transformer.setOutputProperty( OutputKeys.INDENT, "yes" );

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(os);
            transformer.transform(source, result);
        } catch ( Exception e ) {
            throw new UserServiceException( "Failed to marshall DOM Document", e );
        }
    }

    public Collection<User> parseUsers( InputStream userInputStream ) throws UserServiceException {
        Document doc = null;
        try {
            DocumentBuilderFactory dBF = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = dBF.newDocumentBuilder();
            InputSource is = new InputSource( userInputStream );
            doc = builder.parse( is );
        } catch ( Exception e ) {
            throw new UserServiceException( "Failed to build a DOM Document", e );
        }

        return readUsersDocument( doc );
    }
}
