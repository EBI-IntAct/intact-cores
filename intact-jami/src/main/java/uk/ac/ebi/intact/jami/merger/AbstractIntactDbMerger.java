package uk.ac.ebi.intact.jami.merger;

import psidev.psi.mi.jami.enricher.MIEnricher;
import psidev.psi.mi.jami.enricher.exception.EnricherException;
import uk.ac.ebi.intact.jami.model.audit.Auditable;

import java.util.Collection;

/**
 * Abstract class for IntAct merger of Auditable objects in IntAct.
 *
 * It will use a basic enricher to enrich basic properties
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>29/01/14</pre>
 */

public abstract class AbstractIntactDbMerger<I extends Object, A extends Auditable> implements IntactDbMerger<I,A> {

    private MIEnricher<I> basicEnricher;
    private Class<A> intactClass;

    public AbstractIntactDbMerger(Class<A> intactClass) {
        if (intactClass == null){
            throw new IllegalArgumentException("The intact class must be provided");
        }
        this.intactClass = intactClass;
    }

    public AbstractIntactDbMerger(Class<A> intactClass, MIEnricher<I> basicEnricher) {
        this(intactClass);
        this.basicEnricher = basicEnricher;
    }

    public A merge(A obj1, A obj2) {
        if (obj2 == null){
            return obj1;
        }
        else if (obj1 == null){
            return obj2;
        }
        else{
            mergeCreatedDate(obj1, obj2);
            mergeUpdateDate(obj1, obj2);
            mergeCreator(obj1, obj2);
            mergeUpdator(obj1, obj2);

            mergeOtherProperties(obj1, obj2);

            if (this.basicEnricher != null){
                // then merge general properties
                try {
                    // WARNING: we enrich the object loaded from the database, not the object source!
                    enrichBasicProperties((I)obj2, (I)obj1);
                } catch (EnricherException e) {
                    throw new IllegalStateException("Cannot merge "+obj1 + " with "+obj2, e);
                }
            }

            return obj2;
        }
    }

    public void enrich(I object) throws EnricherException {
        if (this.basicEnricher != null){
            this.basicEnricher.enrich(object);
        }
    }

    public void enrich(Collection<I> objects) throws EnricherException {
        if (this.basicEnricher != null){
            this.basicEnricher.enrich(objects);
        }
    }

    public void enrich(I objectToEnrich, I objectSource) throws EnricherException {
        if (intactClass.isAssignableFrom(objectToEnrich.getClass()) && intactClass.isAssignableFrom(objectSource.getClass())){
            merge((A) objectSource, (A) objectToEnrich);
        }
        else if (this.basicEnricher != null){
            enrichBasicProperties(objectToEnrich, objectSource);
        }
    }

    protected void enrichBasicProperties(I objectToEnrich) throws EnricherException{
        this.basicEnricher.enrich(objectToEnrich);
    }

    protected void enrichBasicProperties(I objectToEnrich, I objectSource) throws EnricherException{
         this.basicEnricher.enrich(objectToEnrich, objectSource);
    }

    protected abstract void mergeOtherProperties(A obj1, A obj2);

    protected abstract void mergeCreator(A obj1, A obj2);

    protected abstract void mergeCreatedDate(A obj1, A obj2);

    protected abstract void mergeUpdateDate(A obj1, A obj2);

    protected abstract void mergeUpdator(A obj1, A obj2);

    protected MIEnricher<I> getBasicEnricher() {
        return basicEnricher;
    }
}
