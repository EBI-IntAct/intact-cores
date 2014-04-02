package uk.ac.ebi.intact.jami;

import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.model.impl.DefaultAlias;
import psidev.psi.mi.jami.utils.AliasUtils;
import psidev.psi.mi.jami.utils.AnnotationUtils;
import psidev.psi.mi.jami.utils.ConfidenceUtils;
import psidev.psi.mi.jami.utils.XrefUtils;
import uk.ac.ebi.intact.jami.model.extension.*;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import java.lang.reflect.InvocationTargetException;

/**
 * Utility class for testing
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>02/04/14</pre>
 */

public class IntactTestUtils {

    public static <T extends AbstractIntactAlias> T createIntactAlias(Class<T> aliasClass, String typeName, String typeMI, String name) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {

        T alias = aliasClass.getConstructor(String.class).newInstance(name);
        if (typeName != null){
            alias.setType(IntactUtils.createMIAliasType(typeName, typeMI));
        }
        return alias;
    }

    public static <T extends AbstractIntactAlias> T createAliasNoType(Class<T> aliasClass) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        return createAliasNoType(aliasClass, "test synonym 2");
    }

    public static <T extends AbstractIntactAlias> T createAliasNoType(Class<T> aliasClass, String name) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        return createIntactAlias(aliasClass, null, null, name);
    }

    public static <T extends AbstractIntactAlias> T createAliasSynonym(Class<T> aliasClass) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        return createAliasSynonym(aliasClass, "test synonym");
    }

    public static <T extends AbstractIntactAlias> T createAliasSynonym(Class<T> aliasClass, String name) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        return createIntactAlias(aliasClass, Alias.SYNONYM, Alias.SYNONYM_MI, name);
    }

    public static Alias createAliasNoType() {
        return new DefaultAlias("test synonym 2");
    }

    public static Alias createAliasNoType(String name) {
        return new DefaultAlias(name);
    }

    public static Alias createAliasSynonym() {
        return AliasUtils.createAlias(Alias.SYNONYM, Alias.SYNONYM_MI,"test synonym");
    }

    public static Alias createAliasSynonym(String name) {
        return AliasUtils.createAlias(Alias.SYNONYM, Alias.SYNONYM_MI, name);
    }

    public static <T extends AbstractIntactAnnotation> T createIntactAnnotation(Class<T> annotationClass, String topicName, String topicMI, String description) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {

        return annotationClass.getConstructor(CvTerm.class, String.class).
                newInstance(IntactUtils.createMITopic(topicName, topicMI), description);
    }

    public static <T extends AbstractIntactAnnotation> T createAnnotationNoDescription(Class<T> annotationClass) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        return createAnnotationNoDescription(annotationClass, Annotation.CAUTION, Annotation.CAUTION_MI);
    }

    public static <T extends AbstractIntactAnnotation> T createAnnotationNoDescription(Class<T> annotationClass, String topicName, String topicMI) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        return createIntactAnnotation(annotationClass, topicName, topicMI, null);
    }

    public static <T extends AbstractIntactAnnotation> T createAnnotationComment(Class<T> annotationClass) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        return createIntactAnnotation(annotationClass, Annotation.COMMENT, Annotation.COMMENT_MI, "test comment");
    }

    public static <T extends AbstractIntactAnnotation> T createAnnotationComment(Class<T> annotationClass, String description) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        return createIntactAnnotation(annotationClass, Annotation.COMMENT, Annotation.COMMENT_MI, description);
    }

    public static Annotation createAnnotationNoDescription() {
        return AnnotationUtils.createCaution(null);
    }

    public static Annotation createAnnotationNoDescription(String topicName, String topicMI) {
        return AnnotationUtils.createAnnotation(topicName, topicMI, null);
    }

    public static Annotation createAnnotationComment() {
        return AnnotationUtils.createComment("test comment");
    }

    public static Annotation createAnnotationComment(String description) {
        return AnnotationUtils.createComment(description);
    }

    public static <T extends AbstractIntactConfidence> T createIntactConfidence(Class<T> confidenceClass, String typeName, String typeMI, String value) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {

        return confidenceClass.getConstructor(CvTerm.class, String.class).
                newInstance(IntactUtils.createMIConfidenceType(typeName, typeMI), value);
    }

    public static <T extends AbstractIntactConfidence> T createConfidenceAuthorScore(Class<T> annotationClass) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        return createIntactConfidence(annotationClass, "author-score", "MI:xxx1", "high");
    }

    public static <T extends AbstractIntactConfidence> T createConfidenceAuthorScore(Class<T> annotationClass, String value) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        return createIntactConfidence(annotationClass, "author-score", "MI:xxx1", value);
    }

    public static Confidence createConfidenceAuthorScore() {
        return createConfidenceAuthorScore("high");
    }

    public static Confidence createConfidenceAuthorScore(String value) {
        return ConfidenceUtils.createConfidence("author-score", "MI:xxx1", value);
    }

    public static <T extends AbstractIntactXref> T createIntactXref(Class<T> xrefClass, String dbName, String dbMI,
                                                                    String dbId, String qualifierName, String qualifierId)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {

        T xref = xrefClass.getConstructor(CvTerm.class, String.class).newInstance(IntactUtils.createMIDatabase(dbName, dbMI),dbId);
        if (qualifierName != null){
            xref.setQualifier(IntactUtils.createMIQualifier(qualifierName, qualifierId));
        }
        return xref;
    }

    public static <T extends AbstractIntactXref> T createPubmedXrefNoQualifier(Class<T> xrefClass) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        return createPubmedXrefNoQualifier(xrefClass, "12345");
    }

    public static <T extends AbstractIntactXref> T createPubmedXrefNoQualifier(Class<T> xrefClass, String id) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        return createIntactXref(xrefClass, Xref.PUBMED, Xref.PUBMED_MI, id, null, null);
    }

    public static <T extends AbstractIntactXref> T createXrefSeeAlso(Class<T> xrefClass) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        return createXrefSeeAlso(xrefClass, "IM-1-1");
    }

    public static <T extends AbstractIntactXref> T createXrefSeeAlso(Class<T> xrefClass, String id) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        return createIntactXref(xrefClass, Xref.IMEX, Xref.IMEX_MI, id, Xref.SEE_ALSO, Xref.SEE_ALSO_MI);
    }

    public static Xref createPubmedXrefNoQualifier() {
        return XrefUtils.createXref(Xref.PUBMED, Xref.PUBMED_MI, "12345");
    }

    public static Xref createPubmedXrefNoQualifier(String id) {
        return XrefUtils.createXref(Xref.PUBMED, Xref.PUBMED_MI, id);
    }

    public static Xref createXrefSeeAlso() {
        return XrefUtils.createXrefWithQualifier(Xref.IMEX, Xref.IMEX_MI, "IM-1-1", Xref.SEE_ALSO, Xref.SEE_ALSO_MI);
    }

    public static Xref createXrefSeeAlso(String id) {
        return XrefUtils.createXrefWithQualifier(Xref.IMEX, Xref.IMEX_MI, id, Xref.SEE_ALSO, Xref.SEE_ALSO_MI);
    }
}
