/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.webapp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.context.IntactConfigurator;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.context.IntactSession;
import uk.ac.ebi.intact.context.impl.WebappSession;
import uk.ac.ebi.intact.business.IntactTransactionException;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Injects the HTTP session into the IntactContext - does not open/close transactions
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24-Apr-2006</pre>
 */
public class JpaIntactSessionRequestFilter implements Filter {

    private static final Log log = LogFactory.getLog( IntactSessionRequestFilter.class );

    private FilterConfig myFilterConfig;

    private static final String FILTERED_PARAM_NAME = "uk.ac.ebi.intact.filter.EXCLUDED_EXTENSIONS";
    private static final String MANUAL_TRANSACTION_PARAM_NAME = "uk.ac.ebi.intact.filter.MANUAL_TRANSACTION";

    private static final String[] DEFAULT_EXCLUDED_EXTENSIONS = new String[]{".js", "logout"};

    private List<String> excludedExtensions;
    private boolean manualTransaction;

    public void init( FilterConfig filterConfig ) throws ServletException {
        log.debug( "Initializing filter..." );

        myFilterConfig = filterConfig;

        excludedExtensions = new ArrayList<String>();

        for ( String defaultNotFilterExt : DEFAULT_EXCLUDED_EXTENSIONS ) {
            excludedExtensions.add( defaultNotFilterExt );
        }

        String filteredParamValue = filterConfig.getInitParameter( FILTERED_PARAM_NAME );

        if ( filteredParamValue != null ) {
            String[] fexts = filteredParamValue.split( "," );

            for ( String fext : fexts ) {
                fext = fext.trim();

                if ( fext.startsWith( "*" ) ) {
                    fext = fext.substring( 1, fext.length() );
                }

                excludedExtensions.add( fext );
            }
        }

        String manualTransactionParamValue = filterConfig.getInitParameter(MANUAL_TRANSACTION_PARAM_NAME);

        if (manualTransactionParamValue != null) {
            manualTransaction = Boolean.parseBoolean(manualTransactionParamValue);
        }

        log.debug( "Will not create IntactContexts for requests URL ending with: " + excludedExtensions );
    }

    public void doFilter( ServletRequest request,
                          ServletResponse response,
                          FilterChain chain
    )
            throws IOException, ServletException {

        HttpServletRequest req = ( HttpServletRequest ) request;
        HttpSession session = req.getSession();

        // This is to prevent the IntactSessionRequestFilter from filtering all urls as for exemple it's not worth
        // in our case to open and close an hibernate session if the url end in logout, as it means that the user
        // is just asking to logout the application.
        String requestUrl = req.getRequestURL().toString();
        log.debug( "Request send is : " + requestUrl );
        // if the the url end matches with a filtered extensions do not start IntactContext
        for ( String ext : excludedExtensions ) {
            if ( requestUrl.toLowerCase().endsWith( ext.toLowerCase() ) ) {
                log.debug( "Context not created for (excluded): " + requestUrl );
                chain.doFilter( request, response );
                return;
            }
        }


        if (log.isDebugEnabled())
        {
            log.debug( "Creating JPA IntactContext, for request url: " + req.getRequestURL() + " ; session id: "+session.getId() +" ; thread: "+Thread.currentThread().getName());
        }
        IntactSession intactSession = new WebappSession( session.getServletContext(), session, req );
        IntactContext context = IntactConfigurator.createIntactContext( intactSession );

        assert IntactContext.currentInstanceExists();

        if (!manualTransaction) {
            context.getDataContext().beginTransaction();
        }
        chain.doFilter( request, response );

        if (!manualTransaction) {
            try {
                context.getDataContext().commitTransaction();
            }
            catch (IntactTransactionException e) {
                throw new ServletException(e);
            }
        }
    }

    public void destroy() {
    }


}