package uk.ac.ebi.intact.jami.synchronizer.listener.impl;

import uk.ac.ebi.intact.jami.model.IntactPrimaryObject;
import uk.ac.ebi.intact.jami.synchronizer.listener.DbSynchronizerListener;

import java.util.HashMap;
import java.util.Map;

/**
 * This listener will report statistics about each event
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>17/11/14</pre>
 */

public class DbSynchronizerStatisticsReporter implements DbSynchronizerListener{

    private Map<Class, Integer> persistedCounts;
    private Map<Class, Integer> mergedCounts;
    private Map<Class, Integer> deletedCounts;
    private Map<Class, Integer> mergedTransientCounts;
    private Map<Class, Integer> transientReplacedCounts;

    public DbSynchronizerStatisticsReporter(){
        persistedCounts = new HashMap<Class, Integer>();
        mergedCounts = new HashMap<Class, Integer>();
        deletedCounts = new HashMap<Class, Integer>();
        mergedTransientCounts = new HashMap<Class, Integer>();
        transientReplacedCounts = new HashMap<Class, Integer>();
    }

    @Override
    public void onPersisted(IntactPrimaryObject object) {
       if (persistedCounts.containsKey(object.getClass())){
           int number = persistedCounts.get(object.getClass())+1;
           persistedCounts.put(object.getClass(), number);
       }
        else{
           persistedCounts.put(object.getClass(), 1);
       }
    }

    @Override
    public void onMerged(IntactPrimaryObject object, IntactPrimaryObject existingObject) {
        if (mergedCounts.containsKey(object.getClass())){
            int number = mergedCounts.get(object.getClass())+1;
            mergedCounts.put(object.getClass(), number);
        }
        else{
            mergedCounts.put(object.getClass(), 1);
        }
    }

    @Override
    public void onTransientMergedWithDbInstance(IntactPrimaryObject object, IntactPrimaryObject existingObject) {
        if (mergedTransientCounts.containsKey(object.getClass())){
            int number = mergedTransientCounts.get(object.getClass())+1;
            mergedTransientCounts.put(object.getClass(), number);
        }
        else{
            mergedTransientCounts.put(object.getClass(), 1);
        }
    }

    @Override
    public void onReplacedWithDbInstance(IntactPrimaryObject object, IntactPrimaryObject existingObject) {
        if (transientReplacedCounts.containsKey(object.getClass())){
            int number = transientReplacedCounts.get(object.getClass())+1;
            transientReplacedCounts.put(object.getClass(), number);
        }
        else{
            transientReplacedCounts.put(object.getClass(), 1);
        }
    }

    @Override
    public void onDeleted(IntactPrimaryObject object) {
        if (deletedCounts.containsKey(object.getClass())){
            int number = deletedCounts.get(object.getClass())+1;
            deletedCounts.put(object.getClass(), number);
        }
        else{
            deletedCounts.put(object.getClass(), 1);
        }
    }
}
