package uk.ac.ebi.intact;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.hibernate.dialect.H2Dialect;
import org.junit.Ignore;
import org.junit.Test;
import uk.ac.ebi.intact.core.unit.IntactMockBuilder;
import uk.ac.ebi.intact.core.util.SchemaUtils;
import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.model.Protein;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class PlaygroundTest {

    @Test
    @Ignore
    public void printH2Schema() {
        for (String str : SchemaUtils.generateCreateSchemaDDL( H2Dialect.class.getName())) {
            System.out.println(str);
        }
    }

    @Test
    @Ignore
    public void printPostgres2Schema() {
        for (String str : SchemaUtils.generateCreateSchemaDDLForPostgreSQL()) {
            System.out.println(str);
        }
    }

    @Test
    @Ignore
    public void biMap() {
        BiMap<String,AnnotatedObject> biMap = HashBiMap.create();
        final Protein p1 = new IntactMockBuilder().createProteinRandom();
        final Protein p2 = new IntactMockBuilder().createProteinRandom();
        final Protein p3 = new IntactMockBuilder().createProteinRandom();
        biMap.put("one", p1);
        biMap.put("two", p2);
        biMap.put("three", p3);

        System.out.println(biMap.inverse().get(p1));
        System.out.println(biMap.inverse().get(p3));
        System.out.println(biMap.inverse().get(p2));
    }
}
