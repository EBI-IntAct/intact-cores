package uk.ac.ebi.intact.util;

import java.util.Collection;
import java.util.Iterator;

/**
 * Hashcode utilities.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 1.9.7
 */
public class HashCodeUtils {

    /**
     * Hashcode generator that doesn't take into account order or elements in the given collection.
     * @param c
     * @return
     */
    public static int collectionHashCode(Collection c) {
        int h = 0;
        Iterator i = c.iterator();
        while (i.hasNext()) {
            Object obj = i.next();
            if (obj != null)
                h += obj.hashCode();
        }
        return h;
    }

}
