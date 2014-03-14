package uk.ac.ebi.intact.jami.model.extension;

import psidev.psi.mi.jami.model.Annotation;
import psidev.psi.mi.jami.model.Checksum;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.utils.comparator.checksum.UnambiguousChecksumComparator;

/**
 * Intact checksum which is a wrapper of an annotation.
 *
 * A checksum wrapper is not persistent but can wrap a persistent annotation
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>10/03/14</pre>
 */

public class IntactChecksumWrapper implements Checksum {

    private Annotation annotation;

    protected IntactChecksumWrapper(Annotation annotation){
        if (annotation == null){
            throw new IllegalArgumentException("The annotation of a checksum wrapper cannot be null");
        }
        this.annotation = annotation;

        if (this.annotation.getValue() == null){
            throw new IllegalArgumentException("The annotation of a checksum wrapper cannot have a null value.");
        }
    }

    public CvTerm getMethod() {
        return this.annotation.getTopic();
    }

    public String getValue() {
        return this.annotation.getValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }

        if (!(o instanceof psidev.psi.mi.jami.model.Checksum)){
            return false;
        }

        return UnambiguousChecksumComparator.areEquals(this, (psidev.psi.mi.jami.model.Checksum) o);
    }

    @Override
    public int hashCode() {
        return UnambiguousChecksumComparator.hashCode(this);
    }

    @Override
    public String toString() {
        return this.annotation.getTopic().toString() + ": " + this.annotation.getValue();
    }
}
