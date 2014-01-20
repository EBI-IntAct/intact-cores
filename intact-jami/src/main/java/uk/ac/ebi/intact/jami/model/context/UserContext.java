package uk.ac.ebi.intact.jami.model.context;

import org.springframework.stereotype.Component;
import uk.ac.ebi.intact.jami.model.user.User;

import javax.annotation.PostConstruct;

/**
 * The user context
 * @author Marine Dumousseau (baranda@ebi.ac.uk)
 */
@Component
public class UserContext{

    private User user;

    public UserContext() {
       this.user = null;
    }

    public UserContext(String userId, String userPassword) {
        initialiseDefaultUser(userId, userPassword);
    }

    @PostConstruct
    public void initIfNotDone(){
        if (this.user == null){
            initialiseDefaultUser("default", "");
        }
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    private void initialiseDefaultUser(String userId, String userPassword) {
        user = new User(userId, userId, "N/A", userId+"@example.com");
        user.setPassword(userPassword);
    }
}
