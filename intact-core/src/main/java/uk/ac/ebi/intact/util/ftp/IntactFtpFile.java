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
package uk.ac.ebi.intact.util.ftp;

import org.apache.commons.net.ftp.FTPFile;

import java.net.URL;
import java.util.Calendar;

/**
 * A read-only file on the IntAct FTP site.
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 1.8.6
 */
public class IntactFtpFile {

    private FTPFile ftpFile;
    
    private URL url;

    public IntactFtpFile( FTPFile ftpFile, URL url ) {
        if ( ftpFile == null ) {
            throw new IllegalArgumentException( "You must give a non null FTP file" );
        }
        this.ftpFile = ftpFile;

        if ( url == null ) {
            throw new IllegalArgumentException( "You must give a non null url" );
        }
        this.url = url;
    }

    public URL getUrl() {
        return url;
    }

    /////////////////////////
    // Delegation of FTPFile

    public String getRawListing() {
        return ftpFile.getRawListing();
    }

    public boolean isDirectory() {
        return ftpFile.isDirectory();
    }

    public boolean isFile() {
        return ftpFile.isFile();
    }

    public boolean isSymbolicLink() {
        return ftpFile.isSymbolicLink();
    }

    public boolean isUnknown() {
        return ftpFile.isUnknown();
    }

    public int getType() {
        return ftpFile.getType();
    }

    public String getName() {
        return ftpFile.getName();
    }

    public long getSize() {
        return ftpFile.getSize();
    }

    public int getHardLinkCount() {
        return ftpFile.getHardLinkCount();
    }

    public String getGroup() {
        return ftpFile.getGroup();
    }

    public String getUser() {
        return ftpFile.getUser();
    }

    public String getLink() {
        return ftpFile.getLink();
    }

    public Calendar getTimestamp() {
        return ftpFile.getTimestamp();
    }

    public boolean hasPermission( int access, int permission ) {
        return ftpFile.hasPermission( access, permission );
    }
}
