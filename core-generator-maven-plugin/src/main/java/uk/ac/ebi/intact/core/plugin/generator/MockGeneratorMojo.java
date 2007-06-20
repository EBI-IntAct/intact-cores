/**
 * Copyright 2006 The European Bioinformatics Institute, and others.
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
 *  limitations under the License.
 */
package uk.ac.ebi.intact.core.plugin.generator;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import uk.ac.ebi.intact.annotation.Mockable;
import uk.ac.ebi.intact.annotation.util.AnnotationUtil;
import uk.ac.ebi.intact.commons.lang.CommonURLClassLoader;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * Generates a DBUnit-dataset from a set of PSI 2.5 xml files
 *
 * @goal generate-mocks
 * @phase generate-test-sources
 * @requiresDependencyResolution compile
 */
public class MockGeneratorMojo extends AbstractMojo {

    private static final String MOCK_TEMPLATE = "Mock.vm";

    /**
     * Project instance
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * project-helper instance, used to make addition of resources
     * simpler.
     *
     * @component
     */
    private MavenProjectHelper helper;

    /**
     * @parameter default-value="uk.ac.ebi.intact.core.unit.mock"
     * @required
     */
    private String generatedPackage = "uk.ac.ebi.intact.core.unit.mock";


    public void execute() throws MojoExecutionException, MojoFailureException {

        getLog().info("Executing Core Mock Generator");

        // create a classloader that includes the result of the compilation of the project
        // in order to search the annotations in it
        File classesDir = new File(project.getBuild().getOutputDirectory());
        URL classesDirUrl = null;
        try {
            classesDirUrl = classesDir.toURL();
        } catch (MalformedURLException e) {
            throw new MojoExecutionException("Wrong URL: "+e.getMessage());
        }

        ClassLoader classLoader = new CommonURLClassLoader(new URL[] {classesDirUrl}, MockGeneratorMojo.class.getClassLoader());

        Collection<Class> mockableClasses = AnnotationUtil.getClassesWithAnnotationFromDir(Mockable.class, classesDir, classLoader);

        if (mockableClasses.isEmpty()) {
            getLog().warn("No mockable classes found in "+classesDir);
        }

        getLog().debug("Creating Mocks ("+mockableClasses.size()+"):");
        for (Class mockableClass : mockableClasses) {

            MockInfo mockInfo = new MockInfo(mockableClass, generatedPackage);

            try
            {
                generateMock(mockInfo);
            }
            catch (Exception e)
            {
                throw new MojoExecutionException("Problem generating enum class", e);
            }
        }

        // add the resources into the classpath
        List includes = Collections.singletonList("**/*.java");
        List excludes = null;
        helper.addResource(project, createResourcesDir().toString(), includes, excludes);
        project.addCompileSourceRoot(createResourcesDir().toString());
    }

    public void generateMock(MockInfo mockInfo) throws Exception {
        // create velocity context
        VelocityContext context = new VelocityContext();
        context.put("mojo", this);
        context.put("artifactId", project.getArtifactId());
        context.put("version", project.getVersion());
        context.put("mockInfo", mockInfo);

        Properties props = new Properties();
        props.setProperty("resource.loader", "class");
        props.setProperty("class." + VelocityEngine.RESOURCE_LOADER + ".class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");

        Velocity.init(props);

        Template template = Velocity.getTemplate(MOCK_TEMPLATE);

        // write the resulting file with velocity
        File mockFile = mockInfo.getFileName(createResourcesDir());
        Writer writer = new FileWriter(mockFile);
        template.merge(context, writer);
        writer.close();
    }

    private File createResourcesDir() {
        File file = new File(project.getBuild().getDirectory(), "mocks/");

        if (!file.exists()) {
            file.mkdirs();
        }

        return file;
    }


    public String getGeneratedPackage()
    {
        return generatedPackage;
    }


}