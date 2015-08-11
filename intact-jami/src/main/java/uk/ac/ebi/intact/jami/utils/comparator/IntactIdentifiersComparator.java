package uk.ac.ebi.intact.jami.utils.comparator;

import psidev.psi.mi.jami.model.Source;
import psidev.psi.mi.jami.model.Xref;
import psidev.psi.mi.jami.utils.XrefUtils;
import psidev.psi.mi.jami.utils.comparator.CollectionComparator;
import psidev.psi.mi.jami.utils.comparator.xref.UnambiguousExternalIdentifierComparator;
import uk.ac.ebi.intact.jami.ApplicationContextProvider;
import uk.ac.ebi.intact.jami.context.IntactContext;
import uk.ac.ebi.intact.jami.model.extension.IntactSource;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A comparator for collections of identifiers.
 *
 * Two collections are equals if they have the same content and the same size.
 * The smallest collection will come before the longest collection.
 *
 * This comparator ignore Acs
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>20/12/12</pre>
 */

public class IntactIdentifiersComparator extends CollectionComparator<Xref> {

    private Source institution;
    private String acPrefix;

    /**
     * Creates a new CollectionComparator. It requires a Comparator for the obejcts in the Collection
     */
    public IntactIdentifiersComparator(){
        super(new UnambiguousExternalIdentifierComparator());

        final IntactContext context = ApplicationContextProvider.getBean("intactJamiContext");
        this.acPrefix = "EBI-";
        if (context != null){
            this.acPrefix = context.getIntactConfiguration().getAcPrefix() != null? context.getIntactConfiguration().getAcPrefix() : "EBI-";
            this.institution = context.getIntactConfiguration().getDefaultInstitution();
        }
        else{
            this.institution = new IntactSource("unknown");
        }
    }

    public UnambiguousExternalIdentifierComparator getObjectComparator() {
        return (UnambiguousExternalIdentifierComparator)super.getObjectComparator();
    }

    /**
     * Two collections are equals if they have the same content and the same size.
     * The smallest collection will come before the longest collection.
     * @param ts1
     * @param ts2
     * @return
     */
    public int compare(Collection<? extends Xref> ts1, Collection<? extends Xref> ts2) {

        int EQUAL = 0;
        int BEFORE = -1;
        int AFTER = 1;

        if (ts1 == ts2){
            return EQUAL;
        }
        else if (ts1 == null){
            return AFTER;
        }
        else if (ts2 == null){
            return BEFORE;
        }
        else {
            if (this.institution != null){
                Collection<Xref> identifiers1 = new ArrayList<Xref>(ts1.size());
                filterGeneratedIdentifiers(identifiers1, ts1);
                Collection<Xref> identifiers2 = new ArrayList<Xref>(ts2.size());
                filterGeneratedIdentifiers(identifiers2, ts2);

                return super.compare(identifiers1, identifiers2);
            }
            else {
                // compare collection size
                return super.compare(ts1, ts2);
            }
        }
    }

    private void filterGeneratedIdentifiers(Collection<Xref> identifiers, Collection<? extends Xref> originalXrefs) {
        for (Xref ref : originalXrefs){
            if (XrefUtils.isXrefFromDatabase(ref, this.institution.getMIIdentifier(), this.institution.getShortName())
                    && XrefUtils.doesXrefHaveQualifier(ref, Xref.IDENTITY_MI, Xref.IDENTITY) &&
                    ref.getId().startsWith(acPrefix)){
                // ignore this identifier
            }
            else{
                identifiers.add(ref);
            }
        }
    }
}
