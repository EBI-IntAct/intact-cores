/*
 * Copyright 2001-2007 The European Bioinformatics Institute.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.intact.core.persister;

import uk.ac.ebi.intact.config.impl.SmallCvPrimer;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.visitor.BaseIntactVisitor;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class PersisterVisitor extends BaseIntactVisitor{

    private List<Institution> institutionsToPersist;
    private List<CvObject> cvObjectsToPersist;
    private LinkedList<AnnotatedObject> annotatedObjectsToPersist;

    public PersisterVisitor() {
        this.institutionsToPersist = new ArrayList<Institution>();
        this.cvObjectsToPersist = new ArrayList<CvObject>();
        this.annotatedObjectsToPersist = new LinkedList<AnnotatedObject>();
    }

    public void persistAll() {
        DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();
        SmallCvPrimer primer = new SmallCvPrimer(daoFactory);
        primer.createCVs();

        for (Institution institution : institutionsToPersist) {
            daoFactory.getEntityManager().persist(institution);
        }

        for (CvObject cv : cvObjectsToPersist) {
            daoFactory.getEntityManager().persist(cv);
        }

        daoFactory.getEntityManager().flush();

        for (AnnotatedObject ao : annotatedObjectsToPersist) {
            daoFactory.getEntityManager().persist(ao);
        }
    }

    @Override
    public void visitBioSource(BioSource bioSource) {
        if (!annotatedObjectsToPersist.contains(bioSource)) {
            annotatedObjectsToPersist.addFirst(bioSource);
        }
    }

    @Override
    public void visitComponent(Component component) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void visitCvObject(CvObject cvObject) {
        this.cvObjectsToPersist.add(cvObject);
    }

    @Override
    public void visitExperiment(Experiment experiment) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void visitFeature(Feature feature) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void visitInstitution(Institution institution) {
        this.institutionsToPersist.add(institution);
    }

    @Override
    public void visitInteraction(Interaction interaction) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void visitInteractor(Interactor interactor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void visitPublication(Publication publication) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void visitRange(Range range) {
        throw new UnsupportedOperationException();
    }
}