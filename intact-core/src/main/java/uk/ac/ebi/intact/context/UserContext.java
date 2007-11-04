package uk.ac.ebi.intact.context;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import uk.ac.ebi.intact.business.IntactException;
import uk.ac.ebi.intact.config.DataConfig;

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

    public UserContext( String userId, String userPassword ) {
        this.userId = userId;
        this.userPassword = userPassword;
    }

    public static UserContext getCurrentInstance( IntactSession session ) {
        if ( session.isRequestAvailable() ) {
            Object obj = session.getAttribute( SESSION_ATT_NAME );

            if ( obj != null ) {
                return ( UserContext ) obj;
            }
        }

        UserContext userContext = createDefaultUserContext( session );

        if ( session.isRequestAvailable() ) {
            session.setAttribute( SESSION_ATT_NAME, userContext );
        }

        return userContext;
    }

    private static UserContext createDefaultUserContext( IntactSession session ) {
        if ( log.isDebugEnabled() ) {
            log.debug( "Creating UserContext..." );
        }

        DataConfig defaultDataConfig = RuntimeConfig.getCurrentInstance( session ).getDefaultDataConfig();
        Configuration configuration = (Configuration) defaultDataConfig.getConfiguration();

        String currentUser = configuration.getProperty( Environment.USER );
        String password = configuration.getProperty( Environment.PASS );

        if (currentUser == null) {
            currentUser = "UNKNOWN";
            log.warn("No current user found. Using: "+currentUser);
        }

        UserContext userContext = new UserContext( currentUser, password );

        return userContext;
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
