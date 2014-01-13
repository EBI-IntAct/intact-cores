package uk.ac.ebi.intact.jami.model.extension;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Target;
import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.utils.AnnotationUtils;
import psidev.psi.mi.jami.utils.collection.AbstractCollectionWrapper;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Intact implementation of a source. It replaces Institution from intact core
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>08/01/14</pre>
 */

public class IntactSource extends AbstractIntactCvTerm implements Source {
    private Annotation url;
    private Annotation postalAddress;
    private Publication bibRef;

    private String persistentURL;
    private String persistentPostalAddress;

    public IntactSource(String shortName) {
        super(shortName);
    }

    public IntactSource(String shortName, Xref ontologyId) {
        super(shortName, ontologyId);
    }

    public IntactSource(String shortName, String fullName, Xref ontologyId) {
        super(shortName, fullName, ontologyId);
    }

    public IntactSource(String shortName, String url, String address, Publication bibRef) {
        super(shortName);
        setUrl(url);
        setPostalAddress(address);
        this.bibRef = bibRef;
    }

    public IntactSource(String shortName, Xref ontologyId, String url, String address, Publication bibRef) {
        super(shortName, ontologyId);
        setUrl(url);
        setPostalAddress(address);
        this.bibRef = bibRef;
    }

    public IntactSource(String shortName, String fullName, Xref ontologyId, String url, String address, Publication bibRef) {
        super(shortName, fullName, ontologyId);
        setUrl(url);
        setPostalAddress(address);
        this.bibRef = bibRef;
    }

    public IntactSource(String shortName, String miId) {
        super(shortName, miId);
    }

    public IntactSource(String shortName, String fullName, String miId) {
        super(shortName, fullName, miId);
    }

    public IntactSource(String shortName, String miId, String url, String address, Publication bibRef) {
        super(shortName, miId);
        setUrl(url);
        setPostalAddress(address);
        this.bibRef = bibRef;
    }

    public IntactSource(String shortName, String fullName, String miId, String url, String address, Publication bibRef) {
        super(shortName, fullName, miId);
        setUrl(url);
        setPostalAddress(address);
        this.bibRef = bibRef;
    }

    @Transient
    public String getUrl() {
        return this.persistentURL;
    }

    public void setUrl(String url) {
        Collection<Annotation> sourceAnnotationList = getAnnotations();

        // add new url if not null
        if (url != null){
            CvTerm urlTopic = IntactUtils.createMITopic(Annotation.URL, Annotation.URL_MI);
            // first remove old url if not null
            if (this.url != null){
                sourceAnnotationList.remove(this.url);
            }
            this.url = new SourceAnnotation(urlTopic, url);
            this.persistentURL = url;
            sourceAnnotationList.add(this.url);
        }
        // remove all url if the collection is not empty
        else if (!sourceAnnotationList.isEmpty()) {
            AnnotationUtils.removeAllAnnotationsWithTopic(sourceAnnotationList, Annotation.URL_MI, Annotation.URL);
            this.url = null;
            this.persistentURL = null;
        }
    }

    @Transient
    public String getPostalAddress() {
        return this.persistentPostalAddress;
    }

    public void setPostalAddress(String address) {
        Collection<Annotation> sourceAnnotationList = getAnnotations();

        // add new url if not null
        if (address != null){
            CvTerm addressTopic = IntactUtils.createMITopic(Annotation.POSTAL_ADDRESS, null);
            // first remove old url if not null
            if (this.postalAddress != null){
                sourceAnnotationList.remove(this.postalAddress);
            }
            this.postalAddress = new SourceAnnotation(addressTopic, address);
            sourceAnnotationList.add(this.postalAddress);
            this.persistentPostalAddress = address;
        }
        // remove all url if the collection is not empty
        else if (!sourceAnnotationList.isEmpty()) {
            AnnotationUtils.removeAllAnnotationsWithTopic(sourceAnnotationList, null, Annotation.POSTAL_ADDRESS);
            this.postalAddress = null;
            this.persistentPostalAddress = null;
        }
    }

    @Transient
    // TODO check primary ref?
    public Publication getPublication() {
        return this.bibRef;
    }

    public void setPublication(Publication ref) {
        this.bibRef = ref;
    }

    @OneToMany( mappedBy = "parent", cascade = {CascadeType.ALL}, orphanRemoval = true, targetEntity = SourceAlias.class)
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(SourceAlias.class)
    public Collection<Alias> getSynonyms() {
        return super.getSynonyms();
    }

    @Override
    protected void initialiseAnnotations() {
        super.initialiseAnnotations();
        for (Annotation annot : getAnnotations()){
            processAddedAnnotationEvent(annot);
        }
    }

    @OneToMany( mappedBy = "parent", cascade = {CascadeType.ALL}, orphanRemoval = true, targetEntity = SourceXref.class)
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(SourceXref.class)
    protected Collection<Xref> getPersistentXrefs() {
        return super.getXrefs();
    }

