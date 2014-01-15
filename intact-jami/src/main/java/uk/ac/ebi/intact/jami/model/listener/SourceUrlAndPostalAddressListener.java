package uk.ac.ebi.intact.jami.model.listener;

import uk.ac.ebi.intact.jami.model.extension.IntactSource;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

/**
 * This listener listen to Source object pre update/persist/load events
 * and set url/postaladdress accordingly to existing annotations
 * This listener is for backward compatibility only with previous intact-core.
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>07/01/14</pre>
 */
public class SourceUrlAndPostalAddressListener {

    @PrePersist
    @PreUpdate
    public void prePersist(IntactSource intactCv) {
        if (intactCv.isAnnotationCollectionLoaded()){
            if (intactCv.getUrl() != null){
                intactCv.setPersistentURL(intactCv.getUrl());
            }
            else{
                intactCv.setPersistentURL(null);
            }

            if (intactCv.getPostalAddress() != null){
                intactCv.setPersistentPostalAddress(intactCv.getPostalAddress());
            }
            else{
                intactCv.setPersistentPostalAddress(null);
            }
        }
    }
}
