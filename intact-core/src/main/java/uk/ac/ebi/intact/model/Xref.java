/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import uk.ac.ebi.intact.model.util.CvObjectUtils;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a crossreference to another database.
 *
 * @author hhe
 * @version $Id$
 */
@Entity
@Inheritance( strategy = InheritanceType.TABLE_PER_CLASS )
public abstract class Xref extends BasicObjectImpl {

    private static final Log log = LogFactory.getLog( Xref.class );

    ///////////////////////////////////////
    // Constamt

    public static final int MAX_ID_LEN = 50;
    public static final int MAX_DB_RELEASE_LEN = 10;

    ///////////////////////////////////////
    //attributes

    // TODO: find out if these two fields are used or not.
//    private String qualifierAc;
//    protected String databaseAc;

    /**
     * Ac of the object which holds that Xref
     */
    private String parentAc;

    private AnnotatedObject parent;

    //attributes used for mapping BasicObjects - project synchron
    // TODO: should be move out of the model.
    protected String cvXrefQualifierAc;
    protected String cvDatabaseAc;


    /**
     * Primary identifier of the database referred to.
     */
    private String primaryId;

    /**
     * Secondary identifier of the database. This will usually be
     * a meaningful name, for example a domain name.
     */
    private String secondaryId;

    /**
     * The release number of the external database from which the object
     * has been updated.
     */
    private String dbRelease;

    ///////////////////////////////////////
    // associations

    /**
     * TODO comments
     */
    private CvXrefQualifier cvXrefQualifier;

    /**
     * TODO comments
     */
    private CvDatabase cvDatabase;

    public Xref() {
        //super call sets creation time data
        super();
    }


    /**
     * Creates a valid Xref instance. Items which must be defined are:
     * <ul>
     * <li>an owner (Institution)</li>
     * <li>database details (controlled vocabulary instance)</li>
     * <li>a Primary ID</li>
     *
     * @param aDatabase        Controlled vocabulary instance defining the database details (non-null)
     * @param aPrimaryId       primary identifier for the cross-reference (non-null), this should be 30 characters maximum
     *                         if it's more it will be truncated. if not done, Oracle would throw an error.
     * @param aSecondaryId     secondary identifier (eg a domain name), this should be 30 characters maximum
     *                         if it's more it will be truncated. if not done, Oracle would throw an error.
     * @param aDatabaseRelease database version
     * @param aCvXrefQualifier controlled vocabulary for any qualifiers (may be null)
     *
     * @throws NullPointerException thrown if any mandatory parameters are not specified
     */
    public Xref( CvDatabase aDatabase,
                 String aPrimaryId,
                 String aSecondaryId,
                 String aDatabaseRelease,
                 CvXrefQualifier aCvXrefQualifier
    ) {

        //super call sets creation time data
        this( aDatabase, aPrimaryId, aCvXrefQualifier );

        this.secondaryId = fixId( aSecondaryId );
        this.dbRelease = aDatabaseRelease;
    }

    @Deprecated
    public Xref( Institution anOwner,
                 CvDatabase aDatabase,
                 String aPrimaryId,
                 String aSecondaryId,
                 String aDatabaseRelease,
                 CvXrefQualifier aCvXrefQualifier
    ) {

        //super call sets creation time data
        this( anOwner, aDatabase, aPrimaryId, aCvXrefQualifier );

        this.secondaryId = fixId( aSecondaryId );
        this.dbRelease = aDatabaseRelease;
    }

