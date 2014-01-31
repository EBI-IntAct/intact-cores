package uk.ac.ebi.intact.jami.dao;

import uk.ac.ebi.intact.jami.model.extension.IntactVariableParameter;

import java.util.Collection;

/**
 * DAO for variable parameter
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>31/01/14</pre>
 */

public interface VariableParameterDao extends IntactBaseDao<IntactVariableParameter> {

    public Collection<IntactVariableParameter> getByDescription(String desc);

    public Collection<IntactVariableParameter> getByDescriptionLike(String desc);

    public Collection<IntactVariableParameter> getByUnit(String unitName, String unitMI);

    public Collection<IntactVariableParameter> getByExperimentAc(String parentAc);
}
