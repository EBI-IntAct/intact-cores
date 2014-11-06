package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.Hibernate;
import org.hibernate.annotations.*;
import psidev.psi.mi.jami.model.Alias;
import psidev.psi.mi.jami.model.Annotation;
import psidev.psi.mi.jami.model.OntologyTerm;
import psidev.psi.mi.jami.model.Xref;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Intact implementation of Cv term
 *
 * NOTE: getIdentifiers and getXrefs are not persistent methods annotated with hibernate annotations. All the xrefs present in identifiers
 * and xrefs are persisted in the same table for backward compatibility with intact-core. So the persistent xrefs are available with the getDbXrefs method.
 * For HQL queries, the method getDbXrefs should be used because is annotated with hibernate annotations.
 * However, getDbXrefs should not be used directly to add/remove xrefs because it could mess up with the state of the object. Only the synchronizers
 * can use it this way before persistence.
 * However, it is not recommended to use this method to directly add/remove xrefs as it may mess up with the state of the object.
 * NOTE: The property objClass is deprecated but is kept for backward compatibility with intact-core. Once intact-core is removed, we can remove the objclass
 * property and merge all cvs that were duplicated. The only remaining unique constraint would be the shortlabel.
 * NOTE: The identifier property is deprecated but is automatically set for backward compatibility with intact-core. Once intact-core is removed,
 * we should remove the identifier property and the matching column in the database.
 * NOTE: the parents of a CvTerm are responsible for the persistent relationship in the database.
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>07/01/14</pre>
 */
@Entity
@Table( name = "ia_controlledvocab",
        uniqueConstraints = {@UniqueConstraint(columnNames={"objclass", "shortlabel"})})
@Cacheable
public class IntactCvTerm extends AbstractIntactCvTerm implements OntologyTerm{

    /**
     * PSI-MI Identifier for this object, which is a de-normalization of the
     * value contained in the 'identity' xref
     */
    /**
    * @deprecated the identifier is kept for backward compatibility with intact-core. Use MIIdentifier, MODIdentifier or PARIdentifier instead
     */
    private String identifier;
    /**
     * @deprecated the objclass is only kept for backward compatibility with intact-core
     */
    private String objClass;
    private Annotation definition;

    private Collection<OntologyTerm> parents;
    private Collection<OntologyTerm> children;

    public IntactCvTerm() {
        //super call sets creation time data
        super();
    }

    public IntactCvTerm(String shortName) {
        super(shortName);
    }

    public IntactCvTerm(String shortName, String fullName, String miIdentifier, String objClass) {
        super(shortName, fullName, miIdentifier);
        this.objClass = objClass;
    }

    public IntactCvTerm(String shortName, String miIdentifier) {
        super(shortName, miIdentifier);
    }

    public IntactCvTerm(String shortName, String fullName, String miIdentifier) {
        super(shortName, fullName, miIdentifier);
    }

    public IntactCvTerm(String shortName, Xref ontologyId) {
        super(shortName, ontologyId);
    }

    public IntactCvTerm(String shortName, Xref ontologyId, String objClass) {
        super(shortName, ontologyId);
        this.objClass = objClass;
    }

    public IntactCvTerm(String shortName, String fullName, Xref ontologyId) {
        super(shortName, fullName, ontologyId);
    }

    public IntactCvTerm(String shortName, String fullName, Xref ontologyId, String def){
        this(shortName, fullName, ontologyId);
        setDefinition(def);
    }

    @Column(name = "shortlabel", nullable = false)
    @Size( min = 1, max = IntactUtils.MAX_SHORT_LABEL_LEN )
    @NotNull
    public String getShortName() {
        return super.getShortName();
    }

    public void setShortName(String name) {
        if (name == null){
            throw new IllegalArgumentException("The short name cannot be null");
        }
        super.setShortName(name.toLowerCase());
    }

    /////////////////////////////
    // Entity fields

    /**
     *
     * @param objClass
     * @deprecated the objclass is deprecated and only kept for backward compatibility with intact-core.
     */
    @Deprecated
    public void setObjClass( String objClass ) {
        this.objClass = objClass;
    }

    @OneToMany( cascade = {CascadeType.ALL}, orphanRemoval = true, targetEntity = CvTermAlias.class)
    @JoinColumn(name="parent_ac", referencedColumnName="ac")
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(CvTermAlias.class)
    @Override
    public Collection<Alias> getSynonyms() {
        return super.getSynonyms();
    }

    @OneToMany( cascade = {CascadeType.ALL}, orphanRemoval = true, targetEntity = CvTermAnnotation.class)
    @JoinTable(
            name="ia_cvobject2annot",
            joinColumns = @JoinColumn( name="cvobject_ac"),
            inverseJoinColumns = @JoinColumn( name="annotation_ac")
    )
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(CvTermAnnotation.class)
    @Override
    /**
     * WARNING: The join table is for backward compatibility with intact-core.
     * When intact-core will be removed, the join table would disappear wnd the relation would become
     * @JoinColumn(name="parent_ac", referencedColumnName="ac")
     */
    public Collection<Annotation> getDbAnnotations() {
        return super.getDbAnnotations();
    }

