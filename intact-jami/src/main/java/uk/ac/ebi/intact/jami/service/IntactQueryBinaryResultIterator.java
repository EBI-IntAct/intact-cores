package uk.ac.ebi.intact.jami.service;

import psidev.psi.mi.jami.binary.BinaryInteraction;
import psidev.psi.mi.jami.binary.expansion.ComplexExpansionException;
import psidev.psi.mi.jami.binary.expansion.ComplexExpansionMethod;
import psidev.psi.mi.jami.exception.MIIOException;
import psidev.psi.mi.jami.model.Interaction;

import java.util.*;

/**
 * Iterator for IntAct database binary results
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>21/02/14</pre>
 */

public class IntactQueryBinaryResultIterator<T extends Interaction, B extends BinaryInteraction> implements Iterator<B> {

    private ComplexExpansionMethod<T,B> expansionMethod;
    private Collection<B> binaryInteractions;
    private Iterator<B> binaryIterator;
    private IntactQueryResultIterator<T> queryIterator;
    private B currentBinary;

    public IntactQueryBinaryResultIterator(IntactService<T> service, ComplexExpansionMethod<T,B> expansionMethod){
        this.queryIterator = new IntactQueryResultIterator<T>(service);
        if (expansionMethod == null){
           throw new IllegalArgumentException("The complex expansion is mandatory");
        }
        this.expansionMethod = expansionMethod;
        prepareNextObject();
    }

    public IntactQueryBinaryResultIterator(IntactService<T> service, int batch, ComplexExpansionMethod<T,B> expansionMethod){
        this.queryIterator = new IntactQueryResultIterator<T>(service, batch);
        if (expansionMethod == null){
            throw new IllegalArgumentException("The complex expansion is mandatory");
        }
        this.expansionMethod = expansionMethod;
        prepareNextObject();
    }

    public IntactQueryBinaryResultIterator(IntactService<T> service, String query, String queryCount, Map<String, Object> parameters, ComplexExpansionMethod<T,B> expansionMethod){
        this.queryIterator = new IntactQueryResultIterator<T>(service, query, queryCount, parameters);
        if (expansionMethod == null){
            throw new IllegalArgumentException("The complex expansion is mandatory");
        }
        this.expansionMethod = expansionMethod;
        prepareNextObject();
    }

    public IntactQueryBinaryResultIterator(IntactService<T> service, int batch, String query, String queryCount, Map<String, Object> parameters, ComplexExpansionMethod<T,B> expansionMethod){
        this.queryIterator = new IntactQueryResultIterator<T>(service, batch, query, queryCount, parameters);
        if (expansionMethod == null){
            throw new IllegalArgumentException("The complex expansion is mandatory");
        }
        this.expansionMethod = expansionMethod;
        prepareNextObject();
    }

    protected void prepareNextObject(){

        if (this.binaryIterator.hasNext()){
            this.currentBinary = this.binaryIterator.next();
        }
        else if (!this.queryIterator.hasNext()){
            this.currentBinary = null;
        }
        else{
            T intactObject = this.queryIterator.next();
            while(!this.expansionMethod.isInteractionExpandable(intactObject) &&
                    this.queryIterator.hasNext()){
                intactObject = this.queryIterator.next();
            }
            if (this.expansionMethod.isInteractionExpandable(intactObject)){
                try {
                    this.binaryInteractions = this.expansionMethod.expand(intactObject);
                } catch (ComplexExpansionException e) {
                    throw new MIIOException("Impossible to expand n-ary interaction", e);
                }
                this.binaryIterator = this.binaryInteractions.iterator();
                if (this.binaryIterator.hasNext()){
                    this.currentBinary = this.binaryIterator.next();
                }
                else{
                    this.currentBinary = null;
                }
            }
            else{
                this.currentBinary = null;
            }
        }
    }

    public boolean hasNext() {
        return this.currentBinary != null;
    }

    public B next() {
        if (!hasNext()){
            throw new NoSuchElementException("Does not have any new elements");
        }
        B object = this.currentBinary;
        prepareNextObject();
        return object;
    }

    public void remove() {
        throw new UnsupportedOperationException("An IntAct query iterator does not support the remove method");
    }
}
