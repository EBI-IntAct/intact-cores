package uk.ac.ebi.intact.jami.dao.impl;

import psidev.psi.mi.jami.model.*;
import uk.ac.ebi.intact.jami.context.SynchronizerContext;
import uk.ac.ebi.intact.jami.dao.ComplexDao;
import uk.ac.ebi.intact.jami.model.extension.IntactComplex;
import uk.ac.ebi.intact.jami.model.extension.IntactCooperativityEvidence;
import uk.ac.ebi.intact.jami.model.extension.IntactInteractionEvidence;
import uk.ac.ebi.intact.jami.model.lifecycle.LifeCycleEvent;
import uk.ac.ebi.intact.jami.synchronizer.IntactDbSynchronizer;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.Collection;

/**
 * Implementation of complexDao
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>31/01/14</pre>
 */
public class ComplexDaoImpl extends InteractorDaoImpl<Complex,IntactComplex> implements ComplexDao{

    public ComplexDaoImpl(EntityManager entityManager, SynchronizerContext context) {
        super(IntactComplex.class, entityManager, context);
    }

    @Override
    public IntactDbSynchronizer<Complex, IntactComplex> getDbSynchronizer() {
        return getSynchronizerContext().getComplexSynchronizer();
    }

    public Collection<IntactComplex> getByInteractionType(String typeName, String typeMI, int first, int max) {
        Query query;
        if (typeName == null && typeMI == null){
            query = getEntityManager().createQuery("select f from IntactComplex f "  +
                    "where f.interactionType is null order by f.ac");
        }
        else if (typeMI != null){
            query = getEntityManager().createQuery("select distinct f from IntactComplex f "  +
                    "join f.interactionType as t " +
                    "join t.dbXrefs as xref " +
                    "join xref.database as d " +
                    "join xref.qualifier as q " +
                    "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                    "and d.shortName = :psimi " +
                    "and xref.id = :mi order by f.ac");
            query.setParameter("identity", Xref.IDENTITY);
            query.setParameter("secondaryAc", Xref.SECONDARY);
            query.setParameter("psimi", CvTerm.PSI_MI);
            query.setParameter("mi", typeMI);
        }
        else{
            query = getEntityManager().createQuery("select f from IntactComplex f "  +
                    "join f.interactionType as t " +
                    "where t.shortName = :typeName order by f.ac");
            query.setParameter("typeName", typeName);
        }
        return query.getResultList();
    }

    public Collection<IntactComplex> getByLifecycleEvent(String evtName, int first, int max) {
        Query query;
        if (evtName == null){
            query = getEntityManager().createQuery("select p from IntactComplex p "  +
                    "where p.lifecycleEvents is empty order by p.ac");
        }
        else{
            query = getEntityManager().createQuery("select distinct p from IntactComplex p "  +
                    "join p.lifecycleEvents as l "  +
                    "join l.cvEvent as e "  +
                    "where e.shortName = :name order by p.ac");
            query.setParameter("name", evtName);
        }
        query.setFirstResult(first);
        query.setMaxResults(max);
        return query.getResultList();
    }

    public Collection<IntactComplex> getByStatus(String statusName, int first, int max) {
        Query query;
        if (statusName == null){
            query = getEntityManager().createQuery("select p from IntactComplex p "  +
                    "where p.cvStatus is null order by p.ac");
        }
        else{
            query = getEntityManager().createQuery("select p from IntactComplex p "  +
                    "join p.cvStatus as s "  +
                    "where s.shortName = :name order by p.ac");
            query.setParameter("name", statusName);
        }
        query.setFirstResult(first);
        query.setMaxResults(max);
        return query.getResultList();
    }

