/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.model.*;
import uk.ac.ebi.intact.model.visitor.DefaultTraverser;
import uk.ac.ebi.intact.model.visitor.impl.JTreeBuilderVisitor;
import uk.ac.ebi.intact.model.visitor.impl.PrintVisitor;
import uk.ac.ebi.intact.persistence.dao.DaoFactory;
import uk.ac.ebi.intact.persistence.util.CgLibUtil;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Class for debugging / logging purposes
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>29-Aug-2006</pre>
 */
public class DebugUtil {

    private static final Log log = LogFactory.getLog( DebugUtil.class );

    /**
     * Returns the list of labels for the annotated objects provided
     *
     * @param intactObjects list of annotated objects
     *
     * @return a list of labels
     */
    public static List<String> labelList( Collection<? extends AnnotatedObject> intactObjects ) {
        List<String> labels = new ArrayList<String>( intactObjects.size() );

        for ( AnnotatedObject io : intactObjects ) {
            labels.add( io.getShortLabel() );
        }

        return labels;
    }

    /**
     * Returns the list of ACs for the annotated objects provided
     *
     * @param intactObjects list of intact objects
     *
     * @return a list of Acs
     */
    public static List<String> acList( Collection<? extends IntactObject> intactObjects ) {
        List<String> acs = new ArrayList<String>( intactObjects.size() );

        for ( IntactObject io : intactObjects ) {
            acs.add( io.getAc() );
        }

        return acs;
    }

    /**
     * Prints an IntactObject recursively
     * @param intactObject
     * @param ps
     */
    public static void printIntactObject(IntactObject intactObject, PrintStream ps) {
        DefaultTraverser traverser = new DefaultTraverser();
        traverser.traverse(intactObject, new PrintVisitor(ps));
    }

    public static void renderIntactObjectAsTree( IntactObject intactObject ) {
        renderIntactObjectAsTree( intactObject, null );
    }

    /**
     * Renders a JFrame with the tree for the provided IntactObject
     *
     * @param intactObject the object to be rendered
     */
    public static void renderIntactObjectAsTree( IntactObject intactObject, String windowTitle ) {
        DefaultTraverser traverser = new DefaultTraverser();
        final JTreeBuilderVisitor builderVisitor = new JTreeBuilderVisitor();
        traverser.traverse( intactObject, builderVisitor );
        if ( windowTitle == null ) {
            builderVisitor.renderTree();
        } else {
            builderVisitor.renderTree( windowTitle );
        }
    }

    /**
     * Prints the label, ac, and the counts of some attributes (xrefs, aliases and annotations) of an AnnotatedObject
     * @param ao Annotated object to prints
     *
     * @since 1.8.0
     */
    public static String annotatedObjectToString(AnnotatedObject ao, boolean showAttributesCount) {
        Class clazz = CgLibUtil.removeCglibEnhanced(ao.getClass());
        StringBuilder sb = new StringBuilder();

        sb.append(clazz.getSimpleName()).append("{label=").append(ao.getShortLabel()).append(", ac=").append(ao.getAc());

        if (showAttributesCount) {
            sb.append(", xrefCount=").append(ao.getXrefs().size()).append(", aliasCount=").append(ao.getAliases().size()).append(", annotationsCount=").append(ao.getAnnotations().size());
        }
        sb.append("}");

        return sb.toString();
    }

    /**
     * Prints the most counts in the database.
     * @param ps The printStream to use
     *
     * @since 1.9.0
     */
    public static void printDatabaseCounts(PrintStream ps) {
        final DaoFactory daoFactory = IntactContext.getCurrentInstance().getDataContext().getDaoFactory();

        ps.println("Publications: "+ daoFactory.getPublicationDao().countAll());
        ps.println("\tXrefs: "+ daoFactory.getXrefDao(PublicationXref.class).countAll());
        ps.println("\tAliases: "+ daoFactory.getAliasDao(PublicationAlias.class).countAll());
        ps.println("Experiments: "+ daoFactory.getExperimentDao().countAll());
        ps.println("\tXrefs: "+ daoFactory.getXrefDao(ExperimentXref.class).countAll());
        ps.println("\tAliases: "+ daoFactory.getAliasDao(ExperimentAlias.class).countAll());
        ps.println("Interactors: "+ daoFactory.getInteractorDao().countAll());
        ps.println("\tInteractions: "+ daoFactory.getInteractionDao().countAll());
        ps.println("\tPolymers: " + daoFactory.getPolymerDao().countAll());
        ps.println("\t\tProteins: "+ daoFactory.getProteinDao().countAll());
        ps.println("\t\tNucleic Acids: "+ daoFactory.getInteractorDao(NucleicAcidImpl.class).countAll());
        ps.println("\tSmall molecules: " + daoFactory.getInteractorDao(SmallMoleculeImpl.class).countAll());
        ps.println("\tInteractor Xrefs: "+ daoFactory.getXrefDao(InteractorXref.class).countAll());
        ps.println("\tInteractor Aliases: "+ daoFactory.getAliasDao(InteractorAlias.class).countAll());
        ps.println("Components: "+ daoFactory.getComponentDao().countAll());
        ps.println("Features: "+ daoFactory.getFeatureDao().countAll());
        ps.println("\tRanges: "+ daoFactory.getRangeDao().countAll());
        ps.println("CvObjects: "+ daoFactory.getCvObjectDao().countAll());
        ps.println("BioSources: "+ daoFactory.getBioSourceDao().countAll());
        ps.println("Annotations: "+ daoFactory.getAnnotationDao().countAll());
        ps.println("Institutions: "+ daoFactory.getInstitutionDao().countAll());

    }
}