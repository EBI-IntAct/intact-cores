/*
Copyright (c) 2002 The European Bioinformatics Institute, and others.
All rights reserved. Please see the file LICENSE
in the root directory of this distribution.
*/
package uk.ac.ebi.intact.model;

/**
 * Represents the contact details of a person.
 * The person may be internal, for example a curator, or external, e.g. a submitter.
 *
 * @author Henning Hermjakob
 * @version $id$
 */
public class Person extends BasicObjectImpl {

    ///////////////////////////////////////
    //attributes

    //attributes used for mapping BasicObjects - project synchron
    // TODO: should be move out of the model.
    public String institutionAc;

    /**
     * The first names of a person, as they should be shown in any output.
     */
    private String firstNames;

    /**
     * The surname.
     */
    private String surName;

    /**
     * The full, international telephone number. Represented as a String,
     * but should contain only digits and blanks, for example 0049 122349 5555.
     */
    private String telephone;

    /**
     * The full email address.
     * example: test@nowhere.com
     */
    private String email;

    ///////////////////////////////////////
    // associations

    /**
     * Describes the relationship between a person and his/her
     * institution. A person belongs to zero or one institution,
     * if he/she should have for example more than one employer, one
     * must be chosen.
     */
    private Institution institution;

    /**
     * This constructor should <b>not</b> be used as it could
     * result in objects with invalid state. It is here for object mapping
     * purposes only and if possible will be made private.
     *
     * @deprecated Use the full constructor instead
     */
    @Deprecated
    public Person() {
        super();
    }

    ///////////////////////////////////////
    //access methods for attributes

    public String getFirstNames() {
        return firstNames;
    }

    public void setFirstNames( String firstNames ) {
        this.firstNames = firstNames;
    }

    public String getSurName() {
        return surName;
    }

    public void setSurName( String surName ) {
        this.surName = surName;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone( String telephone ) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail( String email ) {
        this.email = email;
    }

    ///////////////////////////////////////
    // access methods for associations

    public Institution getInstitution() {
        return institution;
    }

    public void setInstitution( Institution institution ) {
        this.institution = institution;
    }


    // TODO needs to be refines
    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( !( o instanceof Person ) ) return false;

        final Person person = ( Person ) o;

        if ( email != null ? !email.equals( person.email ) : person.email != null ) return false;
        if ( firstNames != null ? !firstNames.equals( person.firstNames ) : person.firstNames != null ) return false;
        if ( institution != null ? !institution.equals( person.institution ) : person.institution != null )
            return false;
        if ( surName != null ? !surName.equals( person.surName ) : person.surName != null ) return false;
        if ( telephone != null ? !telephone.equals( person.telephone ) : person.telephone != null ) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        result = ( firstNames != null ? firstNames.hashCode() : 0 );
        result = 29 * result + ( surName != null ? surName.hashCode() : 0 );
        result = 29 * result + ( telephone != null ? telephone.hashCode() : 0 );
        result = 29 * result + ( email != null ? email.hashCode() : 0 );
        result = 29 * result + ( institution != null ? institution.hashCode() : 0 );
        return result;
    }

    //attributes used for mapping BasicObjects - project synchron
    // TODO: should be move out of the model.
    public String getInstitutionAc() {
        return institutionAc;
    }

    public void setInstitutionAc( String ac ) {
        this.institutionAc = ac;
    }

} // end Person




