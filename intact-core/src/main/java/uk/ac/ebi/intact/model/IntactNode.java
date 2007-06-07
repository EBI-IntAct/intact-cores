/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.  
All rights reserved. Please see the file LICENSE 
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import java.sql.Timestamp;


/**
 * TODO comments
 *
 * @author hhe
 * @version $Id$
 */
public class IntactNode extends BasicObjectImpl {

    /**
     * ftp attributes: the address,directory, login and password
     * to access to the directory where xml dump files are available.
     * Format: One string
     */

    /**
     * TODO comments
     */
    private String ftpAddress;

    /**
     * TODO comments
     */
    private String ftpDirectory;

    /**
     * TODO comments
     */
    private String ftpLogin;

    /**
     * TODO comments
     */
    private String ftpPassword;

    /**
     * TODO comments
     */
    private int lastCheckId;

    /**
     * TODO comments
     */
    private int lastProvidedId;

    /**
     * TODO comments
     */
    private Timestamp lastProvidedDate = new Timestamp( 0 );

    /**
     * TODO comments
     */
    private int rejected = 0;

    /**
     * TODO comments
     */
    private String ownerPrefix;

    ///////////////////////////////////////
    //access methods for attributes


    public String getFtpAddress() {
        return ftpAddress;
    }

    public void setFtpAddress( String ftpAddress ) {
        this.ftpAddress = ftpAddress;
    }

    public String getFtpDirectory() {
        return ftpDirectory;
    }

    public void setFtpDirectory( String ftpDirectory ) {
        this.ftpDirectory = ftpDirectory;
    }

    public String getFtpLogin() {
        return ftpLogin;
    }

    public void setFtpLogin( String ftpLogin ) {
        this.ftpLogin = ftpLogin;
    }

    public String getFtpPassword() {
        return ftpPassword;
    }

    public void setFtpPassword( String ftpPassword ) {
        this.ftpPassword = ftpPassword;
    }

    public int getLastCheckId() {
        return lastCheckId;
    }

    public void setLastCheckId( int lastCheckId ) {
        this.lastCheckId = lastCheckId;
    }

    public int getLastProvidedId() {
        return lastProvidedId;
    }

    public void setLastProvidedId( int lastProvidedId ) {
        this.lastProvidedId = lastProvidedId;
    }

    public Timestamp getLastProvidedDate() {
        return lastProvidedDate;

    }

    public void setLastProvidedDate( Timestamp lastProvidedDate ) {
        this.lastProvidedDate = lastProvidedDate;
    }

    public String getOwnerPrefix() {
        return ownerPrefix;
    }

    public void setOwnerPrefix( String ownerPrefix ) {
        this.ownerPrefix = ownerPrefix;
    }

    public int getRejected() {
        return rejected;
    }

    public void setRejected( int rejected ) {
        this.rejected = rejected;
    }
}