    @Override
    protected boolean processAddedAnnotations(Annotation annot) {
        if (annot.getTopic().getShortName().equalsIgnoreCase("definition")){
            this.definition = annot;
            return true;
        }
        return false;
    }

    @Override
    protected void resetFieldsLinkedToAnnotations() {
        this.definition = null;
    }

    /**
     * Sets the old identifier
     * @param identifier
     * @deprecated only kept for backward compatibility with intact-core
     */
    @Deprecated
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    ///////////////////////////////////////
    // associations

    ///////////////////////////////////////
    // access methods for associations

    @ManyToMany( mappedBy = "parents", targetEntity = IntactCvTerm.class )
    @Target(IntactCvTerm.class)
    public Collection<OntologyTerm> getChildren() {
        if (children == null){
            children = new ArrayList<OntologyTerm>();
        }
        return children;
    }

//////////////////////////////////////////////////////////
    // the setter and getter are only used for testing

    public void addChild( OntologyTerm cvDagObject ) {

        getChildren().add( cvDagObject );
        if (!cvDagObject.getParents().contains(cvDagObject)){
            cvDagObject.getParents().add( this );
        }
    }

    public void removeChild( OntologyTerm cvDagObject ) {
        boolean removed = getChildren().remove( cvDagObject );
        if ( removed ) {
            cvDagObject.getParents().remove( this );
        }
    }

    @ManyToMany(targetEntity = IntactCvTerm.class)
    @JoinTable(
            name = "ia_cv2cv",
            joinColumns = {@JoinColumn( name = "child_ac", referencedColumnName = "ac" )},
            inverseJoinColumns = {@JoinColumn( name = "parent_ac", referencedColumnName = "ac" )}
    )
    @Target(IntactCvTerm.class)
    public Collection<OntologyTerm> getParents() {
        if (parents == null){
            parents = new ArrayList<OntologyTerm>();
        }
        return parents;
    }

    public void addParent( OntologyTerm cvDagObject ) {

        getParents().add( cvDagObject );
        if (!cvDagObject.getChildren().contains(this)){
            cvDagObject.getChildren().add( this );
        }
    }

    public void removeParent( OntologyTerm cvDagObject ) {
        boolean removed = getParents().remove( cvDagObject );
        if ( removed ) {
            cvDagObject.getChildren().remove( this );
        }
    }

    /**
     * NOTE: This field is currently transient for backward compatibility with intact-core.
     * In the future, we plan to have a proper column
     * @Column(name = "definition", length = IntactUtils.MAX_DESCRIPTION_LEN )
     * @Size( max = IntactUtils.MAX_DESCRIPTION_LEN )
     */
    @Transient
    public String getDefinition() {
        // initialise annot first
        getAnnotations();
        return this.definition != null ? this.definition.getValue() : null;
    }

    public void setDefinition(String def) {
        if (getDefinition() != null){
            getDbAnnotations().remove(this.definition);
        }
        this.definition = new CvTermAnnotation(IntactUtils.createMITopic("definition", null), def);
        getDbAnnotations().add(this.definition);
    }

    @Column( name = "objclass", nullable = false)
    public String getObjClass() {
        if (this.objClass == null){
            this.objClass = IntactUtils.TOPIC_OBJCLASS;
        }
        return objClass;
    }

    @Transient
    public boolean areXrefsInitialized(){
        return Hibernate.isInitialized(getDbXrefs());
    }

    @Transient
    public boolean areSynonymsInitialized(){
        return Hibernate.isInitialized(getSynonyms());
    }

    @Transient
    public boolean areAnnotationsInitialized(){
        return Hibernate.isInitialized(getDbAnnotations());
    }

    @Transient
    public boolean areChildrenInitialized(){
        return Hibernate.isInitialized(getChildren());
    }

    @Transient
    public boolean areParentsInitialized(){
        return Hibernate.isInitialized(getParents());
    }

    @OneToMany( cascade = {CascadeType.ALL}, orphanRemoval = true, targetEntity = CvTermXref.class)
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @JoinColumn(name="parent_ac", referencedColumnName="ac")
    @Target(CvTermXref.class)
    @LazyCollection(LazyCollectionOption.FALSE)
    @Override
    /**
     * This method give direct access to the persistent collection of xrefs (identifiers and xrefs all together) for this object.
     * WARNING: It should not be used to add/remove objects as it may mess up with the state of the object (only used this way by the synchronizers).
     */
    public Collection<Xref> getDbXrefs() {
        return super.getDbXrefs();
    }

    /**
     * Identifier for this object, which is a de-normalization of the
     * value contained in the 'identity' xref from the 'psimi' database
     * @return the MI Identifier for this CVObject
     * @since 1.9.x
     * @deprecated Only kept for backward compatibility with intact-core
     */
    @Column(name = "identifier", length = 30)
    @Size(max = 30)
    @Index(name = "cvobject_id_idx")
    @Deprecated
    protected String getIdentifier() {
        return this.identifier;
    }

    private void setChildren( Collection<OntologyTerm> children ) {
        this.children = children;
    }

    private void setParents( Collection<OntologyTerm> parents ) {
        this.parents = parents;
    }

}
