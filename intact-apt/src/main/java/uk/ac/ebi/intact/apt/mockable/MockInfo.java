/*
 * Copyright 2001-2007 The European Bioinformatics Institute.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.ac.ebi.intact.apt.mockable;

import java.io.File;
import java.io.IOException;

/**
 * TODO comment this
*
* @author Bruno Aranda (baranda@ebi.ac.uk)
* @version $Id$
*/
public class MockInfo {

    private static final String MOCK_CLASS_PREFIX = "Mock";
   
    private String simpleName;
    private String packageName;

    public MockInfo(String simpleName, String packageName) {
        this.packageName = packageName;

        this.simpleName = MOCK_CLASS_PREFIX +simpleName;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getSimpleName() {
        return simpleName;
    }

    public String getQualifiedName() {
        return packageName+"."+simpleName;
    }

    public File getFileName(File resourcesDir) throws IOException {
        File parentDir = createGeneratedPackageDir(resourcesDir);
        File file = new File(parentDir, simpleName + ".java");
        file.getParentFile().mkdirs();
        return file;
    }

    private File createGeneratedPackageDir(File resourcesDir) throws IOException {
        String strFile = packageName.replaceAll("\\.", "/");
        File file = new File(resourcesDir, strFile + "/");

        file.mkdirs();

        return file;
    }
}