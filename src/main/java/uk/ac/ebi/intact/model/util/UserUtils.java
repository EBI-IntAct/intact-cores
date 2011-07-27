package uk.ac.ebi.intact.model.util;

import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.model.user.Preference;
import uk.ac.ebi.intact.model.user.Role;
import uk.ac.ebi.intact.model.user.User;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public final class UserUtils {

    private UserUtils() {
    }

    public static boolean isAdmin(User user) {
        return hasRole(user, Role.ROLE_ADMIN);
    }

    public static boolean isReviewer(User user) {
        return hasRole(user, Role.ROLE_REVIEWER);
    }

    public static boolean isCurator(User user) {
        return hasRole(user, Role.ROLE_CURATOR);
    }

    public static boolean hasRole(User user, String roleName) {
        for (Role role : user.getRoles()) {
            if (roleName.equals(role.getName())) {
                return true;
            }
        }

        return false;
    }

    public static Integer getReviewerAvailability(User user) {
        return getReviewerAvailability(user, 100);
    }

    public static Integer getReviewerAvailability(User user, Integer defaultValue) {
        Preference preference = user.getPreference(Preference.KEY_REVIEWER_AVAILABILITY);

        if (preference != null) {
            String value = preference.getValue();

            return Integer.parseInt(value);
        }

        return defaultValue;
    }

    public static void setReviewerAvailability(User user, Integer availability) {
        user.addOrUpdatePreference(Preference.KEY_REVIEWER_AVAILABILITY, String.valueOf(availability));
    }

    public static User getMentorReviewer(IntactContext intactContext, User user) {
        Preference preference = user.getPreference(Preference.KEY_MENTOR_REVIEWER);

        if (preference != null) {
            String mentorAc = preference.getValue();
            return intactContext.getDaoFactory().getUserDao().getByAc(mentorAc);
        }

        return null;
    }

    public static void setMentorReviewer(User user, User mentor) {
        user.addOrUpdatePreference(Preference.KEY_MENTOR_REVIEWER, mentor.getAc());
    }
}
