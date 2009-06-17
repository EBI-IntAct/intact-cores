package uk.ac.ebi.intact.core.context;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;
import java.sql.Connection;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class UserContext implements Serializable {

    private static final Log log = LogFactory.getLog( UserContext.class );

    private static final String SESSION_ATT_NAME = UserContext.class.getName();

    private String userId = null;
    private String userMail;
    private String userPassword;

    private Connection connection;

    public UserContext() {
        this.userId = "default";
        this.userPassword = "";
    }

    public UserContext( String userId, String userPassword ) {
        this.userId = userId;
        this.userPassword = userPassword;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId( String userId ) {
        this.userId = userId;
    }

    public String getUserMail() {
        if ( userMail == null ) {
            return userId + "@ebi.ac.uk";

        }
        return userMail;
    }

    public void setUserMail( String userMail ) {
        this.userMail = userMail;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword( String userPassword ) {
        this.userPassword = userPassword;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection( Connection connection ) {
        this.connection = connection;
    }

}