    public Collection<IntactComplex> getByConfidence(String typeName, String typeMI, String value) {
        Query query;
        if (typeName == null && typeMI == null){
            query = getEntityManager().createQuery("select distinct f from IntactComplex f "  +
                    "where f.modelledConfidences is empty order by f.ac");
        }
        else if (typeMI != null){
            query = getEntityManager().createQuery("select distinct f from IntactComplex f "  +
                    "join f.modelledConfidences as c " +
                    "join c.type as t " +
                    "join t.dbXrefs as xref " +
                    "join xref.database as d " +
                    "join xref.qualifier as q " +
                    "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                    "and d.shortName = :psimi " +
                    "and xref.id = :mi " + (value != null ? " and c.value = :confValue ":" ")+
                    "order by f.ac");
            query.setParameter("identity", Xref.IDENTITY);
            query.setParameter("secondaryAc", Xref.SECONDARY);
            query.setParameter("psimi", CvTerm.PSI_MI);
            query.setParameter("mi", typeMI);
            if (value != null){
                query.setParameter("confValue", value);
            }
        }
        else{
            query = getEntityManager().createQuery("select distinct f from IntactComplex f "  +
                    "join f.modelledConfidences as c " +
                    "join c.type as t " +
                    "where t.shortName = :name "  + (value != null ? " and c.value = :confValue ":" ")+
                    "order by f.ac");
            query.setParameter("name", typeName);
            if (value != null){
                query.setParameter("confValue", value);
            }
        }
        return query.getResultList();
    }

