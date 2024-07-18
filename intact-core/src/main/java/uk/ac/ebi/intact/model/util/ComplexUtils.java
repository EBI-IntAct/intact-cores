package uk.ac.ebi.intact.model.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.model.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utility methods for Complexes
 *
 * Created by maitesin on 18/03/2014.
 */
public final class ComplexUtils {
    /**
     * Sets up a logger for that class.
     */
    private static final Log log = LogFactory.getLog(ProteinUtils.class);

    private ComplexUtils() {
    }

    //
    // ALIASES
    //
    //Generic method to get information stored in the aliases
    private static String getAlias(InteractionImpl complex, String id) {
        for (Alias alias : complex.getAliases()) {
            if (alias.getName() != null && alias.getCvAliasType() != null && alias.getCvAliasType().getIdentifier().equals(id)) {
                return alias.getName();
            }
        }
        return null;
    }

    public static String getSystematicName(InteractionImpl complex) {
        return getAlias(complex, CvAliasType.COMPLEX_SYSTEMATIC_NAME_MI_REF);
    }

    public static String getRecommendedName(InteractionImpl complex) {
        return getAlias(complex, CvAliasType.COMPLEX_RECOMMENDED_NAME_MI_REF);
    }

    //Retrieve all the synosyms of the complex
    public static List<String> getSynonyms(InteractionImpl complex) {
        List<String> synosyms = new ArrayList<String>();
        for (Alias alias : complex.getAliases()) {
            if (alias.getName() != null && alias.getCvAliasType() != null && alias.getCvAliasType().getIdentifier().equals(CvAliasType.COMPLEX_SYNONYM_NAME_MI_REF)) {
                synosyms.add(alias.getName());
            }
        }
        return synosyms;
    }

    //Retrieve the first alias found
    public static String getFirstAlias(InteractionImpl complex) {
        for (Alias alias : complex.getAliases()) {
            if (alias.getName() != null) {
                return alias.getName();
            }
        }
        return null;
    }

    public static String getName(InteractionImpl complex) {
        String name = getRecommendedName(complex);
        if (name != null) return name;
        name = getSystematicName(complex);
        if (name != null) return name;
        List<String> synonyms = getSynonyms(complex);
        if (! synonyms.isEmpty()) return synonyms.get(0);
        name = getFirstAlias(complex);
        if (name != null) return name;
        return complex.getShortLabel();
    }

    //
    // SPECIES
    //
    //
    public static String getSpeciesName(InteractionImpl complex) {
        if (!complex.getExperiments().isEmpty()) {
            Experiment exp = complex.getExperiments().iterator().next();
            BioSource bioSource = exp.getBioSource();
            if (bioSource != null) {
                return bioSource.getFullName() != null ? bioSource.getFullName() : bioSource.getShortLabel();
            }
        }
        return null;
    }

    public static String getSpeciesTaxId(InteractionImpl complex) {
        if (!complex.getExperiments().isEmpty()) {
            Experiment exp = complex.getExperiments().iterator().next();
            BioSource bioSource = exp.getBioSource();
            if (bioSource != null) {
                return bioSource.getTaxId();
            }
        }
        return null;
    }

    //
    // ANNOTATIONS
    //
    //Generic method to retrieve information stored in the annotations
    private static String getAnnotationById(InteractionImpl complex, String id) {
        for (Annotation annotation : complex.getAnnotations()) {
            if (annotation.getCvTopic() != null && annotation.getCvTopic().getIdentifier() != null && annotation.getCvTopic().getIdentifier().equalsIgnoreCase(id)) {
                return annotation.getAnnotationText();
            }
        }
        return null;
    }

    public static String getProperties(InteractionImpl complex) {
        return getAnnotationById(complex, CvTopic.COMPLEX_PROPERTIES_MI_REF);
    }

    public static String getDisease(InteractionImpl complex) {
        return getAnnotationById(complex, CvTopic.COMPLEX_DISEASE_MI_REF);
    }

    private static String getAnnotationByShortLabel(InteractionImpl complex, String shortLabel) {
        for (Annotation annotation : complex.getAnnotations()) {
            if (annotation.getCvTopic() != null && annotation.getCvTopic().getShortLabel() != null && annotation.getCvTopic().getShortLabel().equalsIgnoreCase(shortLabel)) {
                return annotation.getAnnotationText();
            }
        }
        return null;
    }

    public static String getLigand(InteractionImpl complex) {
        return getAnnotationByShortLabel(complex, CvTopic.COMPLEX_LIGAND);
    }

    public static String getComplexAssembly(InteractionImpl complex) {
        return getAnnotationByShortLabel(complex, CvTopic.COMPLEX_ASSEMBLY);
    }

    public static String getFunction(InteractionImpl complex) {
        return getAnnotationByShortLabel(complex, CvTopic.CURATED_COMPLEX);
    }

    public static boolean isComplex(Interaction interaction) {
        for (Annotation annot : interaction.getAnnotations()) {
            if (annot.getCvTopic() != null && annot.getCvTopic().getShortLabel().equalsIgnoreCase(CvTopic.CURATED_COMPLEX)) {
                return true;
            }
        }
        return interaction.isPredictedComplex();
    }
}