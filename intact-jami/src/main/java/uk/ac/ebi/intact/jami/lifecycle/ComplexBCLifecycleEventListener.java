package uk.ac.ebi.intact.jami.lifecycle;


import psidev.psi.mi.jami.model.Experiment;
import uk.ac.ebi.intact.jami.model.extension.IntactComplex;
import uk.ac.ebi.intact.jami.model.extension.IntactPublication;
import uk.ac.ebi.intact.jami.model.lifecycle.Releasable;
import uk.ac.ebi.intact.jami.model.user.User;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

/**
 * LifecycleEventListener for complexes with empty implementation of all methods that allows backward compatibility
 * with IntAct core.
 *
 * It will create and/or remove experiments depending on the lifecycle state
 *
 * - unassigned638 is publication for complexes not released
 * - 14681455 is publication for complexes released
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since 2.5
 */
public class ComplexBCLifecycleEventListener implements LifecycleEventListener {

    @Override
    public void fireOwnerChanged( Releasable releaseable, User previousOwner, User newOwner ) {
    }

    @Override
    public void fireReviewerChanged( Releasable releaseable, User previousReviewer, User newReviewer ) {
    }

    @Override
    public void fireCreated( Releasable releaseable ) {
        if (releaseable instanceof IntactComplex){
            IntactComplex intactComplex = (IntactComplex)releaseable;
            if (intactComplex.getExperiments().isEmpty()){
                IntactUtils.createAndAddDefaultExperimentForComplexes(intactComplex, "unassigned638");
            }
        }
    }

    @Override
    public void fireReserved( Releasable releaseable ) {
    }

    @Override
    public void fireAssignentDeclined( Releasable releaseable ) {
    }

    @Override
    public void fireAssigned( Releasable releaseable, User byUser, User toUser ) {
    }

    @Override
    public void fireCurationInProgress( Releasable releaseable ) {
    }

    @Override
    public void fireReadyForChecking( Releasable releaseable ) {
    }

    @Override
    public void fireAccepted( Releasable releaseable ) {
    }

    @Override
    public void fireAcceptedOnHold( Releasable releaseable ) {
    }

    @Override
    public void fireRejected(Releasable releaseable ) {
    }

    @Override
    public void fireReadyForRelease( Releasable releaseable ) {
        if (releaseable instanceof IntactComplex){
            IntactComplex complex = (IntactComplex)releaseable;

            Experiment exp = complex.getExperiments().isEmpty() ? complex.getExperiments().iterator().next() : null;
            if (exp == null){
                IntactUtils.createAndAddDefaultExperimentForComplexes(complex, "14681455");
                exp = complex.getExperiments().iterator().next();
                ((IntactPublication)exp.getPublication()).setStatus(complex.getStatus());
            }
            else if (exp.getPublication() == null){
                exp.setPublication(new IntactPublication("14681455"));
                ((IntactPublication)exp.getPublication()).setStatus(complex.getStatus());
            }
            else if (exp.getPublication().getPubmedId() == null || !exp.getPublication().getPubmedId().equals("14681455")){
                exp.setPublication(new IntactPublication("14681455"));
                ((IntactPublication)exp.getPublication()).setStatus(complex.getStatus());
            }
        }
    }

    @Override
    public void fireReleased( Releasable releaseable ) {
        if (releaseable instanceof IntactComplex){
            IntactComplex complex = (IntactComplex)releaseable;

            Experiment exp = complex.getExperiments().isEmpty() ? complex.getExperiments().iterator().next() : null;
            if (exp == null){
                IntactUtils.createAndAddDefaultExperimentForComplexes(complex, "14681455");
                exp = complex.getExperiments().iterator().next();
                ((IntactPublication)exp.getPublication()).setStatus(complex.getStatus());
            }
            else if (exp.getPublication() == null){
                exp.setPublication(new IntactPublication("14681455"));
                ((IntactPublication)exp.getPublication()).setStatus(complex.getStatus());
            }
            else if (exp.getPublication().getPubmedId() == null || !exp.getPublication().getPubmedId().equals("14681455")){
                exp.setPublication(new IntactPublication("14681455"));
                ((IntactPublication)exp.getPublication()).setStatus(complex.getStatus());
            }
        }
    }

    @Override
    public void fireDiscarded(Releasable releaseable) {
        if (releaseable instanceof IntactComplex){
            IntactComplex complex = (IntactComplex)releaseable;

            Experiment exp = complex.getExperiments().isEmpty() ? complex.getExperiments().iterator().next() : null;
            if (exp == null){
                IntactUtils.createAndAddDefaultExperimentForComplexes(complex, "unassigned638");
            }
            else if (exp.getPublication() == null){
                exp.setPublication(new IntactPublication("unassigned638"));
            }
            else if (exp.getPublication().getPubmedId() == null || !exp.getPublication().getPubmedId().equals("unassigned638")){
                exp.setPublication(new IntactPublication("unassigned638"));
            }
        }
    }

    @Override
    public void firePutOnHold(Releasable releaseable) {
        if (releaseable instanceof IntactComplex){
            IntactComplex complex = (IntactComplex)releaseable;

            Experiment exp = complex.getExperiments().isEmpty() ? complex.getExperiments().iterator().next() : null;
            if (exp == null){
                IntactUtils.createAndAddDefaultExperimentForComplexes(complex, "unassigned638");
            }
            else if (exp.getPublication() == null){
                exp.setPublication(new IntactPublication("unassigned638"));
            }
            else if (exp.getPublication().getPubmedId() == null || !exp.getPublication().getPubmedId().equals("unassigned638")){
                exp.setPublication(new IntactPublication("unassigned638"));
            }
        }
    }
}