    /*public Collection<IntactComplex> getByCooperativeEffectAnnotationTopic(String topicName, String topicMI) {
        Query query;
        if (topicMI != null){
            query = getEntityManager().createQuery("select distinct f from IntactComplex f "  +
                    "join f.cooperativeEffects as c " +
                    "join c.annotations as a " +
                    "join a.topic as t " +
                    "join t.dbXrefs as xref " +
                    "join xref.database as d " +
                    "join xref.qualifier as q " +
                    "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                    "and d.shortName = :psimi " +
                    "and xref.id = :mi");
            query.setParameter("identity", Xref.IDENTITY);
            query.setParameter("secondaryAc", Xref.SECONDARY);
            query.setParameter("psimi", CvTerm.PSI_MI);
            query.setParameter("mi", topicMI);
        }
        else{
            query = getEntityManager().createQuery("select distinct f from IntactComplex f "  +
                    "join f.cooperativeEffects as c " +
                    "join c.annotations as a " +
                    "join a.topic as t " +
                    "where t.shortName = :topicName");
            query.setParameter("topicName", topicName);
        }
        return query.getResultList();
    }

    public Collection<IntactComplex> getByCooperativeEffectAnnotationTopicAndValue(String topicName, String topicMI, String value) {
        Query query;
        if (topicMI != null){
            query = getEntityManager().createQuery("select distinct f from IntactComplex f "  +
                    "join f.cooperativeEffects as c " +
                    "join c.annotations as a " +
                    "join a.topic as t " +
                    "join t.dbXrefs as xref " +
                    "join xref.database as d " +
                    "join xref.qualifier as q " +
                    "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                    "and d.shortName = :psimi " +
                    "and xref.id = :mi"+(value != null ? " and a.value = :annotValue" : ""));
            query.setParameter("identity", Xref.IDENTITY);
            query.setParameter("secondaryAc", Xref.SECONDARY);
            query.setParameter("psimi", CvTerm.PSI_MI);
            query.setParameter("mi", topicMI);
            if (value != null){
                query.setParameter("annotValue", value);
            }
        }
        else{
            query = getEntityManager().createQuery("select distinct f from IntactComplex f "  +
                    "join f.cooperativeEffects as c " +
                    "join c.annotations as a " +
                    "join a.topic as t " +
                    "where t.shortName = :topicName"+(value != null ? " and a.value = :annotValue" : ""));
            query.setParameter("topicName", topicName);
            if (value != null){
                query.setParameter("annotValue", value);
            }
        }
        return query.getResultList();
    }

    public Collection<IntactComplex> getByCooperativeEffectAffectedInteractionAc(String ac) {
        Query query = getEntityManager().createQuery("select distinct f from IntactComplex f "  +
                "join f.cooperativeEffects as c " +
                "join c.affectedInteractions as i " +
                "where i.ac = :interactionAc");
        query.setParameter("interactionAc", ac);
        return query.getResultList();
    }

    public Collection<IntactComplex> getByCooperativeEffectOutcome(String name, String mi) {
        Query query;
        if (mi != null){
            query = getEntityManager().createQuery("select distinct f from IntactComplex f "  +
                    "join f.cooperativeEffects as coop " +
                    "join coop.outcome as c " +
                    "join c.dbXrefs as xref " +
                    "join xref.database as d " +
                    "join xref.qualifier as q " +
                    "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                    "and d.shortName = :psimi " +
                    "and xref.id = :mi");
            query.setParameter("identity", Xref.IDENTITY);
            query.setParameter("secondaryAc", Xref.SECONDARY);
            query.setParameter("psimi", CvTerm.PSI_MI);
            query.setParameter("mi", mi);
        }
        else{
            query = getEntityManager().createQuery("select f from IntactComplex f "  +
                    "join f.cooperativeEffects as coop " +
                    "join coop.outcome as c " +
                    "where c.shortName = :name");
            query.setParameter("name", name);
        }
        return query.getResultList();
    }

    public Collection<IntactComplex> getByCooperativeEffectResponse(String name, String mi) {
        Query query;
        if (mi != null){
            query = getEntityManager().createQuery("select distinct f from IntactComplex f "  +
                    "join f.cooperativeEffects as coop " +
                    "join coop.response as r " +
                    "join r.dbXrefs as xref " +
                    "join xref.database as d " +
                    "join xref.qualifier as q " +
                    "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                    "and d.shortName = :psimi " +
                    "and xref.id = :mi");
            query.setParameter("identity", Xref.IDENTITY);
            query.setParameter("secondaryAc", Xref.SECONDARY);
            query.setParameter("psimi", CvTerm.PSI_MI);
            query.setParameter("mi", mi);
        }
        else{
            query = getEntityManager().createQuery("select f from IntactComplex f "  +
                    "join f.cooperativeEffects as coop " +
                    "join coop.response as r " +
                    "where r.shortName = :name");
            query.setParameter("name", name);
        }
        return query.getResultList();
    }

    public Collection<IntactComplex> getByAllostericMoleculeAc(String ac) {
        Query query = getEntityManager().createQuery("select f from IntactComplex f "  +
                "join f.cooperativeEffects as coop " +
                "join coop.allostericMolecule as c " +
                "where c.ac = :moleculeAc");
        query.setParameter("moleculeAc", ac);
        return query.getResultList();
    }

    public Collection<IntactComplex> getByAllosteryMechanism(String name, String mi) {
        Query query;
        if (mi != null){
            query = getEntityManager().createQuery("select distinct f from IntactComplex f "  +
                    "join f.cooperativeEffects as coop " +
                    "join coop.mechanism as c " +
                    "join c.dbXrefs as xref " +
                    "join xref.database as d " +
                    "join xref.qualifier as q " +
                    "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                    "and d.shortName = :psimi " +
                    "and xref.id = :mi");
            query.setParameter("identity", Xref.IDENTITY);
            query.setParameter("secondaryAc", Xref.SECONDARY);
            query.setParameter("psimi", CvTerm.PSI_MI);
            query.setParameter("mi", mi);
        }
        else{
            query = getEntityManager().createQuery("select f from IntactComplex f "  +
                    "join f.cooperativeEffects as coop " +
                    "join coop.mechanism as c " +
                    "where c.shortName = :name");
            query.setParameter("name", name);
        }
        return query.getResultList();
    }

    public Collection<IntactComplex> getByAllosteryType(String name, String mi) {
        Query query;
        if (mi != null){
            query = getEntityManager().createQuery("select distinct f from IntactComplex f "  +
                    "join f.cooperativeEffects as coop " +
                    "join coop.allosteryType as r " +
                    "join r.dbXrefs as xref " +
                    "join xref.database as d " +
                    "join xref.qualifier as q " +
                    "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                    "and d.shortName = :psimi " +
                    "and xref.id = :mi");
            query.setParameter("identity", Xref.IDENTITY);
            query.setParameter("secondaryAc", Xref.SECONDARY);
            query.setParameter("psimi", CvTerm.PSI_MI);
            query.setParameter("mi", mi);
        }
        else{
            query = getEntityManager().createQuery("select f from IntactComplex f "  +
                    "join f.cooperativeEffects as coop " +
                    "join coop.allosteryType as r " +
                    "where r.shortName = :name");
            query.setParameter("name", name);
        }
        return query.getResultList();
    }

    public Collection<IntactComplex> getByAllostericMoleculeEffectorAc(String ac) {
        Query query = getEntityManager().createQuery("select f from IntactComplex f "  +
                "join f.cooperativeEffects as coop " +
                "join coop.allostericEffector as c " +
                "where c.ac = :moleculeAc");
        query.setParameter("moleculeAc", ac);
        return query.getResultList();
    }

    public Collection<IntactComplex> getByAllostericFeatureModificationEffectorAc(String ac) {
        Query query = getEntityManager().createQuery("select f from IntactComplex f "  +
                "join f.cooperativeEffects as coop " +
                "join coop.allostericEffector as c " +
                "where c.ac = :featureAc");
        query.setParameter("featureAc", ac);
        return query.getResultList();
    } */

