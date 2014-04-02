package uk.ac.ebi.intact.jami;

import psidev.psi.mi.jami.model.Alias;
import psidev.psi.mi.jami.model.impl.DefaultAlias;
import psidev.psi.mi.jami.utils.AliasUtils;
import uk.ac.ebi.intact.jami.model.extension.AbstractIntactAlias;
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
        return AliasUtils.createAlias(Alias.SYNONYM, Alias.SYNONYM_MI,name);
    }
}
