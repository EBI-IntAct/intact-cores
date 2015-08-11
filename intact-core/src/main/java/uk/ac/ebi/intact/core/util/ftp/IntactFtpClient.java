/**
 * Copyright 2008 The European Bioinformatics Institute, and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.intact.core.util.ftp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import uk.ac.ebi.intact.commons.util.net.UrlFilter;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides access to the IntAct public FTP and the data it contains.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 1.8.6
 */
public class IntactFtpClient {

    private static final Log log = LogFactory.getLog( IntactFtpClient.class );

    public static final String SLASH = "/";
    public static final String HOST = "ftp.ebi.ac.uk";
    public static final String INTACT_FOLDER = "pub/databases/intact";

    private final String CURRENT_RELEASE = "current";

    public static final String PSI25_DIR = "psi25";
    public static final String PSI10_DIR = "psi25";
    public static final String MITAB_DIR = "psimitab";

    public static final String SPECIES = "species";
    public static final String PUBLICATIONS = "pmid";

    /**
     * FTP client used internaly to carry out network operations.
     */
    private FTPClient ftpClient;

    /**
     * Last directory in which we have read data.
     */
    public static String folder;

    public IntactFtpClient() {
        ftpClient = new FTPClient();
    }

    /////////////////////////
    // Getters

    protected String getFolder() {
        return folder;
    }

    protected FTPClient getFtpClient() {
        return ftpClient;
    }

    protected String getHost() {
        return HOST;
    }

    ///////////////////////////
    // FTP methods

    public void connect() throws IOException {
        if ( log.isDebugEnabled() ) log.debug( "Connecting to: " + HOST );

        ftpClient.connect( HOST );

        if ( log.isDebugEnabled() ) log.debug( "\tReply: " + ftpClient.getReplyString() );
        if ( log.isDebugEnabled() ) log.debug( "Login as anonymous" );

        ftpClient.login( "anonymous", "" );

        if ( log.isDebugEnabled() ) log.debug( "\tReply: " + ftpClient.getReplyString() );

        folder = INTACT_FOLDER;
    }

    public void disconnect() throws IOException {
        ftpClient.disconnect();
    }

    protected void checkConnected() {
        if ( !isConnected() ) {
            throw new IllegalStateException( "FtpClient is not connected to the FTP HOST. Call IntactFtpClient.connect() first" );
        }
    }

    public boolean isConnected() {
        return ftpClient.isConnected();
    }

    ////////////////////////////////
    // FTP site navigation

    // PSI-MI XML 2.5

    public List<IntactFtpFile> getPsimiXml25Publications( UrlFilter filter ) throws IOException {
        folder = INTACT_FOLDER + SLASH + CURRENT_RELEASE + SLASH + PSI25_DIR + SLASH + PUBLICATIONS;
        return listFiles( folder, filter );
    }

    public List<IntactFtpFile> getPsimiXml25Publications( int year, UrlFilter filter ) throws IOException {
        folder = INTACT_FOLDER + SLASH + CURRENT_RELEASE + SLASH + PSI25_DIR + SLASH + PUBLICATIONS + SLASH + year;
        return listFiles( folder, filter );
    }

    public List<IntactFtpFile> getPsimiXml25Publications() throws IOException {
        return getPsimiXml25Publications( null );
    }

    public List<IntactFtpFile> getPsimiXml25Publications( int year ) throws IOException {
        return getPsimiXml25Publications( year, null );
    }

    // PSI-MI XML 1.0

    public List<IntactFtpFile> getPsimiXml10Publications( UrlFilter filter ) throws IOException {
        folder = INTACT_FOLDER + SLASH + CURRENT_RELEASE + SLASH + PSI10_DIR + SLASH + PUBLICATIONS;
        return listFiles( folder, filter );
    }

    public List<IntactFtpFile> getPsimiXml10Publications( int year, UrlFilter filter ) throws IOException {
        folder = INTACT_FOLDER + SLASH + CURRENT_RELEASE + SLASH + PSI10_DIR + SLASH + PUBLICATIONS + SLASH + year;
        return listFiles( folder, filter );
    }

    public List<IntactFtpFile> getPsimiXml10Publications() throws IOException {
        return getPsimiXml10Publications( null );
    }

    public List<IntactFtpFile> getPsimiXml10Publications( int year ) throws IOException {
       return getPsimiXml10Publications( year, null );
    }

    // PSI-MI TAB

    public List<IntactFtpFile> getPsimiTabPublications( UrlFilter filter ) throws IOException {
        folder = INTACT_FOLDER + SLASH + CURRENT_RELEASE + SLASH + MITAB_DIR + SLASH + PUBLICATIONS;
        return listFiles( folder );
    }

    public List<IntactFtpFile> getPsimiTabPublications( int year, UrlFilter filter ) throws IOException {
        folder = INTACT_FOLDER + SLASH + CURRENT_RELEASE + SLASH + MITAB_DIR + SLASH + PUBLICATIONS + SLASH + year;
        return listFiles( folder, filter );
    }

    public List<IntactFtpFile> getPsimiTabPublications() throws IOException {
        return getPsimiTabPublications( null );
    }

    public List<IntactFtpFile> getPsimiTabPublications( int year ) throws IOException {
        return getPsimiTabPublications( year, null );
    }

    /////////////////////////
    // private methods

    private List<IntactFtpFile> listFiles( String folder, UrlFilter filter ) throws IOException {

        checkConnected();

        FTPFile[] baseFiles = ftpClient.listFiles( folder );

        List<IntactFtpFile> allFiles = new ArrayList<IntactFtpFile>();

        for ( FTPFile file : baseFiles ) {
            if ( file.isDirectory() ) {
                if ( log.isDebugEnabled() )
                    log.debug( "Recursive listing of " + "ftp://" + HOST + SLASH + folder + SLASH + file.getName() );
                allFiles.addAll( listFiles( folder + SLASH + file.getName(), filter ) );
            } else {
                final URL url = new URL( "ftp://" + HOST + SLASH + folder + SLASH + file.getName() );
                if ( filter == null || filter.accept( url ) ) {
                    allFiles.add( new IntactFtpFile( file, url ) );
                }
            }
        }

        return allFiles;
    }

    private List<IntactFtpFile> listFiles( String folder ) throws IOException {
        return listFiles( folder, null );
    }
}
