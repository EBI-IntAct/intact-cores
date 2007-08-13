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
package uk.ac.ebi.intact.core.persister.standard;

import uk.ac.ebi.intact.core.persister.PersisterException;
import uk.ac.ebi.intact.model.*;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class ExperimentPersister extends AbstractAnnotatedObjectPersister<Experiment> {

    private static ThreadLocal<ExperimentPersister> instance = new ThreadLocal<ExperimentPersister>() {
        @Override
        protected ExperimentPersister initialValue() {
            return new ExperimentPersister();
        }
    };

    public static ExperimentPersister getInstance() {
        return instance.get();
    }

    public ExperimentPersister() {
        super();
    }

    protected Experiment fetchFromDataSource(Experiment intactObject) {
        return getIntactContext().getDataContext().getDaoFactory()
                .getExperimentDao().getByShortLabel(intactObject.getShortLabel());
    }

    @Override
    protected void saveOrUpdateAttributes(Experiment intactObject) throws PersisterException {
        super.saveOrUpdateAttributes(intactObject);
        
        if (intactObject.getBioSource() != null) {
            BioSourcePersister.getInstance().saveOrUpdate(intactObject.getBioSource());
        }

        if (intactObject.getCvInteraction() != null) {
            CvObjectPersister.getInstance().saveOrUpdate(intactObject.getCvInteraction());
        } else {
            throw new NullPointerException("Experiment without CvInteraction: "+intactObject.getShortLabel());
        }

        if (intactObject.getCvIdentification() != null) {
            CvObjectPersister.getInstance().saveOrUpdate(intactObject.getCvIdentification());
        }

        if (intactObject.getPublication() != null) {
            PublicationPersister.getInstance().saveOrUpdate(intactObject.getPublication());
        }

        for (Interaction interaction : intactObject.getInteractions()) {
            InteractionPersister.getInstance().saveOrUpdate(interaction);
        }
    }

    @Override
    protected Experiment syncAttributes(Experiment intactObject) {
        if (intactObject.getBioSource() != null) {
            BioSource bioSource = BioSourcePersister.getInstance().syncIfTransient(intactObject.getBioSource());
            intactObject.setBioSource(bioSource);
        }

        intactObject.setCvInteraction((CvInteraction) CvObjectPersister.getInstance().syncIfTransient(intactObject.getCvInteraction()));

        if (intactObject.getCvIdentification() != null) {
            intactObject.setCvIdentification((CvIdentification) CvObjectPersister.getInstance().syncIfTransient(intactObject.getCvIdentification()));
        }

        if (intactObject.getPublication() != null) {
            intactObject.setPublication(PublicationPersister.getInstance().syncAttributes(intactObject.getPublication()));
        }

        return super.syncAttributes(intactObject);
    }
}