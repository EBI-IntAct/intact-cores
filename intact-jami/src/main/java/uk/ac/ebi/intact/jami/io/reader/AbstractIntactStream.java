package uk.ac.ebi.intact.jami.io.reader;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import psidev.psi.mi.jami.datasource.InteractionStream;
import psidev.psi.mi.jami.datasource.MIDataSource;
import psidev.psi.mi.jami.exception.MIIOException;
import psidev.psi.mi.jami.model.Interaction;
import uk.ac.ebi.intact.jami.ApplicationContextProvider;
import uk.ac.ebi.intact.jami.service.IntactQueryResultIterator;
import uk.ac.ebi.intact.jami.service.IntactService;
import uk.ac.ebi.intact.jami.utils.IntactDataSourceOptions;

import java.util.Iterator;
import java.util.Map;

/**
 * Abstract class for Intact interaction data source
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>16/10/13</pre>
 */

public abstract class AbstractIntactStream<T extends Interaction> implements MIDataSource, InteractionStream<T> {

    private boolean isInitialised = false;
    private IntactService<T> intactService;
    private String countQuery;
    private String query;
    private Map<String, Object> queryParameters;

    public AbstractIntactStream(){
        if (isSpringContextInitialised()){
            initialiseDefaultIntactService();
            isInitialised = true;
        }
    }

    public AbstractIntactStream(String springFile, String intactServiceName) {
        initialiseSpringContext(springFile, intactServiceName);
        isInitialised = true;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Map<String, Object> getQueryParameters() {
        return queryParameters;
    }

    public void setQueryParameters(Map<String, Object> queryParameters) {
        this.queryParameters = queryParameters;
    }

    public String getCountQuery() {
        return countQuery;
    }

    public void setCountQuery(String countQuery) {
        this.countQuery = countQuery;
    }

    public void initialiseContext(Map<String, Object> options) {
        IntactService<T> intactService = null;

        if (options == null && !isInitialised){
            initialiseSpringContext(null, null);
            if (this.intactService == null){
                throw new IllegalStateException("The IntAct interaction datasource has not been initialised. The options for the IntAct interaction datasource should contains at least "+ IntactDataSourceOptions.SPRING_CONFIG_OPTION + " to know how to connect to " +
                        "the IntAct database using a valid IntactService<Interaction> bean.");
            }
            else{
                isInitialised = true;
            }
            return;
        }

        // first load spring context if not done yet
        if (options.containsKey(IntactDataSourceOptions.SPRING_CONFIG_OPTION)
                && options.containsKey(IntactDataSourceOptions.INTERACTION_SERVICE_NAME_OPTION)){
            initialiseSpringContext((String)options.get(IntactDataSourceOptions.SPRING_CONFIG_OPTION), (String)options.get(IntactDataSourceOptions.INTERACTION_SERVICE_NAME_OPTION));
        }
        else if (options.containsKey(IntactDataSourceOptions.SPRING_CONFIG_OPTION)){
            initialiseSpringContext((String)options.get(IntactDataSourceOptions.SPRING_CONFIG_OPTION), null);
        }
        else if (options.containsKey(IntactDataSourceOptions.INTERACTION_SERVICE_NAME_OPTION)){
            initialiseSpringContext(null, (String)options.get(IntactDataSourceOptions.INTERACTION_SERVICE_NAME_OPTION));
        }
        else{
            initialiseSpringContext(null, null);
        }

        // load special count query
        if (options.containsKey(IntactDataSourceOptions.HQL_COUNT_QUERY_OPTION)){
            this.countQuery = (String)options.get(IntactDataSourceOptions.HQL_COUNT_QUERY_OPTION);
        }
        // load special query
        if (options.containsKey(IntactDataSourceOptions.HQL_QUERY_OPTION)){
            this.query = (String)options.get(IntactDataSourceOptions.HQL_QUERY_OPTION);
        }
        // load query parameters
        if (options.containsKey(IntactDataSourceOptions.HQL_QUERY_PARAMETERS_OPTION)){
            this.queryParameters = (Map<String, Object>)options.get(IntactDataSourceOptions.HQL_QUERY_PARAMETERS_OPTION);
        }

        if (this.intactService == null){
            throw new IllegalStateException("The IntAct interaction datasource has not been initialised. The options for the IntAct interaction datasource should contains at least "+ IntactDataSourceOptions.SPRING_CONFIG_OPTION + " to know how to connect to " +
                    "the IntAct database using a valid IntactService<Interaction> bean.");
        }
        else{
            isInitialised = true;
        }
    }

    public Iterator<T> getInteractionsIterator() throws MIIOException {
        if (!isInitialised){
            initialiseContext(null);
        }
        return new IntactQueryResultIterator<T>(getIntactService(), getQuery(), getCountQuery(), getQueryParameters());
    }

    public void close() throws MIIOException{
        if (isInitialised){
            this.intactService = null;
            this.queryParameters = null;
            this.query = null;
            this.countQuery = null;
            isInitialised = false;
        }
    }

    public void reset() throws MIIOException{
        if (isInitialised){
            this.intactService = null;
            this.queryParameters = null;
            this.query = null;
            this.countQuery = null;
            isInitialised = false;
        }
    }

    protected abstract boolean isSpringContextInitialised();

    protected abstract void initialiseDefaultIntactService();

    protected void initialiseDefaultSpringContext(){
        if (!isSpringContextInitialised()){
            ClassPathXmlApplicationContext springContext = new ClassPathXmlApplicationContext(new String [] {"classpath*:/META-INF/intact-jami.spring.xml"}, ApplicationContextProvider.getApplicationContext());
            springContext.registerShutdownHook();
        }
    }

    protected IntactService<T> getIntactService() {
        return intactService;
    }

    protected void setIntactService(IntactService<T> service){
        this.intactService = service;
    }

    protected boolean isInitialised() {
        return isInitialised;
    }

    protected void initialiseSpringContext(String springFile, String intactServiceName){
        if (springFile != null){
            ClassPathXmlApplicationContext springContext = new ClassPathXmlApplicationContext(new String [] {springFile}, ApplicationContextProvider.getApplicationContext());
            springContext.registerShutdownHook();
        }
        else if (!isSpringContextInitialised()){
            initialiseDefaultSpringContext();
        }

        // load special service bean
        if (intactServiceName != null){
            this.intactService = ApplicationContextProvider.getBean(intactServiceName, IntactService.class);
        }
        else{
            initialiseDefaultIntactService();
        }
    }

}
