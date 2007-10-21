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
 * When an HTTP request has to be handled, a new Session and database transaction will begin.
 * Right before the response is send to the client, and after all the work has been done,
 * the transaction will be committed, and the Session will be closed.
 *
 * If there's an error at commit time 3 things can happen :
 *
 *         1. The COMMIT_ERRROR_MESSAGE_PARAM request attribute, is null. The intactSessionRequestFilter, will throw
 *            a ServletException with the message "Exception commiting trying to commit...".
 *         2. The COMMIT_ERROR_MESSAGE_PARAM request attribute is not null but not equal to IntactSessionRequestFilter.COULD_NOT_LOGIN.
 *            The IntactSessionRequestFilter will throw a ServletException with as first part or the message, the value
 *            of the attribute COMMIT_ERRROR_MESSAGE_PARAM.
 *         3. The COMMIT_ERROR_MESSAGE_PARAM is equal to IntactSessionRequestFilter.COULD_NOT_LOGIN
 *            The IntactSessionRequestFilter, will assume that your application have dealt with the problem earlier
 *            (ex: you've build a page with a nice error message "login or password was wrong" and will just display the
 *            Response your application has built.
 *            So if the login in your aplication fail, you should set the COMMIT_ERRROR_MESSAGE_PARAM attribute
 *            of the request to IntactSessionRequestFilter.COULD_NOT_LOGIN if you want your own built response to be
 *            displayed.
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>24-Apr-2006</pre>
 */
public class IntactSessionRequestFilter implements Filter {

    private FilterConfig myFilterConfig;

    private static final Log log = LogFactory.getLog( IntactSessionRequestFilter.class );

    public static final String  COULD_NOT_LOGIN = "Could not login";

    private static final String FILTERED_PARAM_NAME = "uk.ac.ebi.intact.filter.EXCLUDED_EXTENSIONS";

    private static final String[] DEFAULT_EXCLUDED_EXTENSIONS = new String[]{".js", "logout"};

    private static final String COMMIT_ERRROR_MESSAGE_PARAM = IntactSessionRequestFilter.class+".COMMIT_ERROR_MSG";

    private List<String> excludedExtensions;

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
            log.debug( "Creating IntactContext, for request url: " + requestUrl + " ; session id: "+session.getId() +" ; thread: "+Thread.currentThread().getName());
        }
        IntactSession intactSession = new WebappSession( session.getServletContext(), session, req );
        IntactContext context = IntactConfigurator.createIntactContext( intactSession );
        log.debug("Beginning the transaction");
        IntactContext.getCurrentInstance().getDataContext().beginTransaction();

        // We need to write the httpResponse firt in the responseWrapper. Other wise when we get back the response and
        // do the commit, if the fails, it's impossible for us to display an error message as the httpResponse is already
        // commited and sent.
        // Therefore, we get the response in the responseWrapper, if the commit does not fail we write the responseWrapper
        // to the out PrintWriter which displays the message on the client screen :
        //          out.write(responseWrapper.toString());
        // If the commit fails, we rollback, in a finally we close the session (hibernateSession.close()) and we throw
        // a new ServletException("Exception commiting, rollback sucessfull" + e);
        // In a web application you can configure your web.xml so that when those kind of exception occured a specific
        // jsp is displayed :
        //          <error-page>
        //              <exception-type>javax.servlet.ServletException</exception-type>
        //              <location>/pages/errorCommiting.jsp</location>
        //          </error-page>

        PrintWriter out = response.getWriter();
        StringResponseWrapper responseWrapper = new StringResponseWrapper( ( HttpServletResponse ) response );

        try {
            // Call the next filter (continue request processing)
            chain.doFilter( request, responseWrapper );
        }
        catch ( IOException e ) {
            // use the lograther than standard output or some errors could be raised
            log.error( "intact session: ", e );
        }
        catch ( ServletException e ) {
            log.error( "intact session: ", e );
        }
        finally {
            log.debug( "Committing active transactions" );
            try {
                context.getDataContext().commitTransaction();
                out.write( responseWrapper.toString() );
            }
            catch ( Exception e ) {
                log.error( "Exception commiting trying to commit : ", e );
                try {
                    context.getDataContext().getDaoFactory().getCurrentTransaction().rollback();
                } catch ( Exception ie ) {
                    log.error( "Couldn't rollback." + ie );
                    throw new ServletException( "Couldn't rollback." + ie );
                }
                if (getCommitErrorMessage(req) != null)
                {
                    if(COULD_NOT_LOGIN.equals(getCommitErrorMessage(req))){
                        out.write( responseWrapper.toString() );
                    }else{
                        log.error( getCommitErrorMessage(req));
                        throw new ServletException( getCommitErrorMessage(req) + e );
                    }
                }
                else
                {
                   log.error("Exception commiting, rollback sucessfull" + e ); 
                   throw new ServletException( "Exception commiting, rollback sucessfull" + e );
                }

            }

        }
        out.close();

    }

    public void init( FilterConfig filterConfig ) throws ServletException {
        log.debug( "Initializing filter..." );

        myFilterConfig = filterConfig;

        excludedExtensions = new ArrayList<String>();

        for ( String defaultNotFilterExt : DEFAULT_EXCLUDED_EXTENSIONS ) {
            excludedExtensions.add( defaultNotFilterExt );
        }

        String paramValue = filterConfig.getInitParameter( FILTERED_PARAM_NAME );

        if ( paramValue != null ) {
            String[] fexts = paramValue.split( "," );

            for ( String fext : fexts ) {
                fext = fext.trim();

                if ( fext.startsWith( "*" ) ) {
                    fext = fext.substring( 1, fext.length() );
                }

                excludedExtensions.add( fext );
            }
        }

        log.debug( "Will not create IntactContexts for requests URL ending with: " + excludedExtensions );
    }

    public void destroy() {
    }

    public static final void setCommitErrorMessage(String message, IntactSession session)
    {
        session.setRequestAttribute(COMMIT_ERRROR_MESSAGE_PARAM, message);
    }

    private static final String getCommitErrorMessage(HttpServletRequest request)
    {
        return (String) request.getAttribute(COMMIT_ERRROR_MESSAGE_PARAM);
    }
}
