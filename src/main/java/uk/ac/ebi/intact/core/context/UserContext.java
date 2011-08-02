package uk.ac.ebi.intact.core.context;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Transactional;
import uk.ac.ebi.intact.model.user.User;

import java.io.Serializable;
import java.sql.Connection;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class UserContext implements Serializable {

    private User user;

    private Connection connection;

    public UserContext() {
       this("default", "");
    }

    public UserContext( String userId, String userPassword ) {
        user = new User();
        user.setLogin(userId);
        user.setFirstName(userId);
        user.setLastName("N/A");
        user.setPassword(userPassword);
        user.setEmail(userId+"@example.com");
    }

    public UserContext(User user) {
        this.user = user;
    }

    public String getUserId() {
        return user.getLogin();
    }

    public void setUserId( String userId ) {
        this.user.setLogin(userId);
    }

    public String getUserMail() {
        return user.getEmail();
    }

    public void setUserMail( String userMail ) {
        user.setEmail(userMail);
    }

    public String getUserPassword() {
        return user.getPassword();
    }

    public void setUserPassword( String userPassword ) {
        user.setPassword(userPassword);
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection( Connection connection ) {
        this.connection = connection;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