    public Collection<IntactComplex> getByParameterType(String typeName, String typeMI) {
        Query query;
        if (typeMI != null){
            query = getEntityManager().createQuery("select distinct i from IntactComplex i " +
                    "join i.modelledParameters as p " +
                    "join p.type as t " +
                    "join t.dbXrefs as x " +
                    "join x.database as d " +
                    "join x.qualifier as q " +
                    "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                    "and d.shortName = :psimi " +
                    "and x.id = :mi");
            query.setParameter("identity", Xref.IDENTITY);
            query.setParameter("secondaryAc", Xref.SECONDARY);
            query.setParameter("psimi", CvTerm.PSI_MI);
            query.setParameter("mi", typeMI);
        }
        else{
            query = getEntityManager().createQuery("select distinct i from IntactComplex i " +
                    "join i.modelledParameters as p " +
                    "join p.type as t " +
                    "where t.shortName = :paramName");
            query.setParameter("paramName", typeName);
        }
        return query.getResultList();
    }

    public Collection<IntactComplex> getByParameterUnit(String unitName, String unitMI) {
        Query query;
        if (unitMI == null && unitName == null){
            query = getEntityManager().createQuery("select distinct i from IntactComplex i " +
                    "join i.modelledParameters as p " +
                    "where p.unit is null");
        }
        else if (unitMI != null){
            query = getEntityManager().createQuery("select distinct i from IntactComplex i " +
                    "join i.modelledParameters as p " +
                    "join p.unit as u " +
                    "join u.dbXrefs as x " +
                    "join x.database as d " +
                    "join x.qualifier as q " +
                    "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                    "and d.shortName = :psimi " +
                    "and x.id = :mi");
            query.setParameter("identity", Xref.IDENTITY);
            query.setParameter("secondaryAc", Xref.SECONDARY);
            query.setParameter("psimi", CvTerm.PSI_MI);
            query.setParameter("mi", unitMI);
        }
        else{
            query = getEntityManager().createQuery("select distinct i from IntactComplex i " +
                    "join i.modelledParameters as p " +
                    "join p.unit as u " +
                    "where u.shortName = :unitName");
            query.setParameter("unitName", unitName);
        }
        return query.getResultList();
    }

