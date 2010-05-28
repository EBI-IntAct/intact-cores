/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.validator.Length;
import uk.ac.ebi.intact.model.util.CvObjectUtils;

import javax.persistence.*;


/**
 * Funtional description of an object.
 *
 * @author hhe
 * @version $Id$
 */
@Entity()
@Table( name = "ia_annotation" )
public class Annotation extends BasicObjectImpl {

    private static final Log log = LogFactory.getLog( Annotation.class );

    public static final int MAX_DESCRIPTION_LEN = 4000;

    ///////////////////////////////////////
    //attributes

    /**
     * Text describing one aspect of the annotation of
     * an object.
     */
    private String annotationText;

    ///////////////////////////////////////
    // associations

    /**
     * Type of the annotation
     */
    private CvTopic cvTopic;

    /**
     * This constructor should <b>not</b> be used as it could
     * result in objects with invalid state. It is here for object mapping
     * purposes only and if possible will be made private.
     *
     * @deprecated Use the full constructor instead
     */
    @Deprecated
    public Annotation() {
        //super call sets creation time data
        super();
    }

    /**
     * Creates a valid Annotation instance. A valid instance must have at least
     * a non-null Institution specified. A side-effect of this constructor is to
     * set the <code>created</code> and <code>updated</code> fields of the instance
     * to the current time.
     *
     * @param topic Refers to the controlled vocabulary topic this Annotation relates
     *              to. This should be non-null.
     *
     * @throws NullPointerException thrown if no Institution specified.
     */
    public Annotation( CvTopic topic ) {

        //super call sets creation time data
        super( );
        setCvTopic( topic );
    }

    /**
     * Creates a valid Annotation instance. A valid instance must have at least
     * a non-null Institution specified. A side-effect of this constructor is to
     * set the <code>created</code> and <code>updated</code> fields of the instance
     * to the current time.
     *
     * @param owner          The <code>Institution</code> which 'owns' this BioSource
     * @param topic          Refers to the controlled vocabulary topic this Annotation relates
     *                       to. This should be non-null.
     * @param annotationText the test of the annotation.
     *
     * @throws NullPointerException thrown if no Institution specified.
     */
    public Annotation( CvTopic topic, String annotationText ) {

        this( topic );

        if ( annotationText != null ) {
            this.annotationText = annotationText.trim();
        } else {
            log.warn( "AnnotationText is null when instantiating Annotation using full constructor" );
        }
    }

    @Deprecated
    public Annotation( Institution owner, CvTopic topic ) {

        //super call sets creation time data
        super( );
        setCvTopic( topic );
        setOwner(owner);
    }

    @Deprecated
    public Annotation( Institution owner, CvTopic topic, String annotationText ) {
        this(topic, annotationText);
    }

    ///////////////////////////////////////
    //access methods for attributes
    @Column( name = "description", length = MAX_DESCRIPTION_LEN )
    @Length( max = MAX_DESCRIPTION_LEN )
    public String getAnnotationText() {
        return annotationText;
    }

    public void setAnnotationText( String annotationText ) {
        this.annotationText = annotationText;
    }

    ///////////////////////////////////////
    // access methods for associations
    @ManyToOne
    @JoinColumn( name = "topic_ac" )
    @ForeignKey(name = "FK_ANNOTATION$TOPIC")
    public CvTopic getCvTopic() {
        return cvTopic;
    }

    public void setCvTopic( CvTopic cvTopic ) {

        if ( cvTopic == null ) {
            throw new NullPointerException( "valid Annotation must have an associated topic!" );
        }

        this.cvTopic = cvTopic;
    }

    /**
     * Equality for Annotations is currently based on equality for
     * <code>CvTopics</code> and annotationText (a String).
     *
     * @param o The object to check
     *
     * @return true if the parameter equals this object, false otherwise
     *
     * @see uk.ac.ebi.intact.model.CvTopic
     */
    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof Annotation ) ) {
            return false;
        }

        final Annotation annotation = ( Annotation ) o;

        if ( ac != null ) {
            return ac.equals( annotation.ac );
        }

        if (!CvObjectUtils.areEqual(cvTopic, annotation.getCvTopic()) ) {
            return false;
        }

        //get to here and cvTopics are equal (null or non-null)
        if ( annotationText != null ) {
            return annotationText.equals( annotation.annotationText );
        }

        return annotation.annotationText == null;
    }

    /**
     * This class overwrites equals. To ensure proper functioning of HashTable,
     * hashCode must be overwritten, too.
     *
     * @return hash code of the object.
     */
    @Override
    public int hashCode() {

        int code = 29;
        if ( ac != null ) {
            code = 29 * code + ac.hashCode();
        }

        if ( cvTopic != null ) {
            code = 29 * code + cvTopic.hashCode();
        }

        if ( null != annotationText ) {
            code = 29 * code + annotationText.hashCode();
        }

        return code;
    }

    @Override
    public String toString() {
        return "Annotation[type: " + ( cvTopic != null ? cvTopic.getShortLabel() : "" ) +
               ", text: " + annotationText + "]";
    }
}