    /**
     * Creates a valid Xref instance. Items which must be defined are: <ul> <li>an owner (Institution)</li> <li>database
     * details (controlled vocabulary instance)</li> <li>a Primary ID</li>
     *
     * @param aDatabase        Controlled vocabulary instance defining the database details (non-null)
     * @param aPrimaryId       primary identifier for the cross-reference (non-null), this should be 30 characters
     *                         maximum if it's more it will be truncated. if not done, Oracle would throw an error.
     * @param aCvXrefQualifier controlled vocabulary for any qualifiers (may be null)
     *
     * @throws NullPointerException thrown if any mandatory parameters are not specified
     */
    public Xref( CvDatabase aDatabase,
                 String aPrimaryId,
                 CvXrefQualifier aCvXrefQualifier
    ) {

        //super call sets creation time data
        super();

        if ( aPrimaryId == null ) {
            throw new NullPointerException( "valid Xref must have a primary ID!" );
        }

        aPrimaryId = fixId( aPrimaryId );

        if ( aPrimaryId.length() == 0 ) {
            throw new IllegalArgumentException( "Must define a non empty primaryId for an Xref." );
        }

        this.primaryId = aPrimaryId;
        this.cvDatabase = aDatabase;
        this.cvXrefQualifier = aCvXrefQualifier;
    }

    @Deprecated
    public Xref( Institution anOwner, CvDatabase aDatabase,
                 String aPrimaryId,
                 CvXrefQualifier aCvXrefQualifier
    ) {

        this(aDatabase, aPrimaryId, aCvXrefQualifier);
    }

    ///////////////////////////////////////
    //access methods for attributes

    public String getPrimaryId() {
        return primaryId;
    }

    public void setPrimaryId( String aPrimaryId ) {

        if ( aPrimaryId == null ) {
            throw new NullPointerException( "valid Xref must have a primary ID!" );
        }

        if ( "".equals( aPrimaryId ) ) {
            throw new IllegalArgumentException( "Must define a non empty primaryId for an Xref." );
        }

        this.primaryId = aPrimaryId;
    }

    @Size( max = MAX_ID_LEN )
    public String getSecondaryId() {
        return secondaryId;
    }

    public void setSecondaryId( String aSecondaryId ) {
        this.secondaryId = aSecondaryId;
    }

    public String getDbRelease() {
        return dbRelease;
    }


    public void setDbRelease( String aDbRelease ) {

        if ( aDbRelease != null && aDbRelease.length() >= MAX_DB_RELEASE_LEN ) {
            aDbRelease = aDbRelease.substring( 0, MAX_DB_RELEASE_LEN );
        }

        this.dbRelease = aDbRelease;
    }

    @Transient
    public String getParentAc() {
        if ( parent != null ) {
            parentAc = parent.getAc();
        }
        return parentAc;
    }

    public void setParentAc( String parentAc ) {
        this.parentAc = parentAc;
    }

    @Transient
    public AnnotatedObject getParent() {
        return parent;
    }

    public void setParent( AnnotatedObject parent ) {
        this.parent = parent;
    }

    ///////////////////////////////////////
    // access methods for associations
    @ManyToOne
    @JoinColumn( name = "qualifier_ac" )
    public CvXrefQualifier getCvXrefQualifier() {
        return cvXrefQualifier;
    }

    public void setCvXrefQualifier( CvXrefQualifier cvXrefQualifier ) {
        this.cvXrefQualifier = cvXrefQualifier;
    }

    @ManyToOne
    @JoinColumn( name = "database_ac" )
    public CvDatabase getCvDatabase() {
        return cvDatabase;
    }

    public void setCvDatabase( CvDatabase cvDatabase ) {
        if ( cvDatabase == null ) throw new NullPointerException( "valid Xref must have non-null database details!" );
        this.cvDatabase = cvDatabase;
    }

    ///////////////////////////////////////
    // Utility method

