package uk.ac.ebi.intact.jami.service;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Iterator for IntAct database results
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/02/14</pre>
 */

public class IntactQueryResultIterator<T> implements Iterator<T> {

    private IntactService<T> service;
    private long totalCount = 0;
    private T currentObject;
    private long currentCount = 0;
    private int batch = 200;
    private List<T> chunk;
    private Iterator<T> chunkIterator;
    private String query;
    private Map<String, Object> queryParameters;

    public IntactQueryResultIterator(IntactService<T> service){
        if (service == null){
             throw new IllegalArgumentException("The IntAct service cannot be null");
        }
        this.service = service;
        this.totalCount = this.service.countAll();
        this.query = null;
        this.queryParameters = null;

        prepareNextObject();

    }

    public IntactQueryResultIterator(IntactService<T> service, int batch){
        if (service == null){
            throw new IllegalArgumentException("The IntAct service cannot be null");
        }
        this.service = service;
        this.totalCount = this.service.countAll();
        this.batch = batch;
        this.query = null;
        this.queryParameters = null;

        prepareNextObject();
    }

    public IntactQueryResultIterator(IntactService<T> service, String query, String queryCount, Map<String, Object> parameters){
        if (service == null){
            throw new IllegalArgumentException("The IntAct service cannot be null");
        }
        this.service = service;
        this.query = query;
        this.queryParameters = parameters;
        this.totalCount = this.service.countAll(queryCount, parameters);

        prepareNextObject();

    }

    public IntactQueryResultIterator(IntactService<T> service, int batch, String query, String queryCount, Map<String, Object> parameters){
        if (service == null){
            throw new IllegalArgumentException("The IntAct service cannot be null");
        }
        this.service = service;
        this.query = query;
        this.queryParameters = parameters;
        if (queryCount != null){
            this.totalCount = this.service.countAll(queryCount, parameters);
        }
        else{
            this.totalCount = this.service.countAll();
        }
        this.batch = batch;

        prepareNextObject();
    }

    protected void prepareNextObject(){

        if (this.chunkIterator != null && this.chunkIterator.hasNext()){
            this.currentObject = this.chunkIterator.next();
        }
        else if (totalCount == currentCount){
            this.currentObject = null;
        }
        else{
            long max = Math.min(batch, totalCount - currentCount);
            if (this.query == null){
                this.chunk = this.service.fetchIntactObjects((int)currentCount, (int)max);
            }
            else{
                this.chunk = this.service.fetchIntactObjects(this.query, this.queryParameters, (int)currentCount, (int)max);
            }
            this.chunkIterator = this.chunk.iterator();
            if (this.chunkIterator.hasNext()){
                this.currentObject = this.chunkIterator.next();
            }
            else{
                this.currentObject = null;
            }
            this.currentCount+=max;
        }
    }

    public boolean hasNext() {
        return this.currentObject != null;
    }

    public T next() {
        if (!hasNext()){
            throw new NoSuchElementException("Does not have any new elements");
        }
        T object = this.currentObject;
        prepareNextObject();
        return object;
    }

    public void remove() {
        throw new UnsupportedOperationException("An IntAct query iterator does not support the remove method");
    }
}
