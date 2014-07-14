package uk.ac.ebi.intact.jami.dao;

import psidev.psi.mi.jami.model.Alias;
import uk.ac.ebi.intact.jami.model.extension.IntactOrganism;

import java.util.Collection;

/**
 * DAO factory for organisms
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>23/01/14</pre>
 */

public interface OrganismDao extends IntactBaseDao<IntactOrganism>{

    public IntactOrganism getByAc(String ac);

    public IntactOrganism getByShortName(String value);

    public Collection<IntactOrganism> getByShortNameLike(String value);

    public Collection<IntactOrganism> getByScientificName(String value);

    public Collection<IntactOrganism> getByScientificNameLike(String value);

    public Collection<IntactOrganism> getByTaxid(int taxid);

    /**
     * Biosources with only a taxid, no celltype or tissue
     * @param taxid
     * @return
     */
    public IntactOrganism getByTaxidOnly(int taxid);

    public Collection<IntactOrganism> getByAliasName(String name);

    public Collection<IntactOrganism> getByAliasTypeAndName(String typeName, String typeMI, String name);

    public Collection<IntactOrganism> getByAliasNameLike(String name);

    public Collection<IntactOrganism> getByAliasTypeAndNameLike(String typeName, String typeMI, String name);

    public Collection<IntactOrganism> getByCellTypeAc(String cellAc);

    public Collection<IntactOrganism> getByTissueAc(String tissueAc);

    public Collection<IntactOrganism> getByCellTypeName(String cellName);

    public Collection<IntactOrganism> getByTissueName(String tissueName);

    public Collection<IntactOrganism> getByCellTypeNameLike(String cellName);

    public Collection<IntactOrganism> getByTissueNameLike(String tissueName);

    public Collection<Alias> getAliasesForOrganism(String ac);

    public Collection<IntactOrganism> getAllOrganisms(boolean allowOrganismWithCellType, boolean allowOrganismWithTissue);
}
