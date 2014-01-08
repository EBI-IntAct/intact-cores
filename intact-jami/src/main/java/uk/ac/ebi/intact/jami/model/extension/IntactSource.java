package uk.ac.ebi.intact.jami.model.extension;

import psidev.psi.mi.jami.model.*;
import psidev.psi.mi.jami.model.impl.DefaultAnnotation;
import psidev.psi.mi.jami.model.impl.DefaultCvTerm;
import psidev.psi.mi.jami.utils.AnnotationUtils;
import psidev.psi.mi.jami.utils.CvTermUtils;
import psidev.psi.mi.jami.utils.collection.AbstractListHavingProperties;

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

    @Override
    protected void initialiseAnnotations() {
        initialiseAnnotationsWith(new SourceAnnotationList());
    }

    public String getUrl() {
        return this.url != null ? this.url.getValue() : null;
    }

    public void setUrl(String url) {
        SourceAnnotationList sourceAnnotationList = (SourceAnnotationList)getAnnotations();

        // add new url if not null
        if (url != null){
            CvTerm urlTopic = CvTermUtils.createMICvTerm(Annotation.URL, Annotation.URL_MI);
            // first remove old url if not null
            if (this.url != null){
                sourceAnnotationList.removeOnly(this.url);
            }
            this.url = new DefaultAnnotation(urlTopic, url);
            sourceAnnotationList.addOnly(this.url);
        }
        // remove all url if the collection is not empty
        else if (!sourceAnnotationList.isEmpty()) {
            AnnotationUtils.removeAllAnnotationsWithTopic(sourceAnnotationList, Annotation.URL_MI, Annotation.URL);
            this.url = null;
        }
    }

    public String getPostalAddress() {
        return this.postalAddress != null ? this.postalAddress.getValue() : null;
    }

    public void setPostalAddress(String address) {
        SourceAnnotationList sourceAnnotationList = (SourceAnnotationList)getAnnotations();

        // add new url if not null
        if (address != null){
            CvTerm addressTopic = new DefaultCvTerm(Annotation.POSTAL_ADDRESS);
            // first remove old url if not null
            if (this.postalAddress != null){
                sourceAnnotationList.removeOnly(this.postalAddress);
            }
            this.postalAddress = new DefaultAnnotation(addressTopic, address);
            sourceAnnotationList.addOnly(this.postalAddress);
        }
        // remove all url if the collection is not empty
        else if (!sourceAnnotationList.isEmpty()) {
            AnnotationUtils.removeAllAnnotationsWithTopic(sourceAnnotationList, null, Annotation.POSTAL_ADDRESS);
            this.postalAddress = null;
        }
    }

    public Publication getPublication() {
        return this.bibRef;
    }

    public void setPublication(Publication ref) {
        this.bibRef = ref;
    }

    protected void processAddedAnnotationEvent(Annotation added) {
        if (url == null && AnnotationUtils.doesAnnotationHaveTopic(added, Annotation.URL_MI, Annotation.URL)){
            url = added;
        }
        else if (postalAddress == null && AnnotationUtils.doesAnnotationHaveTopic(added, null, Annotation.POSTAL_ADDRESS)){
            postalAddress = added;
        }
    }

    protected void processRemovedAnnotationEvent(Annotation removed) {
        if (url != null && url.equals(removed)){
            url = null;
        }
        else if (postalAddress != null && postalAddress.equals(removed)){
            postalAddress = null;
        }
    }

    protected void clearPropertiesLinkedToAnnotations() {
        url = null;
        postalAddress = null;
    }

    private class SourceAnnotationList extends AbstractListHavingProperties<Annotation> {
        public SourceAnnotationList(){
            super();
        }

        @Override
        protected void processAddedObjectEvent(Annotation added) {
            processAddedAnnotationEvent(added);
        }

        @Override
        protected void processRemovedObjectEvent(Annotation removed) {
            processRemovedAnnotationEvent(removed);
        }

        @Override
        protected void clearProperties() {
            clearPropertiesLinkedToAnnotations();
        }
    }
}
