package uk.ac.ebi.intact.jami.io.reader;

import psidev.psi.mi.jami.binary.BinaryInteraction;
import psidev.psi.mi.jami.binary.expansion.ComplexExpansionMethod;
import psidev.psi.mi.jami.datasource.BinaryInteractionStream;
import psidev.psi.mi.jami.datasource.MIDataSource;
import psidev.psi.mi.jami.exception.MIIOException;
import psidev.psi.mi.jami.factory.options.MIDataSourceOptions;
import psidev.psi.mi.jami.model.Interaction;
import uk.ac.ebi.intact.jami.model.extension.factory.IntactBinaryInteractionFactory;
import uk.ac.ebi.intact.jami.service.IntactQueryBinaryResultIterator;

import java.util.Iterator;
import java.util.Map;

/**
 * Abstract class for Intact binary interaction data source
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>16/10/13</pre>
 */

public abstract class AbstractIntactBinaryStream<T extends Interaction, B extends BinaryInteraction> implements MIDataSource, BinaryInteractionStream<B> {

    private ComplexExpansionMethod<T,B> expansionMethod;
    private AbstractIntactStream<T> delegate;

    public AbstractIntactBinaryStream(AbstractIntactStream<T> delegate){
        if (delegate == null){
            throw new IllegalArgumentException("Delegate interaction stream is required and cannot be null");
        }
        this.delegate = delegate;
    }

    public String getQuery() {
        return this.delegate.getQuery();
    }

    public void setQuery(String query) {
        this.delegate.setQuery(query);
    }

    public Map<String, Object> getQueryParameters() {
        return this.delegate.getQueryParameters();
    }

    public void setQueryParameters(Map<String, Object> queryParameters) {
        this.delegate.setQueryParameters(queryParameters);
    }

    public String getCountQuery() {
        return this.delegate.getCountQuery();
    }

    public void setCountQuery(String countQuery) {
        this.delegate.setCountQuery(countQuery);
    }

    public ComplexExpansionMethod<T, B> getExpansionMethod() {
        if (this.expansionMethod == null){
           initialiseDefaultExpansionMethod();
        }
        return expansionMethod;
    }

    public void setExpansionMethod(ComplexExpansionMethod<T, B> expansionMethod) {
        this.expansionMethod = expansionMethod;
        if (expansionMethod != null){
            this.expansionMethod.setBinaryInteractionFactory(new IntactBinaryInteractionFactory());
        }
    }

    public void initialiseContext(Map<String, Object> options) {

        if (options == null){
            this.delegate.initialiseContext(options);
            return;
        }

        // initialise complex expansion
        if (options.containsKey(MIDataSourceOptions.COMPLEX_EXPANSION_OPTION_KEY)){
            setExpansionMethod((ComplexExpansionMethod<T,B>)options.get(MIDataSourceOptions.COMPLEX_EXPANSION_OPTION_KEY));
        }
    }

    public Iterator<B> getInteractionsIterator() throws MIIOException {
        if (!this.delegate.isInitialised()){
            this.delegate.initialiseContext(null);
        }
        return new IntactQueryBinaryResultIterator<T,B>(this.delegate.getIntactService(), getQuery(), getCountQuery(), getQueryParameters(), getExpansionMethod());
    }

    public void close() throws MIIOException{
        if (this.delegate.isInitialised()){
            this.delegate.close();
            this.expansionMethod = null;
        }
    }

    public void reset() throws MIIOException{
        if (this.delegate.isInitialised()){
            this.delegate.reset();
            this.expansionMethod = null;
        }
    }

    protected AbstractIntactStream<T> getDelegate() {
        return delegate;
    }

    protected abstract void initialiseDefaultExpansionMethod();

}