    public Collection<IntactComplex> getByParameterTypeAndUnit(String typeName, String typeMI, String unitName, String unitMI) {
        Query query;
        if (typeMI != null){
            if (unitMI == null && unitName == null){
                query = getEntityManager().createQuery("select distinct i from IntactComplex i " +
                        "join i.modelledParameters as p " +
                        "join p.type as t " +
                        "join t.dbXrefs as x " +
                        "join x.database as d " +
                        "join x.qualifier as q " +
                        "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                        "and d.shortName = :psimi " +
                        "and x.id = :mi " +
                        "and p.unit is null");
                query.setParameter("identity", Xref.IDENTITY);
                query.setParameter("secondaryAc", Xref.SECONDARY);
                query.setParameter("psimi", CvTerm.PSI_MI);
                query.setParameter("mi", typeMI);
            }
            else if (unitMI != null){
                query = getEntityManager().createQuery("select distinct i from IntactComplex i " +
                        "join i.modelledParameters as p " +
                        "join p.type as t " +
                        "join t.dbXrefs as x " +
                        "join x.database as d " +
                        "join x.qualifier as q " +
                        "join p.unit as u " +
                        "join u.dbXrefs as x2 " +
                        "join x2.database as d2 " +
                        "join x2.qualifier as q2 " +
                        "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                        "and d.shortName = :psimi " +
                        "and x.id = :mi " +
                        "and (q2.shortName = :identity or q2.shortName = :secondaryAc) "+
                        "and d2.shortName = :psimi "+
                        "and x2.id = :mi2 ");
                query.setParameter("identity", Xref.IDENTITY);
                query.setParameter("secondaryAc", Xref.SECONDARY);
                query.setParameter("psimi", CvTerm.PSI_MI);
                query.setParameter("mi", typeMI);
                query.setParameter("mi2", unitMI);
            }
            else{
                query = getEntityManager().createQuery("select distinct i from IntactComplex i " +
                        "join i.modelledParameters as p " +
                        "join p.type as t " +
                        "join t.dbXrefs as x " +
                        "join x.database as d " +
                        "join x.qualifier as q " +
                        "join p.unit as u " +
                        "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                        "and d.shortName = :psimi " +
                        "and x.id = :mi " +
                        "and u.shortName = :unitName");
                query.setParameter("identity", Xref.IDENTITY);
                query.setParameter("secondaryAc", Xref.SECONDARY);
                query.setParameter("psimi", CvTerm.PSI_MI);
                query.setParameter("mi", typeMI);
                query.setParameter("unitName", unitName);
            }
        }
        else{
            if (unitMI == null && unitName == null){
                query = getEntityManager().createQuery("select distinct i from IntactComplex i " +
                        "join i.modelledParameters as p " +
                        "join p.type as t " +
                        "where t.shortName = :confName " +
                        "and p.unit is null");
                query.setParameter("typeName", typeName);
            }
            else if (unitMI != null){
                query = getEntityManager().createQuery("select distinct i from IntactComplex i " +
                        "join i.modelledParameters as p " +
                        "join p.type as t " +
                        "join p.unit as u " +
                        "join u.dbXrefs as x " +
                        "join x.database as d " +
                        "join x.qualifier as q " +
                        "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                        "and d.shortName = :psimi " +
                        "and x.id = :mi " +
                        "and t.shortName = :typeName");
                query.setParameter("identity", Xref.IDENTITY);
                query.setParameter("secondaryAc", Xref.SECONDARY);
                query.setParameter("psimi", CvTerm.PSI_MI);
                query.setParameter("mi", unitMI);
                query.setParameter("typeName", typeName);
            }
            else{
                query = getEntityManager().createQuery("select distinct i from IntactComplex i " +
                        "join i.modelledParameters as p " +
                        "join p.type as t " +
                        "join p.unit as u " +
                        "where u.shortName = :unitName " +
                        "and t.shortName = :typeName");
                query.setParameter("unitName", unitName);
                query.setParameter("typeName", typeName);
            }
        }
        return query.getResultList();
    }

    /*public Collection<IntactComplex> getByCooperativityEvidenceMethod(String methodName, String methodMI) {
        Query query;
        if (methodMI != null){
            query = getEntityManager().createQuery("select distinct comp from IntactComplex comp " +
                    "join comp.cooperativeEffects as c " +
                    "join c.cooperativityEvidences as e " +
                    "join e.evidenceMethods as t " +
                    "join t.dbXrefs as x " +
                    "join x.database as d " +
                    "join x.qualifier as q " +
                    "where (q.shortName = :identity or q.shortName = :secondaryAc) " +
                    "and d.shortName = :psimi " +
                    "and x.id = :mi");
            query.setParameter("identity", Xref.IDENTITY);
            query.setParameter("secondaryAc", Xref.SECONDARY);
            query.setParameter("psimi", CvTerm.PSI_MI);
            query.setParameter("mi", methodMI);
        }
        else{
            query = getEntityManager().createQuery("select distinct comp from IntactComplex comp " +
                    "join comp.cooperativeEffects as c " +
                    "join c.cooperativityEvidences as e " +
                    "join e.evidenceMethods as t " +
                    "where t.shortName = :methodName");
            query.setParameter("methodName", methodName);
        }
        return query.getResultList();
    }

    public Collection<IntactComplex> getByCooperativityEvidencePublicationPubmed(String pubmed) {
        Query query = getEntityManager().createQuery("select distinct comp from IntactComplex comp " +
                "join comp.cooperativeEffects as c " +
                "join c.cooperativityEvidences as e " +
                "join e.publication as p " +
                "join p.dbXrefs as x " +
                "join x.database as d " +
                "join x.qualifier as q " +
                "where (q.shortName = :identity or q.shortName = :secondaryAc or q.shortName = :primary) " +
                "and d.shortName = :pubmed " +
                "and x.id = :identifier");
        query.setParameter("identity", Xref.IDENTITY);
        query.setParameter("secondaryAc", Xref.SECONDARY);
        query.setParameter("primary", Xref.PRIMARY);
        query.setParameter("pubmed", Xref.PUBMED);
        query.setParameter("identifier", pubmed);
        return query.getResultList();
    }

    public Collection<IntactComplex> getByCooperativityEvidencePublicationDoi(String doi) {
        Query query = getEntityManager().createQuery("select distinct comp from IntactComplex comp " +
                "join comp.cooperativeEffects as c " +
                "join c.cooperativityEvidences as e " +
                "join e.publication as p " +
                "join p.dbXrefs as x " +
                "join x.database as d " +
                "join x.qualifier as q " +
                "where (q.shortName = :identity or q.shortName = :secondaryAc or q.shortName = :primary) " +
                "and d.shortName = :doi " +
                "and x.id = :identifier");
        query.setParameter("identity", Xref.IDENTITY);
        query.setParameter("secondaryAc", Xref.SECONDARY);
        query.setParameter("primary", Xref.PRIMARY);
        query.setParameter("doi", Xref.DOI);
        query.setParameter("identifier", doi);
        return query.getResultList();
    }

    public Collection<IntactComplex> getByCooperativityEvidencePublicationAc(String ac) {
        Query query = getEntityManager().createQuery("select distinct comp from IntactComplex comp " +
                "join comp.cooperativeEffects as c " +
                "join c.cooperativityEvidences as e " +
                "join e.publication as p " +
                "where p.ac = :pubAc");
        query.setParameter("pubAc", ac);
        return query.getResultList();
    } */

