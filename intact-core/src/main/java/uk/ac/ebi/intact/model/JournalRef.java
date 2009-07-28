/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;


/**
 * TODO Represents ...
 *
 * @author Henning Hermjakob
 * @version $Id$
 */
public class JournalRef extends Reference {

    ///////////////////////////////////////
    //attributes

    //attributes used for mapping BasicObjects - project synchron
    // TODO: should be move out of the model.
    private String cvJournalAc;

    /**
     * TODO Represents ...
     */
    private Integer pubmidId;

    /**
     * TODO Represents ...
     */
    private String firstpage;

    ///////////////////////////////////////
    // associations

    /**
     * TODO comments
     */
    private CvJournal cvJournal;

    /**
     * This constructor should <b>not</b> be used as it could
     * result in objects with invalid state. It is here for object mapping
     * purposes only and if possible will be made private.
     *
     * @deprecated Use the full constructor instead
     */
    @Deprecated
    public JournalRef() {
        super();
    }

    public JournalRef( Institution owner, String title, String authors, Integer pubmidId, String firstpage, CvJournal cvJournal ) {
        super( owner, title, authors );
        this.pubmidId = pubmidId;
        this.firstpage = firstpage;
        this.cvJournal = cvJournal;
    }
    ///////////////////////////////////////
    //access methods for attributes

    public Integer getPubmidId() {
        return pubmidId;
    }

    public void setPubmidId( Integer pubmidId ) {
        this.pubmidId = pubmidId;
    }

    public String getFirstpage() {
        return firstpage;
    }

    public void setFirstpage( String firstpage ) {
        this.firstpage = firstpage;
    }

    ///////////////////////////////////////
    // access methods for associations

    public CvJournal getCvJournal() {
        return cvJournal;
    }

    public void setCvJournal( CvJournal cvJournal ) {
        this.cvJournal = cvJournal;
    }

    //attributes used for mapping BasicObjects - project synchron
    // TODO: should be move out of the model.
    public void setCvJournalAc( String ac ) {
        this.cvJournalAc = ac;
    }

    public String getCvJournalAc() {
        return this.cvJournalAc;
    }

} // end JournalRef




