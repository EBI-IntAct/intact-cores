package uk.ac.ebi.intact.jami.model.listener;

import psidev.psi.mi.jami.model.Checksum;
import psidev.psi.mi.jami.utils.ChecksumUtils;
import uk.ac.ebi.intact.jami.model.extension.IntactPolymer;
import uk.ac.ebi.intact.jami.model.extension.InteractorChecksum;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

/**
 * This listener listen to Polymer object pre update/persist/load events
 * and set crc64 accordingly to existing annotations
 * This listener is for backward compatibility only with previous intact-core.
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>07/01/14</pre>
 */
public class PolymerCrc64Listener {

    @PrePersist
    @PreUpdate
    public void prePersist(IntactPolymer intactPolymer) {
        if (!intactPolymer.getChecksums().isEmpty()){
            Checksum check = ChecksumUtils.collectFirstChecksumWithMethod(intactPolymer.getChecksums(), null, "crc64");
            if (check != null){
                intactPolymer.setCrc64(check.getValue());
            }
            else{
                intactPolymer.setCrc64(null);
            }
        }
        else{
            intactPolymer.setCrc64(null);
        }
    }

    @PostLoad
    public void postLoad(IntactPolymer intactPolymer) {
        if (intactPolymer.getCrc64() != null){
            Checksum check = ChecksumUtils.collectFirstChecksumWithMethod(intactPolymer.getChecksums(), null, "crc64");
            if (check != null && !intactPolymer.getCrc64().equals(check.getValue())){
                intactPolymer.getChecksums().remove(check);
                intactPolymer.getChecksums().add(new InteractorChecksum(check.getMethod(), intactPolymer.getCrc64()));
            }
            else{
                intactPolymer.getChecksums().add(new InteractorChecksum(IntactUtils.createMITopic("crc64", null), intactPolymer.getCrc64()));
            }
        }
    }
}
