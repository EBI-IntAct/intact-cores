package uk.ac.ebi.intact.jami.synchronizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import psidev.psi.mi.jami.model.CvTerm;
import psidev.psi.mi.jami.model.Xref;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactXref;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.EntityManager;
import java.lang.reflect.InvocationTargetException;

/**
 * Finder/persister for xrefs
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>27/01/14</pre>
 */

public class IntactXrefSynchronizer implements IntactDbSynchronizer<Xref> {

    private IntactDbSynchronizer<CvTerm> dbSynchronizer;
    private IntactDbSynchronizer<CvTerm> qualifierSynchronizer;

    private EntityManager entityManager;
    private Class<? extends AbstractIntactXref> xrefClass;

    private static final Log log = LogFactory.getLog(IntactCvTermSynchronizer.class);

    public IntactXrefSynchronizer(EntityManager entityManager, Class<? extends AbstractIntactXref> xrefClass){
        if (entityManager == null){
            throw new IllegalArgumentException("Xref synchronizer needs a non null entityManager");
        }
        this.entityManager = entityManager;
        if (xrefClass == null){
            throw new IllegalArgumentException("Xref synchronizer needs a non null xref class");
        }
        this.xrefClass = xrefClass;
        this.dbSynchronizer = new IntactCvTermSynchronizer(entityManager, IntactUtils.DATABASE_OBJCLASS, null, null, this);
        this.qualifierSynchronizer = new IntactCvTermSynchronizer(entityManager, IntactUtils.QUALIFIER_OBJCLASS, null, null, this);
    }

    public IntactXrefSynchronizer(EntityManager entityManager, Class<? extends AbstractIntactXref> xrefClas,
                                  IntactDbSynchronizer<CvTerm> dbSynchronizer, IntactDbSynchronizer<CvTerm> qualifierSynchronizer){
        if (entityManager == null){
            throw new IllegalArgumentException("Xref synchronizer needs a non null entityManager");
        }
        this.entityManager = entityManager;
        if (xrefClas == null){
            throw new IllegalArgumentException("Xref synchronizer needs a non null xref class");
        }
        this.xrefClass = xrefClas;
        this.dbSynchronizer = dbSynchronizer != null ? dbSynchronizer : new IntactCvTermSynchronizer(entityManager, IntactUtils.DATABASE_OBJCLASS, null, null, this);
        this.qualifierSynchronizer = qualifierSynchronizer != null ? qualifierSynchronizer : new IntactCvTermSynchronizer(entityManager, IntactUtils.QUALIFIER_OBJCLASS, null, null, this);
    }

    public Xref find(Xref object) throws FinderException {
        return null;
    }

    public Xref persist(Xref object) throws FinderException, PersisterException, SynchronizerException {
        synchronizeProperties((AbstractIntactXref) object);
        this.entityManager.persist(object);
        return object;
    }

    public void synchronizeProperties(Xref object) throws FinderException, PersisterException, SynchronizerException {
        synchronizeProperties((AbstractIntactXref)object);
    }

    public Xref synchronize(Xref object, boolean persist, boolean merge) throws FinderException, PersisterException, SynchronizerException {
        if (!object.getClass().isAssignableFrom(this.xrefClass)){
            AbstractIntactXref newXref = null;
            try {
                newXref = this.xrefClass.getConstructor(CvTerm.class, String.class, String.class, CvTerm.class).newInstance(object.getDatabase(), object.getId(), object.getVersion(), object.getQualifier());
            } catch (InstantiationException e) {
                throw new SynchronizerException("Impossible to create a new instance of type "+this.xrefClass, e);
            } catch (IllegalAccessException e) {
                throw new SynchronizerException("Impossible to create a new instance of type "+this.xrefClass, e);
            } catch (InvocationTargetException e) {
                throw new SynchronizerException("Impossible to create a new instance of type "+this.xrefClass, e);
            } catch (NoSuchMethodException e) {
                throw new SynchronizerException("Impossible to create a new instance of type "+this.xrefClass, e);
            }

            // synchronize properties
            synchronizeProperties(newXref);
            if (persist){
                this.entityManager.persist(newXref);
            }
            return newXref;
        }
        else{
            AbstractIntactXref intactType = (AbstractIntactXref)object;
            // detached existing instance
            if (intactType.getAc() != null && !this.entityManager.contains(intactType)){
                // synchronize properties
                synchronizeProperties(intactType);
                // merge
                if (merge){
                    return this.entityManager.merge(intactType);
                }
                else{
                    return intactType;
                }
            }
            // retrieve and or persist transient instance
            else if (intactType.getAc() == null){
                // synchronize properties
                synchronizeProperties(intactType);
                // persist alias
                if (persist){
                    this.entityManager.persist(intactType);
                }
                return intactType;
            }
            else{
                // synchronize properties
                synchronizeProperties(intactType);
                return intactType;
            }
        }
    }

    public void clearCache() {
        this.dbSynchronizer.clearCache();
        this.qualifierSynchronizer.clearCache();
    }

    protected void synchronizeProperties(AbstractIntactXref object) throws PersisterException, SynchronizerException {
        // database first
        CvTerm db = object.getDatabase();
        try {
            object.setDatabase(dbSynchronizer.synchronize(db, true, true));
        } catch (FinderException e) {
            throw new IllegalStateException("Cannot persist the xref because could not synchronize its database.");
        }
        // check primaryId
        if (object.getId().length() > IntactUtils.MAX_ID_LEN){
            log.warn("Xref id too long: "+object.getId()+", will be truncated to "+ IntactUtils.MAX_ID_LEN+" characters.");
            object.setId(object.getId().substring(0, IntactUtils.MAX_ID_LEN));
        }
        // check secondaryId
        if (object.getSecondaryId() != null && object.getSecondaryId().length() > IntactUtils.MAX_SECONDARY_ID_LEN){
            log.warn("Xref secondaryId too long: "+object.getSecondaryId()+", will be truncated to "+ IntactUtils.MAX_SECONDARY_ID_LEN+" characters.");
            object.setSecondaryId(object.getSecondaryId().substring(0, IntactUtils.MAX_SECONDARY_ID_LEN));
        }
        // check version
        if (object.getVersion() != null && object.getVersion().length() > IntactUtils.MAX_DB_RELEASE_LEN){
            log.warn("Xref version too long: "+object.getVersion()+", will be truncated to "+ IntactUtils.MAX_DB_RELEASE_LEN+" characters.");
            object.setVersion(object.getVersion().substring(0, IntactUtils.MAX_DB_RELEASE_LEN));
        }
        // check qualifier
        if (object.getQualifier() != null){
            CvTerm qualifier = object.getQualifier();
            try {
                object.setQualifier(qualifierSynchronizer.synchronize(qualifier, true, true));
            } catch (FinderException e) {
                throw new IllegalStateException("Cannot persist the xref because could not synchronize its xref qualifier.");
            }
        }
    }
}
