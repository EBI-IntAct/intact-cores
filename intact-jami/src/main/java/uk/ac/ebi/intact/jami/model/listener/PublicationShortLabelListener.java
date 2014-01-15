package uk.ac.ebi.intact.jami.model.listener;

import uk.ac.ebi.intact.jami.model.extension.IntactPublication;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

/**
 * This listener listen to Publication object pre update/persist/load events
 * and set shortlabel accordingly to existing identifiers
 * This listener is for backward compatibility only with previous intact-core.
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>07/01/14</pre>
 */
public class PublicationShortLabelListener {

    @PrePersist
    @PreUpdate
    public void prePersist(IntactPublication pub) {
        // set shortlabel if not done yet
        if (pub.getPubmedId() != null){
            pub.setShortLabel(pub.getPubmedId());
        }
        else if (pub.getDoi() != null){
            pub.setShortLabel(pub.getDoi());
        }
        else if (!pub.getIdentifiers().isEmpty()){
            pub.setShortLabel(pub.getIdentifiers().iterator().next().getId());
        }
    }
}
