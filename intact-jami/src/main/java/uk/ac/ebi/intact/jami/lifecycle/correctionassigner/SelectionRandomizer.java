package uk.ac.ebi.intact.jami.lifecycle.correctionassigner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 */
public class SelectionRandomizer<T> {

    private List<T> objects;

    public SelectionRandomizer() {
        this.objects = new ArrayList<T>();
    }

    public SelectionRandomizer addObject(T object, int weight) {
        for (int i=0; i<weight; i++) {
            objects.add(object);
        }

        return this;
    }

    public T randomSelection() {
        return randomSelection(null);
    }

    public T randomSelection(T objectToExclude) {
        List<T> shuffled = new ArrayList<T>(objects);
        Collections.shuffle(shuffled);

        for (T obj : shuffled) {
            if (!obj.equals(objectToExclude)) {
                return obj;
            }
        }

        return null;
    }
}
