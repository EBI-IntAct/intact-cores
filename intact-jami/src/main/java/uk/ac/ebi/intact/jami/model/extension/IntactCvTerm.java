package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.Alias;
import psidev.psi.mi.jami.model.Annotation;
import psidev.psi.mi.jami.model.OntologyTerm;
import psidev.psi.mi.jami.model.Xref;
import uk.ac.ebi.intact.jami.model.listener.CvDefinitionListener;
import uk.ac.ebi.intact.jami.model.listener.CvIdentifierListener;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Intact implementation of Cv term
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>07/01/14</pre>
 */
@Entity
@Table( name = "ia_controlledvocab",
        uniqueConstraints = {@UniqueConstraint(columnNames={"objclass", "shortlabel"})})
@EntityListeners(value = {CvIdentifierListener.class, CvDefinitionListener.class})
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
    private String definition;

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
        this.definition = def;
    }

    @Column(name = "shortlabel", nullable = false)
    @Size( min = 1, max = IntactUtils.MAX_SHORT_LABEL_LEN )
    @NotNull
    public String getShortName() {
        return super.getShortName();
    }

    public void setShortName(String name) {
        super.setShortName(name);
    }

    /////////////////////////////
    // Entity fields

    /**
     *
     * @param objClass
     * @deprecated the objclass is deprecated and only kept for backward compatibility with intact-core.
     */
    public void setObjClass( String objClass ) {
        this.objClass = objClass;
    }

    @OneToMany( mappedBy = "parent", cascade = {CascadeType.ALL}, orphanRemoval = true, targetEntity = CvTermAnnotation.class)
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(CvTermAnnotation.class)
    @Override
    public Collection<Annotation> getAnnotations() {
        return super.getAnnotations();
    }

    @OneToMany( mappedBy = "parent", cascade = {CascadeType.ALL}, orphanRemoval = true, targetEntity = CvTermXref.class)
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(CvTermXref.class)
    @Override
    protected Collection<Xref> getPersistentXrefs() {
        return super.getPersistentXrefs();
    }

    @OneToMany( mappedBy = "parent", cascade = {CascadeType.ALL}, orphanRemoval = true, targetEntity = CvTermAlias.class)
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(CvTermAlias.class)
    @Override
    public Collection<Alias> getSynonyms() {
        return super.getSynonyms();
    }

    /**
     * Sets the old identifier
     * @param identifier
     * @deprecated only kept for backward compatibility with intact-core
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    ///////////////////////////////////////
    // associations

    ///////////////////////////////////////
    // access methods for associations

    @ManyToMany(targetEntity = IntactCvTerm.class)
    @JoinTable(
            name = "ia_cv2cv",
            joinColumns = {@JoinColumn( name = "parent_ac", referencedColumnName = "ac" )},
            inverseJoinColumns = {@JoinColumn( name = "child_ac", referencedColumnName = "ac" )}
    )
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

        if ( !getChildren().contains( cvDagObject ) ) {
            children.add( cvDagObject );
            cvDagObject.getParents().add( this );
        }
    }

    public void removeChild( OntologyTerm cvDagObject ) {
        boolean removed = getChildren().remove( cvDagObject );
        if ( removed ) {
            cvDagObject.getParents().remove( this );
        }
    }

    @ManyToMany( mappedBy = "children", targetEntity = IntactCvTerm.class )
    @Target(IntactCvTerm.class)
    public Collection<OntologyTerm> getParents() {
        if (parents == null){
            parents = new ArrayList<OntologyTerm>();
        }
        return parents;
    }

    public void addParent( OntologyTerm cvDagObject ) {

        if ( !getParents().contains( cvDagObject ) ) {
            parents.add( cvDagObject );
            cvDagObject.getChildren().add( this );
        }
    }

    public void removeParent( OntologyTerm cvDagObject ) {
        boolean removed = getParents().remove( cvDagObject );
        if ( removed ) {
            cvDagObject.getChildren().remove( this );
        }
    }

    @Column(name = "definition", length = IntactUtils.MAX_DESCRIPTION_LEN )
    @Size( max = IntactUtils.MAX_DESCRIPTION_LEN )
    public String getDefinition() {
        return this.definition;
    }

    public void setDefinition(String def) {
        this.definition = def;
    }

    @Override
    protected Xref instantiateXrefFrom(Xref added) {
        return new CvTermXref(added.getDatabase(), added.getId(), added.getVersion(), added.getQualifier());
    }

    @Override
    protected boolean needToWrapXrefForPersistence(Xref added) {
        if (!(added instanceof CvTermXref)){
            return true;
        }
        else{
            CvTermXref termXref = (CvTermXref)added;
            if (termXref.getParent() != null && termXref.getParent() != this){
                return true;
            }
        }
        return false;
    }

    private void setChildren( Collection<OntologyTerm> children ) {
        this.children = children;
    }

    private void setParents( Collection<OntologyTerm> parents ) {
        this.parents = parents;
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
    private String getIdentifier() {
        return this.identifier;
    }

    @Column( name = "objclass", nullable = false)
    private String getObjClass() {
        return objClass;
    }
}