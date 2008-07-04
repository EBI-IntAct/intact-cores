package uk.ac.ebi.intact.persistence.svc;

import java.util.Date;

/**
 * Service interface to update the last protein and cv update info in to DBinfo table
 *
 * @author Prem Anand (prem@ebi.ac.uk)
 * @version $Id$
 * @since 2.0.1-SNAPSHOT
 */
public interface DbInfoService {

    void saveLastProteinUpdate( Date date) throws DbInfoServiceException;
    void saveLastCvUpdate(Date date,String namespace) throws DbInfoServiceException;

    Date getLastProteinUpdate() throws DbInfoServiceException;
    Date getLastCvUpdate(String namespace) throws DbInfoServiceException;

  


}
