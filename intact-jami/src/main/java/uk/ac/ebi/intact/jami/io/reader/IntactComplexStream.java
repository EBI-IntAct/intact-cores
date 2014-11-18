package uk.ac.ebi.intact.jami.io.reader;

import psidev.psi.mi.jami.datasource.ComplexStream;
import psidev.psi.mi.jami.exception.MIIOException;
import psidev.psi.mi.jami.model.Complex;
import uk.ac.ebi.intact.jami.ApplicationContextProvider;
import uk.ac.ebi.intact.jami.service.ComplexService;

import java.util.Iterator;

/**
 * Intact stream for biological complexes
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24/02/14</pre>
 */

public class IntactComplexStream extends AbstractIntactStream<Complex> implements ComplexStream{

    public IntactComplexStream() {
        super();
    }

    public IntactComplexStream(String springFile, String intactServiceName) {
        super(springFile, intactServiceName);
    }

    @Override
    protected boolean isSpringContextInitialised() {
        return ApplicationContextProvider.getBean(ComplexService.class) != null;
    }

    @Override
    protected void initialiseDefaultIntactService() {
        setIntactService((ComplexService)ApplicationContextProvider.getBean("complexService"));
    }

    @Override
    public Iterator<Complex> getInteractorsIterator() throws MIIOException {
        return getInteractionsIterator();
    }
}
