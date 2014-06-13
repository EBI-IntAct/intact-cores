/**
 * Copyright 2009 The European Bioinformatics Institute, and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.intact.model.util;

import org.springframework.beans.factory.FactoryBean;
import uk.ac.ebi.intact.core.unit.IntactMockBuilder;
import uk.ac.ebi.intact.model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Used to create institutions;
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class InstitutionFactoryBean implements FactoryBean {

    private String name;
    private String description;
    private String miAc;
    private String url;
    private String address;
    private String pubmed;
    private String email;
    private List<String> aliases;

    public InstitutionFactoryBean() {
        aliases = new ArrayList<String>();
    }

    public Object getObject() throws Exception {
        if (name == null) {
            name = "unknown";
        }

        Institution institution = new Institution(name);
        institution.setFullName(description);

        IntactMockBuilder mockBuilder = new IntactMockBuilder(institution);

        if (miAc != null) {
            InstitutionXref idXref = mockBuilder.createIdentityXrefPsiMi(institution, miAc);
            institution.addXref(idXref);
        }

        if (pubmed != null) {
            CvDatabase cvPubmed = mockBuilder.createCvObject(CvDatabase.class, CvDatabase.PUBMED_MI_REF, CvDatabase.PUBMED);
            InstitutionXref pubmedXref = mockBuilder.createIdentityXref(institution, pubmed, cvPubmed);
            institution.addXref(pubmedXref);
        }

        if (url != null) {
            CvTopic cvUrl = CvObjectUtils.createCvObject(institution, CvTopic.class, CvTopic.URL_MI_REF, CvTopic.URL);

            Annotation annotation = mockBuilder.createAnnotation( url, cvUrl);
            institution.addAnnotation(annotation);
        }

        if (address != null) {
            CvTopic cvAddress = new CvTopic("postaladdress");

            Annotation annotation = new Annotation( cvAddress, address);
            institution.addAnnotation(annotation);
        }

        if (email != null) {
            CvTopic cvEmail = CvObjectUtils.createCvObject(institution, CvTopic.class, CvTopic.CONTACT_EMAIL_MI_REF, CvTopic.CONTACT_EMAIL);

            Annotation annotation = mockBuilder.createAnnotation(email, cvEmail);
            institution.addAnnotation(annotation);
        }

        for (String aliasName : aliases) {
            InstitutionAlias alias = new InstitutionAlias(institution, institution, null, aliasName);
            institution.addAlias(alias);
        }

        return institution;
    }

    public Class getObjectType() {
        return Institution.class;
    }

    public boolean isSingleton() {
        return false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMiAc() {
        return miAc;
    }

    public void setMiAc(String miAc) {
        this.miAc = miAc;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPubmed() {
        return pubmed;
    }

    public void setPubmed(String pubmed) {
        this.pubmed = pubmed;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public void setAliases(List<String> aliases) {
        this.aliases = aliases;
    }
}
