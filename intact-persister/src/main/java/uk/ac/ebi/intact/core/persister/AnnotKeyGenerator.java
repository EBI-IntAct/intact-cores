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

import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.util.CvObjectUtils;
import uk.ac.ebi.intact.model.util.CrcCalculator;
import uk.ac.ebi.intact.persistence.util.CgLibUtil;
import uk.ac.ebi.intact.util.DebugUtil;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class AnnotKeyGenerator {

    private AnnotKeyGenerator() {}

    public static String createKey(AnnotatedObject ao) {
        String key;

        final String normalizedClassName = normalizeClassName(ao.getClass());
        
        if (ao instanceof Component) {
            // So far, components have as shorltabel N/A => we need to create one that allows to discriminate them
            Component comp = (Component)ao;
            String label = comp.getInteraction().getShortLabel()+"_"+comp.getInteractor().getShortLabel();
            key = normalizedClassName +":"+label;
        } else if (ao instanceof BioSource) {
            key = normalizedClassName +":"+((BioSource)ao).getTaxId();
        } else if (ao instanceof CvObject) {
            CvObject cv = (CvObject)ao;
            CvObjectXref identity = CvObjectUtils.getPsiMiIdentityXref(cv);
            if (identity != null) {
                key = normalizedClassName +":"+identity.getPrimaryId();
            } else {
                key = normalizedClassName +":"+ao.getShortLabel();
            }
        } else if (ao instanceof Feature) {
            String label = ao.getShortLabel()+"_"+createKey(((Feature)ao).getComponent());
            key = normalizedClassName+":"+label;
        } else if (ao instanceof Interaction) {
            key = normalizedClassName +":"+new CrcCalculator().crc64((Interaction)ao);
        } else {
            key = normalizedClassName +":"+ao.getShortLabel();
        }

        return key;
    }

    protected static String normalizeClassName(Class clazz) {
        Class realClass = CgLibUtil.removeCglibEnhanced(clazz);
        return realClass.getSimpleName();
        //return clazz.getSimpleName();
    }
}