package uk.ac.ebi.intact.webapp;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by IntelliJ IDEA.
 * User: CatherineLeroy
 * Date: 08-Jan-2007
 * Time: 11:19:14
 * To change this template use File | Settings | File Templates.
 */
public class StringResponseWrapper extends HttpServletResponseWrapper {

    private PrintWriter writer;
    StringWriter stringWriter;

    /**
     * Constructs a response adaptor wrapping the given response.
     *
     * @throws IllegalArgumentException if the response is null
     */
    public StringResponseWrapper( HttpServletResponse response ) {

        super( response );

        stringWriter = new StringWriter( 4096 );
        writer = new PrintWriter( stringWriter );

    }


    public PrintWriter getWriter() throws IOException {

        return writer;    //To change body of overridden methods use File | Settings | File Templates.
    }

    public String toString() {
        return stringWriter.toString();
    }
}
