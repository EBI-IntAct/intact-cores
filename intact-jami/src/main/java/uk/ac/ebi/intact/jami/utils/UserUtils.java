package uk.ac.ebi.intact.jami.utils;

import psidev.psi.mi.jami.model.Source;
import uk.ac.ebi.intact.jami.dao.SourceDao;
import uk.ac.ebi.intact.jami.dao.UserDao;
import uk.ac.ebi.intact.jami.model.extension.IntactSource;
import uk.ac.ebi.intact.jami.model.user.Preference;
import uk.ac.ebi.intact.jami.model.user.Role;
import uk.ac.ebi.intact.jami.model.user.User;

/**
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

    public static Source getInstitution(SourceDao sourceDao, User user) {
        Preference preference = user.getPreference(Preference.KEY_INSTITUTION_AC);

        if (preference != null) {
            String institutionAc = preference.getValue();
            return sourceDao.getByAc(institutionAc);
        }

        return null;
    }

    public static void setInstitution(User user, IntactSource institution) {
        user.addOrUpdatePreference(Preference.KEY_INSTITUTION_AC, institution.getAc());
        user.addOrUpdatePreference(Preference.KEY_INSTITUTION_NAME, institution.getShortName());
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

    public static User getMentorReviewer(UserDao userDao, User user) {
        if (user != null) {
            Preference preference = user.getPreference(Preference.KEY_MENTOR_REVIEWER);

            if (preference != null) {
                String mentorAc = preference.getValue();
                return userDao.getByAc(mentorAc);
            }
        }

        return null;
    }

    public static void setMentorReviewer(User user, User mentor) {
        if (user != null) {
            if (mentor == null) {
                // If we have null as a mentor, we want to assign random reviewers again and for that we need
                // to remove the preference reviewer
                user.removePreference(user.getPreference(Preference.KEY_MENTOR_REVIEWER));
            } else {
                user.addOrUpdatePreference(Preference.KEY_MENTOR_REVIEWER, mentor.getAc());

            }
        }
    }
}
