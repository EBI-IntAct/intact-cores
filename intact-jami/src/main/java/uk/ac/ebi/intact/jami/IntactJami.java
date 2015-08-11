package uk.ac.ebi.intact.jami;

import psidev.psi.mi.jami.factory.InteractionWriterFactory;
import psidev.psi.mi.jami.factory.MIDataSourceFactory;
import psidev.psi.mi.jami.factory.options.InteractionWriterOptions;
import psidev.psi.mi.jami.factory.options.MIDataSourceOptions;
import psidev.psi.mi.jami.factory.options.MIFileDataSourceOptions;
import psidev.psi.mi.jami.model.ComplexType;
import psidev.psi.mi.jami.model.InteractionCategory;
import uk.ac.ebi.intact.jami.io.reader.*;
import uk.ac.ebi.intact.jami.io.writer.IntactEvidenceWriter;
import uk.ac.ebi.intact.jami.io.writer.IntactMixedWriter;
import uk.ac.ebi.intact.jami.io.writer.IntactModelledWriter;
import uk.ac.ebi.intact.jami.utils.IntactDataSourceOptions;
import uk.ac.ebi.intact.jami.utils.IntactWriterOptions;

import java.util.HashMap;
import java.util.Map;

/**
 * Intact jami environment
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24/02/14</pre>
 */

public class IntactJami {

    public static final String INTACT_INPUT_TYPE = "intact_db";

    public static void initialiseAllFactories(){

        initialiseAllMIDataSources();

        initialiseAllInteractionWriters();
    }

    public static void initialiseAllInteractionWriters() {

        InteractionWriterFactory writerFactory = InteractionWriterFactory.getInstance();
        Map<String, Object> supportedOptions1 = createIntactWriterOptions(InteractionCategory.mixed);
        writerFactory.registerDataSourceWriter(IntactMixedWriter.class, supportedOptions1);

        Map<String, Object> supportedOptions2 = createIntactWriterOptions(InteractionCategory.evidence);
        writerFactory.registerDataSourceWriter(IntactEvidenceWriter.class, supportedOptions2);

        Map<String, Object> supportedOptions3 = createIntactWriterOptions(InteractionCategory.modelled);
        writerFactory.registerDataSourceWriter(IntactModelledWriter.class, supportedOptions3);
    }

    public static void initialiseAllMIDataSources() {

        MIDataSourceFactory datasourceFactory = MIDataSourceFactory.getInstance();

        Map<String, Object> supportedOptions3 = createDataSourceOptions(true, InteractionCategory.evidence, ComplexType.n_ary, false);
        datasourceFactory.registerDataSource(IntactEvidenceStream.class, supportedOptions3);

        Map<String, Object> supportedOptions4 = createDataSourceOptions(true, InteractionCategory.evidence, ComplexType.binary, true);
        datasourceFactory.registerDataSource(IntactBinaryEvidenceStream.class, supportedOptions4);

        Map<String, Object> supportedOptions7 = createDataSourceOptions(false, InteractionCategory.evidence, ComplexType.n_ary, false);
        datasourceFactory.registerDataSource(IntactEvidenceSource.class, supportedOptions7);

        Map<String, Object> supportedOptions8 = createDataSourceOptions(false, InteractionCategory.evidence, ComplexType.binary, true);
        datasourceFactory.registerDataSource(IntactBinaryEvidenceSource.class, supportedOptions8);

        Map<String, Object> supportedOptions5 = createDataSourceOptions(true, InteractionCategory.modelled, ComplexType.n_ary, false);
        datasourceFactory.registerDataSource(IntactModelledStream.class, supportedOptions5);

        Map<String, Object> supportedOptions6 = createDataSourceOptions(true, InteractionCategory.modelled, ComplexType.binary, false);
        datasourceFactory.registerDataSource(IntactModelledBinaryStream.class, supportedOptions6);

        Map<String, Object> supportedOptions9 = createDataSourceOptions(false, InteractionCategory.modelled, ComplexType.n_ary, false);
        datasourceFactory.registerDataSource(IntactModelledSource.class, supportedOptions9);

        Map<String, Object> supportedOptions10 = createDataSourceOptions(false, InteractionCategory.modelled, ComplexType.binary, false);
        datasourceFactory.registerDataSource(IntactModelledBinarySource.class, supportedOptions10);
    }

    private static Map<String, Object> createIntactWriterOptions(InteractionCategory interactionCategory) {
        Map<String, Object> supportedOptions4 = new HashMap<String, Object>(6);
        supportedOptions4.put(InteractionWriterOptions.OUTPUT_FORMAT_OPTION_KEY, INTACT_INPUT_TYPE);
        supportedOptions4.put(InteractionWriterOptions.INTERACTION_CATEGORY_OPTION_KEY, interactionCategory);
        supportedOptions4.put(IntactWriterOptions.INTERACTION_SERVICE_NAME_OPTION, null);
        supportedOptions4.put(IntactWriterOptions.SPRING_CONFIG_OPTION, null);
        return supportedOptions4;
    }

    private static Map<String, Object> createDataSourceOptions(boolean streaming, InteractionCategory objectCategory, ComplexType complexType,
                                                               boolean needExpansion) {
        Map<String, Object> supportedOptions1 = new HashMap<String, Object>(10);
        supportedOptions1.put(MIFileDataSourceOptions.INPUT_TYPE_OPTION_KEY, INTACT_INPUT_TYPE);
        supportedOptions1.put(MIFileDataSourceOptions.STREAMING_OPTION_KEY, streaming);
        supportedOptions1.put(MIFileDataSourceOptions.INTERACTION_CATEGORY_OPTION_KEY, objectCategory);
        supportedOptions1.put(MIFileDataSourceOptions.COMPLEX_TYPE_OPTION_KEY, complexType);
        if (needExpansion){
           supportedOptions1.put(MIDataSourceOptions.COMPLEX_EXPANSION_OPTION_KEY, null);
        }
        supportedOptions1.put(IntactDataSourceOptions.SPRING_CONFIG_OPTION, null);
        supportedOptions1.put(IntactDataSourceOptions.INTERACTION_SERVICE_NAME_OPTION, null);
        supportedOptions1.put(IntactDataSourceOptions.HQL_QUERY_PARAMETERS_OPTION, null);
        supportedOptions1.put(IntactDataSourceOptions.HQL_COUNT_QUERY_OPTION, null);
        supportedOptions1.put(IntactDataSourceOptions.HQL_QUERY_OPTION, null);

        return supportedOptions1;
    }
}
