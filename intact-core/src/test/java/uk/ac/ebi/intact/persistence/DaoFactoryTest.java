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
package uk.ac.ebi.intact.persistence;

import org.h2.tools.Server;
import org.junit.Test;
import uk.ac.ebi.intact.context.IntactContext;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;

/**
 * TODO comment this
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class DaoFactoryTest {

    /**
     * Bug: http://www.ebi.ac.uk/interpro/internal-tools/jira-intact/browse/IAC-140
     *
     * @throws Exception
     */
    @Test
    public void connection_creation() throws Exception {
        Server server = Server.createTcpServer(new String[0]).start();

        File hibernateConfigFile = new File(DaoFactoryTest.class.getResource("/META-INF/h2server-hibernate.cfg.xml").getFile());
        IntactContext.initStandaloneContext(hibernateConfigFile);


        for (int i = 0; i < 6; i++) {
            Connection connection = IntactContext.getCurrentInstance().getDataContext()
                    .getDaoFactory().connection();

            ResultSet rs = connection.createStatement().executeQuery("select shortlabel from ia_institution");

            while (rs.next()) {
                rs.getString(1);
            }
        }

        IntactContext.getCurrentInstance().close();
        server.stop();
    }

}