    @Override
    public Collection<LifeCycleEvent> getLifeCycleEventsForComplex(String ac) {
        Query query = getEntityManager().createQuery("select distinct l from IntactComplex p " +
                "join p.lifecycleEvents as l " +
                "where p.ac = :ac");
        query.setParameter("ac", ac);
        return query.getResultList();
    }

    @Override
    public int countParticipantsForComplex(String ac) {
        Query query = getEntityManager().createQuery("select size(i.participants) from IntactComplex i " +
                "where i.ac = :ac");
        query.setParameter("ac", ac);
        return (Integer)query.getSingleResult();
    }

    @Override
    public Collection<ModelledConfidence> getConfidencesForComplex(String ac) {
        Query query = getEntityManager().createQuery("select distinct x from IntactComplex i " +
                "join i.modelledConfidences as x " +
                "where i.ac = :ac");
        query.setParameter("ac", ac);
        return query.getResultList();
    }

    @Override
    public Collection<ModelledParameter> getParametersForComplex(String ac) {
        Query query = getEntityManager().createQuery("select distinct x from IntactComplex i " +
                "join i.modelledParameters as x " +
                "where i.ac = :ac");
        query.setParameter("ac", ac);
        return query.getResultList();
    }

    @Override
    public int countConfidencesForComplex(String ac) {
        Query query = getEntityManager().createQuery("select size(i.modelledConfidences) from IntactComplex i " +
                "where i.ac = :ac");
        query.setParameter("ac", ac);
        return (Integer)query.getSingleResult();
    }

    @Override
    public int countParametersForComplex(String ac) {
        Query query = getEntityManager().createQuery("select size(i.modelledParameters) from IntactComplex i " +
                "where i.ac = :ac");
        query.setParameter("ac", ac);
        return (Integer)query.getSingleResult();
    }

    @Override
    public int countComplexesInvolvingInteractor(String ac) {
        Query query = getEntityManager().createQuery("select count(distinct i.ac) from IntactComplex i " +
                "join i.participants as p join p.interactor as inter " +
                "where inter.ac = :ac");
        query.setParameter("ac", ac);
        Long results = (Long)query.getSingleResult();
        return results != null ? results.intValue() : 0;
    }

    @Override
    public int countComplexesByOrganism(String organismAc) {
        Query query = getEntityManager().createQuery("select count (distinct i.ac) from IntactComplex i " +
                "join i.organism o " +
                "where o.ac = :ac");
        query.setParameter("ac", organismAc);
        Long results = (Long)query.getSingleResult();
        return results != null ? results.intValue() : 0;
    }
}
