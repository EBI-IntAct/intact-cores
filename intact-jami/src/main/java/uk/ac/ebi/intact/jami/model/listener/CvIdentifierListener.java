package uk.ac.ebi.intact.jami.model.listener;

import org.apache.commons.lang.StringUtils;
import uk.ac.ebi.intact.jami.ApplicationContextProvider;
import uk.ac.ebi.intact.jami.model.context.IntactContext;
import uk.ac.ebi.intact.jami.model.extension.IntactCvTerm;
import uk.ac.ebi.intact.jami.model.sequence.SequenceManager;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.Entity;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

/**
 * This listener listen to Cv object pre update/persist/load events
 * and generates an identifier when it is empty
 * This listener is for backward compatibility only with previous intact-core.
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>07/01/14</pre>
 */
@Entity
public class CvIdentifierListener {

    @PrePersist
    @PostLoad
    @PreUpdate
    public void prePersist(IntactCvTerm intactCv) {
        if (!intactCv.isIdentifierSet()){
            // first look at PSI-MI
            if (intactCv.getMIIdentifier() != null){
                intactCv.setIdentifier(intactCv.getMIIdentifier());
            }
            // then MOD identifier
            else if (intactCv.getMODIdentifier() != null){
                intactCv.setIdentifier(intactCv.getMODIdentifier());
            }
            // then PAR identifier
            else if (intactCv.getPARIdentifier() != null){
                intactCv.setIdentifier(intactCv.getPARIdentifier());
            }
            // then first identifier
            else if (intactCv.getPARIdentifier() != null){
                intactCv.setIdentifier(intactCv.getPARIdentifier());
            }
            // then generate automatic identifier
            else{
                final IntactContext context = ApplicationContextProvider.getBean(IntactContext.class);
                String prefix = "IA";
                if (context != null){
                    prefix = context.getConfig().getLocalCvPrefix();
                }

                SequenceManager seqManager = ApplicationContextProvider.getBean(SequenceManager.class);
                if (seqManager == null){
                    throw new IllegalStateException("The Cv identifier listener needs a sequence manager to automatically generate a cv identifier for backward compatibility. No sequence manager bean " +
                            "was found in the spring context.");
                }
                seqManager.createSequenceIfNotExists(IntactUtils.CV_LOCAL_SEQ, 1);
                String nextIntegerAsString = String.valueOf(seqManager.getNextValueForSequence(IntactUtils.CV_LOCAL_SEQ));
                String identifier = prefix+":" + StringUtils.leftPad(nextIntegerAsString, 4, "0");
                // set identifier
                intactCv.setIdentifier(identifier);
                // TODO add xref ?

            }
        }
    }
}