    protected void processAddedAnnotationEvent(Annotation added) {
        if (url == null && AnnotationUtils.doesAnnotationHaveTopic(added, Annotation.URL_MI, Annotation.URL)){
            url = added;
            this.persistentURL = added.getValue();
        }
        else if (postalAddress == null && AnnotationUtils.doesAnnotationHaveTopic(added, null, Annotation.POSTAL_ADDRESS)){
            postalAddress = added;
            this.persistentPostalAddress = added.getValue();
        }
    }

    protected void processRemovedAnnotationEvent(Annotation removed) {
        if (url != null && url.equals(removed)){
            url = null;
            this.persistentURL = null;
        }
        else if (postalAddress != null && postalAddress.equals(removed)){
            postalAddress = null;
            this.persistentPostalAddress = null;
        }
    }

    protected void clearPropertiesLinkedToAnnotations() {
        url = null;
        postalAddress = null;
        this.postalAddress = null;
    }

    @Override
    protected Xref instantiateXrefFrom(Xref added) {
        SourceXref persistentRef;
        persistentRef = new SourceXref(added.getDatabase(), added.getId(), added.getVersion(), added.getQualifier());
        return persistentRef;
    }

    @Override
    protected boolean needToWrapXrefForPersistence(Xref added) {
        if (!(added instanceof SourceXref)){
            return false;
        }
        else{
            SourceXref termXref = (SourceXref)added;
            if (termXref.getParent() != null && termXref.getParent() != this){
                return false;
            }
        }
        return true;
    }

    @Override
    protected Annotation instantiateAnnotationFrom(Annotation added) {
        return new SourceAnnotation(added.getTopic(), added.getValue());
    }

    @Override
    protected boolean needToWrapAnnotationForPersistence(Annotation added) {
        if (!(added instanceof SourceAnnotation)){
            return false;
        }
        else{
            SourceAnnotation termAnnot = (SourceAnnotation)added;
            if (termAnnot.getParent() != null && termAnnot.getParent() != this){
                return false;
            }
        }
        return true;
    }

    @Override
    protected Alias instantiateAliasFrom(Alias added) {
        return new SourceAlias(added.getType(), added.getName());
    }

    @Override
    protected boolean needToWrapAliasForPersistence(Alias added) {
        if (!(added instanceof SourceAlias)){
            return false;
        }
        else{
            SourceAlias termAlias = (SourceAlias)added;
            if (termAlias.getParent() != null && termAlias.getParent() != this){
                return false;
            }
        }
        return true;
    }

    @OneToMany( mappedBy = "parent", cascade = {CascadeType.ALL}, orphanRemoval = true, targetEntity = SourceAnnotation.class)
    @Cascade( value = {org.hibernate.annotations.CascadeType.SAVE_UPDATE} )
    @Target(SourceAnnotation.class)
    private Collection<Annotation> getPersistentAnnotations() {
        return ((SourceAnnotationList)super.getAnnotations()).getWrappedList();
    }

    private void setPersistentAnnotations(Collection<Annotation> persistentAnnotations) {
        super.setAnnotations(new SourceAnnotationList(persistentAnnotations));
    }

    @Column(name = "url")
    public String getPersistentURL() {
        return persistentURL;
    }

    public void setPersistentURL(String persistentURL) {
        this.persistentURL = persistentURL;
    }

    @Column(name = "postaladdress")
    public String getPersistentPostalAddress() {
        return persistentPostalAddress;
    }

    public void setPersistentPostalAddress(String persistentPostalAddress) {
        this.persistentPostalAddress = persistentPostalAddress;
    }

    private class SourceAnnotationList extends AbstractCollectionWrapper<Annotation> {
        public SourceAnnotationList(Collection<Annotation> annots){
            super(annots);
        }

        @Override
        public boolean add(Annotation xref) {
            if(super.add(xref)){
                processAddedAnnotationEvent(xref);
                return true;
            }
            return false;
        }

        @Override
        public boolean remove(Object o) {
            if (super.remove(o)){
                processRemovedAnnotationEvent((Annotation)o);
                return true;
            }
            return false;
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            boolean hasChanged = false;
            for (Object annot : c){
                if (remove(annot)){
                    hasChanged = true;
                }
            }
            return hasChanged;
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            List<Annotation> existingObject = new ArrayList<Annotation>(this);

            boolean removed = false;
            for (Annotation o : existingObject){
                if (!c.contains(o)){
                    if (remove(o)){
                        removed = true;
                    }
                }
            }

            return removed;
        }

        @Override
        public void clear() {
            super.clear();
            clearPropertiesLinkedToAnnotations();
        }

        @Override
        protected boolean needToPreProcessElementToAdd(Annotation added) {
            return false;
        }

        @Override
        protected Annotation processOrWrapElementToAdd(Annotation added) {
            return added;
        }
    }
}
