package uk.ac.ebi.intact.jami.model.listener;

import org.apache.commons.lang.StringUtils;
import psidev.psi.mi.jami.model.Annotation;
import psidev.psi.mi.jami.model.CurationDepth;
import psidev.psi.mi.jami.utils.AnnotationUtils;
import uk.ac.ebi.intact.jami.model.extension.IntactPublication;
import uk.ac.ebi.intact.jami.model.extension.PublicationAnnotation;
import uk.ac.ebi.intact.jami.utils.IntactUtils;

import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.text.ParseException;
import java.util.Arrays;

/**
 * This listener listen to Publication object pre update/persist/load events
 * and set authors/journal/publication date accordingly to existing annotations
 * This listener is for backward compatibility only with previous intact-core.
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>13/01/14</pre>
 */

public class PublicationPropertiesListener {

    @PrePersist
    @PreUpdate
    public void prePersistAndPreUpdate(IntactPublication pub){
        if (pub.getAnnotations().isEmpty()){
            if (pub.getJournal() != null){
                pub.getAnnotations().add(new PublicationAnnotation(IntactUtils.createMITopic(Annotation.PUBLICATION_JOURNAL, Annotation.PUBLICATION_JOURNAL_MI), pub.getJournal()));
            }
            if (pub.getPublicationDate() != null){
                pub.getAnnotations().add(new PublicationAnnotation(IntactUtils.createMITopic(Annotation.PUBLICATION_YEAR, Annotation.PUBLICATION_YEAR_MI), IntactUtils.YEAR_FORMAT.format(pub.getPublicationDate())));
            }
            if (!pub.getAuthors().isEmpty()){
                pub.getAnnotations().add(new PublicationAnnotation(IntactUtils.createMITopic(Annotation.AUTHOR, Annotation.AUTHOR_MI), StringUtils.join(pub.getAuthors(), ", ")));
            }
            if (!CurationDepth.undefined.equals(pub.getCurationDepth())){
                switch (pub.getCurationDepth()){
                    case IMEx:
                        pub.getAnnotations().add(new PublicationAnnotation(IntactUtils.createMITopic(Annotation.CURATION_DEPTH, Annotation.CURATION_DEPTH_MI),Annotation.IMEX_CURATION));
                        break;
                    case MIMIx:
                        pub.getAnnotations().add(new PublicationAnnotation(IntactUtils.createMITopic(Annotation.CURATION_DEPTH, Annotation.CURATION_DEPTH_MI),Annotation.MIMIX_CURATION));
                        break;
                    case rapid_curation:
                        pub.getAnnotations().add(new PublicationAnnotation(IntactUtils.createMITopic(Annotation.CURATION_DEPTH, Annotation.CURATION_DEPTH_MI),Annotation.RAPID_CURATION));
                        break;
                    default:
                        break;
                }
            }
        }
        else{
            if (pub.getJournal() == null){
                AnnotationUtils.removeAllAnnotationsWithTopic(pub.getAnnotations(), Annotation.PUBLICATION_JOURNAL_MI, Annotation.PUBLICATION_JOURNAL);
            }
            else{
                Annotation journal = AnnotationUtils.collectFirstAnnotationWithTopic(pub.getAnnotations(), Annotation.PUBLICATION_JOURNAL_MI, Annotation.PUBLICATION_JOURNAL);
                if (journal != null && journal.getValue() != null){
                    if (!pub.getJournal().equalsIgnoreCase(journal.getValue())){
                        journal.setValue(pub.getJournal());
                    }
                }
                else {
                    pub.getAnnotations().remove(journal);
                }
            }
            if (pub.getPublicationDate() == null){
                AnnotationUtils.removeAllAnnotationsWithTopic(pub.getAnnotations(), Annotation.PUBLICATION_YEAR_MI, Annotation.PUBLICATION_YEAR);
            }
            else{
                Annotation date = AnnotationUtils.collectFirstAnnotationWithTopic(pub.getAnnotations(), Annotation.PUBLICATION_YEAR_MI, Annotation.PUBLICATION_YEAR);
                String year = IntactUtils.YEAR_FORMAT.format(pub.getPublicationDate());
                if (date != null && date.getValue() != null){
                    if (!year.equalsIgnoreCase(date.getValue())){
                        date.setValue(year);
                    }
                }
                else {
                    pub.getAnnotations().add(new PublicationAnnotation(IntactUtils.createMITopic(Annotation.PUBLICATION_YEAR, Annotation.PUBLICATION_YEAR_MI), year));
                }
            }
            if (pub.getAuthors().isEmpty()){
                AnnotationUtils.removeAllAnnotationsWithTopic(pub.getAnnotations(), Annotation.AUTHOR_MI, Annotation.AUTHOR);
            }
            else{
                Annotation author = AnnotationUtils.collectFirstAnnotationWithTopic(pub.getAnnotations(), Annotation.AUTHOR_MI, Annotation.AUTHOR);
                String newAuth = StringUtils.join(pub.getAuthors(), ", ");
                if (author != null && author.getValue() != null){
                    if (!newAuth.equalsIgnoreCase(author.getValue())){
                        author.setValue(newAuth);
                    }
                }
                else {
                    pub.getAnnotations().add(new PublicationAnnotation(IntactUtils.createMITopic(Annotation.AUTHOR, Annotation.AUTHOR_MI), newAuth));
                }
            }
            if (pub.getCurationDepth().equals(CurationDepth.undefined)){
                AnnotationUtils.removeAllAnnotationsWithTopic(pub.getAnnotations(), Annotation.CURATION_DEPTH_MI, Annotation.CURATION_DEPTH);
            }
            else{
                Annotation depth = AnnotationUtils.collectFirstAnnotationWithTopic(pub.getAnnotations(), Annotation.CURATION_DEPTH_MI, Annotation.CURATION_DEPTH);
                switch (pub.getCurationDepth()){
                    case IMEx:
                        if (depth != null && depth.getValue() != null){
                            if (!depth.getValue().equalsIgnoreCase(Annotation.IMEX_CURATION)){
                                depth.setValue(Annotation.IMEX_CURATION);
                            }
                        }
                        else {
                            pub.getAnnotations().add(new PublicationAnnotation(IntactUtils.createMITopic(Annotation.CURATION_DEPTH, Annotation.CURATION_DEPTH_MI), Annotation.IMEX_CURATION));
                        }
                        break;
                    case MIMIx:
                        if (depth != null && depth.getValue() != null){
                            if (!depth.getValue().equalsIgnoreCase(Annotation.MIMIX_CURATION)){
                                depth.setValue(Annotation.MIMIX_CURATION);
                            }
                        }
                        else {
                            pub.getAnnotations().add(new PublicationAnnotation(IntactUtils.createMITopic(Annotation.CURATION_DEPTH, Annotation.CURATION_DEPTH_MI), Annotation.MIMIX_CURATION));
                        }
                        break;
                    case rapid_curation:
                        if (depth != null && depth.getValue() != null){
                            if (!depth.getValue().equalsIgnoreCase(Annotation.RAPID_CURATION)){
                                depth.setValue(Annotation.RAPID_CURATION);
                            }
                        }
                        else {
                            pub.getAnnotations().add(new PublicationAnnotation(IntactUtils.createMITopic(Annotation.CURATION_DEPTH, Annotation.CURATION_DEPTH_MI), Annotation.RAPID_CURATION));
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @PostLoad
    public void postLoad(IntactPublication pub){

        if (pub.getAnnotations().isEmpty()){
            pub.setJournal(null);
            pub.setPublicationDate(null);
            pub.getAuthors().clear();
            pub.setCurationDepth(pub.getImexId() == null ? CurationDepth.undefined : CurationDepth.IMEx);
        }
        else{
            // journal
            if (pub.getJournal() == null){
                Annotation journal = AnnotationUtils.collectFirstAnnotationWithTopic(pub.getAnnotations(), Annotation.PUBLICATION_JOURNAL_MI, Annotation.PUBLICATION_JOURNAL);
                if (journal != null){
                    pub.setJournal(journal.getValue());
                }
                else {
                    pub.setJournal(null);
                }
            }

            // publication date
            if (pub.getPublicationDate() == null){
                Annotation date = AnnotationUtils.collectFirstAnnotationWithTopic(pub.getAnnotations(), Annotation.PUBLICATION_YEAR_MI, Annotation.PUBLICATION_YEAR);
                if (date != null && date.getValue() != null){
                    try {
                        pub.setPublicationDate(IntactUtils.YEAR_FORMAT.parse(date.getValue().trim()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                        pub.setPublicationDate(null);
                    }
                }
                else{
                    pub.setPublicationDate(null);
                }
            }

            // authors
            Annotation authors = AnnotationUtils.collectFirstAnnotationWithTopic(pub.getAnnotations(), Annotation.AUTHOR_MI, Annotation.AUTHOR);
            if (authors != null && authors.getValue() != null){
                if (authors.getValue().contains(", ")){
                    pub.getAuthors().addAll(Arrays.asList(authors.getValue().split(", ")));
                }
                else {
                    pub.getAuthors().add(authors.getValue());
                }
            }

            // curation depth
            if (pub.getCurationDepth().equals(CurationDepth.undefined)){
                Annotation depth = AnnotationUtils.collectFirstAnnotationWithTopic(pub.getAnnotations(), Annotation.CURATION_DEPTH_MI, Annotation.CURATION_DEPTH);
                if (depth != null && depth.getValue() != null){
                    if (Annotation.IMEX_CURATION.equalsIgnoreCase(depth.getValue())){
                        pub.setCurationDepth(CurationDepth.IMEx);
                    }
                    else if (Annotation.MIMIX_CURATION.equalsIgnoreCase(depth.getValue())){
                        pub.setCurationDepth(CurationDepth.MIMIx);
                    }
                    else if (Annotation.RAPID_CURATION.equalsIgnoreCase(depth.getValue())){
                        pub.setCurationDepth(CurationDepth.rapid_curation);
                    }
                }
            }
        }
    }
}
