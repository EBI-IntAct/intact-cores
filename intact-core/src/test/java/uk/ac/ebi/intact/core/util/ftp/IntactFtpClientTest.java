package uk.ac.ebi.intact.core.util.ftp;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import uk.ac.ebi.intact.commons.util.net.UrlFilter;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * IntactFtpClient Tester.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 1.8.6
 */
public class IntactFtpClientTest {

    private IntactFtpClient ftpClient;

    @Before
    public void connect() throws IOException {
        ftpClient = new IntactFtpClient();
        ftpClient.connect();
    }

    @After
    public void disconnect() throws IOException {
        ftpClient.disconnect();
    }

    @Test
    public void getPsimiXml25Publications() throws Exception {
        final List<IntactFtpFile> files = ftpClient.getPsimiXml25Publications();
        Assert.assertTrue( files.size() > 0 );
    }

    @Test
    public void getPsimiXml25Publications_year() throws Exception {
        final List<IntactFtpFile> files = ftpClient.getPsimiXml25Publications( 2007 );
        Assert.assertTrue( files.size() > 0 );
    }

    @Test
    public void getPsimiXml25Publications_year_filter() throws Exception {
        final List<IntactFtpFile> files = ftpClient.getPsimiXml25Publications( 2007, new UrlFilter() {
            public boolean accept( URL url ) throws IOException {
                return url.getFile().endsWith( ".zip" );
            }
        } );
        Assert.assertTrue( files.size() > 0 );
    }

    @Test
    public void getPsimiXml10Publications() throws Exception {
        final List<IntactFtpFile> files = ftpClient.getPsimiXml10Publications();
        Assert.assertTrue( files.size() > 0 );
    }

    @Test
    public void getPsimiXml10Publications_year() throws Exception {
        final List<IntactFtpFile> files = ftpClient.getPsimiXml10Publications( 2007 );
        Assert.assertTrue( files.size() > 0 );
    }

    @Test
    public void getPsimiXml10Publications_year_filter() throws Exception {
        final List<IntactFtpFile> files = ftpClient.getPsimiXml10Publications( 2007, new UrlFilter() {
            public boolean accept( URL url ) throws IOException {
                return url.getFile().endsWith( ".xml" );
            }
        } );
        Assert.assertTrue( files.size() > 0 );
    }

    @Test
    public void getPsimiTabPublications() throws Exception {
        final List<IntactFtpFile> files = ftpClient.getPsimiTabPublications();
        Assert.assertTrue( files.size() > 0 );
    }

    @Test
    public void getPsimiTabPublications_year() throws Exception {
        final List<IntactFtpFile> files = ftpClient.getPsimiTabPublications( 2007 );
        Assert.assertTrue( files.size() > 0 );
    }

    @Test
    public void getPsimiTabPublications_year_filter() throws Exception {

        final List<IntactFtpFile> files = ftpClient.getPsimiTabPublications( 2007, new UrlFilter() {
            public boolean accept( URL url ) throws IOException {
                return url.getFile().endsWith( ".txt" );
            }
        } );
        Assert.assertTrue( files.size() > 0 );
    }
}