    /**
     * checks that the given primaryId matched (if defined) the regular expression on the CvDatabase.
     * <br>
     * If no validation regexp is found, the primaryId is declared valid.
     *
     * @return true if the primaryId is valid or no regexp could be found in the CvDatabase annotations. Otherwise false.
     */
    public boolean hasValidPrimaryId() {

        boolean valid = true;
        boolean stop = false;

        // until all annotation are checked or we find the validation rule
        for ( Iterator iterator = cvDatabase.getAnnotations().iterator(); iterator.hasNext() && false == stop; ) {
            Annotation annotation = ( Annotation ) iterator.next();

            if ( CvTopic.XREF_VALIDATION_REGEXP.equals( annotation.getCvTopic().getShortLabel() ) ) {
                String regexp = annotation.getAnnotationText();

                try {
                    // TODO escape special characters !!
                    Pattern pattern = Pattern.compile( regexp );

                    // validate the primaryId against that regular expression
                    Matcher matcher = pattern.matcher( primaryId );
                    if ( false == matcher.matches() ) {
                        valid = false;
                    }

                } catch ( Exception e ) {
                    // if the RegExp engine thrown an Exception, that may happen if the format is wrong.
                    // we just display it for debugging sake, but the Xref is declared valid.
                    e.printStackTrace();
                }

                // whatever the outcome of the check is, we know that there is only one validation rule, so we can stop.
                stop = true;
            }
        }

        return valid;
    }


    ///////////////////////////////////////
    // instance methods
    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( !( o instanceof Xref ) ) return false;

        if (!super.equals(o)) {
            return false;
        }

        final Xref xref = ( Xref ) o;

        if (!getClass().equals(xref.getClass())) return false;

        if ( !StringUtils.equals(getPrimaryId(), xref.getPrimaryId() )) return false;

        if (!(cvDatabase == null && xref.getCvDatabase() == null)) {
            if (cvDatabase != null && !CvObjectUtils.areEqual(cvDatabase, xref.getCvDatabase())) {
                return false;
            } else if (cvDatabase == null) {
                return false;
            }
        }

        if (!(cvXrefQualifier == null && xref.getCvXrefQualifier() == null)) {
            if (cvXrefQualifier != null && !CvObjectUtils.areEqual(cvXrefQualifier, xref.getCvXrefQualifier())) {
                return false;
            } else if (cvXrefQualifier == null) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        if (ac != null) return super.hashCode();

        int result = 29;
        result = 29 * result + primaryId.hashCode();
        result = 29 * result + cvDatabase.hashCode();
        result = 29 * result + ( cvXrefQualifier != null ? cvXrefQualifier.hashCode() : 0 );
        return result;
    }

    @Override
    public String toString() {

        final StringBuffer sb = new StringBuffer( 150 );
        sb.append( "Xref" );
        sb.append( "{cvDatabase=" ).append( cvDatabase.getShortLabel() );
        sb.append( ", cvXrefQualifier=" ).append( ( cvXrefQualifier == null ? "-" : cvXrefQualifier.getShortLabel() ) );
        sb.append( ", primaryId='" ).append( primaryId ).append( '\'' );
        sb.append( ", secondaryId='" ).append( ( secondaryId == null ? "-" : secondaryId ) ).append( '\'' );
        sb.append( ", dbRelease='" ).append( ( dbRelease == null ? "-" : dbRelease ) ).append( '\'' );
        sb.append( '}' );
        return sb.toString();
    }

//    public String toString() {
//
//        return " Xref: " + getAc()
//               + "; Owner: " + getOwner().getShortLabel()
//               + "; DB: " + cvDatabase.getShortLabel()
//               + "; PrimaryId: " + primaryId;
//    }

    /**
     * Returns a cloned version of the current object.
     *
     * @return a cloned version of the current Range. The Cv database and
     *         xref qualifier are not cloned (shared). The parent AC is set to null.
     *
     * @throws CloneNotSupportedException for errors in cloning this object.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        Xref copy = ( Xref ) super.clone();
        // Reset the parent ac.
        copy.parentAc = null;
        copy.parent = null;
        return copy;
    }

    private String fixId( String id ) {
        if ( id != null ) {
            // delete leading and trailing spaces.
            id = id.trim();

            if ( id.length() > MAX_ID_LEN ) {
                id = id.substring( 0, MAX_ID_LEN );
            }
        }
        return id;
    }

} // end Xref




