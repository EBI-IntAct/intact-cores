package uk.ac.ebi.intact.jami.io.writer;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import psidev.psi.mi.jami.datasource.InteractionWriter;
import psidev.psi.mi.jami.exception.MIIOException;
import psidev.psi.mi.jami.model.Interaction;
import uk.ac.ebi.intact.jami.ApplicationContextProvider;
import uk.ac.ebi.intact.jami.service.IntactService;
import uk.ac.ebi.intact.jami.synchronizer.FinderException;
import uk.ac.ebi.intact.jami.synchronizer.PersisterException;
import uk.ac.ebi.intact.jami.synchronizer.SynchronizerException;
import uk.ac.ebi.intact.jami.utils.IntactWriterOptions;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * Abstract class for Intact interaction writer
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>16/10/13</pre>
 */

public abstract class AbstractIntactWriter<T extends Interaction> implements InteractionWriter<T> {

    private boolean isInitialised = false;
    private IntactService<T> intactService;

    public AbstractIntactWriter(){
        if (isSpringContextInitialised()){
            initialiseDefaultIntactService();
            isInitialised = true;
        }
    }

    public AbstractIntactWriter(String springFile, String intactServiceName) {
        initialiseSpringContext(springFile, intactServiceName);
        isInitialised = true;
    }

    public void initialiseContext(Map<String, Object> options) {
        IntactService<T> intactService = null;

        if (options == null && !isInitialised){
            initialiseSpringContext(null, null);
            if (this.intactService == null){
                throw new IllegalStateException("The IntAct interaction writer has not been initialised. The options for the IntAct interaction writer should contains at least "+ IntactWriterOptions.SPRING_CONFIG_OPTION + " to know how to connect to " +
                        "the IntAct database using a valid IntactService<Interaction> bean.");
            }
            else{
                isInitialised = true;
            }
            return;
        }

        // first load spring context if not done yet
        if (options.containsKey(IntactWriterOptions.SPRING_CONFIG_OPTION)
                && options.containsKey(IntactWriterOptions.INTERACTION_SERVICE_NAME_OPTION)){
            initialiseSpringContext((String)options.get(IntactWriterOptions.SPRING_CONFIG_OPTION), (String)options.get(IntactWriterOptions.INTERACTION_SERVICE_NAME_OPTION));
        }
        else if (options.containsKey(IntactWriterOptions.SPRING_CONFIG_OPTION)){
            initialiseSpringContext((String)options.get(IntactWriterOptions.SPRING_CONFIG_OPTION), null);
        }
        else if (options.containsKey(IntactWriterOptions.INTERACTION_SERVICE_NAME_OPTION)){
            initialiseSpringContext(null, (String)options.get(IntactWriterOptions.INTERACTION_SERVICE_NAME_OPTION));
        }
        else{
            initialiseSpringContext(null, null);
        }

        if (this.intactService == null){
            throw new IllegalStateException("The IntAct interaction writer has not been initialised. The options for the IntAct interaction writer should contains at least "+ IntactWriterOptions.SPRING_CONFIG_OPTION + " to know how to connect to " +
                    "the IntAct database using a valid IntactService<Interaction> bean.");
        }
        else{
            isInitialised = true;
        }
    }

    public void start() throws MIIOException {
        // nothing to do
    }

    public void end() throws MIIOException {
        // nothing to do
    }

    public void write(T t) throws MIIOException {
        if (!isInitialised()){
            throw new IllegalStateException("The IntAct interaction writer has not been initialised. The options for the IntAct interaction writer should contains at least "+ IntactWriterOptions.SPRING_CONFIG_OPTION + " to know how to connect to " +
                    "the IntAct database using a valid IntactService<Interaction> bean.");
        }

        try {
            getIntactService().saveOrUpdate(t);
        } catch (PersisterException e) {
            throw new MIIOException("Cannot persist interaction "+t.toString(), e);
        } catch (FinderException e) {
            throw new MIIOException("Cannot persist interaction "+t.toString(), e);
        } catch (SynchronizerException e) {
            throw new MIIOException("Cannot persist interaction "+t.toString(), e);
        }
    }

    public void write(Collection<? extends T> ts) throws MIIOException {
        write(ts.iterator());
    }

    public void write(Iterator<? extends T> iterator) throws MIIOException {
        while(iterator.hasNext()){
           write(iterator.next());
        }
    }

    public void flush() throws MIIOException {
         // nothing to do
    }

    public void close() throws MIIOException{
        if (isInitialised){
            this.intactService = null;
            isInitialised = false;
        }
    }

    public void reset() throws MIIOException{
        if (isInitialised){
            this.intactService = null;
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
