package uk.ac.ebi.intact.jami.context;

import org.springframework.stereotype.Component;
import uk.ac.ebi.intact.jami.model.user.User;

/**
 * The user context
 * @author Marine Dumousseau (baranda@ebi.ac.uk)
 */
@Component(value = "jamiUserContext")
public class UserContext{

    private User user;

    public UserContext() {
       this.user = null;
    }

    public UserContext(String userId, String userPassword) {
        initialiseDefaultUser(userId, userPassword);
    }

    public UserContext(User user) {
        this.user = user;
    }

    public String getUserId() {
        return getUser().getLogin();
    }

    public void setUserId( String userId ) {
        this.getUser().setLogin(userId);
    }

    public String getUserMail() {
        return getUser().getEmail();
    }

    public void setUserMail( String userMail ) {
        getUser().setEmail(userMail);
    }

    public String getUserPassword() {
        return getUser().getPassword();
    }

    public void setUserPassword( String userPassword ) {
        getUser().setPassword(userPassword);
    }

    public User getUser() {
        if (this.user == null){
            initialiseDefaultUser("default", "");
        }
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    private void initialiseDefaultUser(String userId, String userPassword) {
        this.user = new User(userId, userId, "N/A", userId+"@example.com");
        this.user.setPassword(userPassword);
    }
}
