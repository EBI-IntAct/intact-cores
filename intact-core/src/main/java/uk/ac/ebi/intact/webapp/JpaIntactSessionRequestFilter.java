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

    public void init(FilterConfig filterConfig) throws ServletException {

    }

    public void doFilter( ServletRequest request,
                          ServletResponse response,
                          FilterChain chain
    )
            throws IOException, ServletException {

        HttpServletRequest req = ( HttpServletRequest ) request;
        HttpSession session = req.getSession();

        if (log.isDebugEnabled())
        {
            log.debug( "Creating JPA IntactContext, for request url: " + req.getRequestURL() + " ; session id: "+session.getId() +" ; thread: "+Thread.currentThread().getName());
        }
        IntactSession intactSession = new WebappSession( session.getServletContext(), session, req );
        IntactContext context = IntactConfigurator.createIntactContext( intactSession );

        chain.doFilter( request, response );
    }

    public void destroy() {
    }


}