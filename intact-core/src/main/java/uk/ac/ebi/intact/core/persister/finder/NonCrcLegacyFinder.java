package uk.ac.ebi.intact.core.persister.finder;

import uk.ac.ebi.intact.core.context.IntactContext;
import uk.ac.ebi.intact.core.persistence.dao.InteractionDao;
import uk.ac.ebi.intact.model.Interaction;
import uk.ac.ebi.intact.model.util.CrcCalculator;
import uk.ac.ebi.intact.model.util.InteractionUtils;

import java.util.List;

/**
 * This Finder tries to fetch the AC of an interaction ignoring the CRC
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class NonCrcLegacyFinder extends DefaultFinder {

    public NonCrcLegacyFinder(IntactContext intactContext) {
        super(intactContext);
    }

    @Override
    protected String findAcForInteraction( Interaction interaction ) {
        // replace all this eventually by just using the CRC

        InteractionDao interactionDao = getDaoFactory().getInteractionDao();

        CrcCalculator crcCalculator = new CrcCalculator();

        // Get the interactors where exactly the same interactors are involved
        List<String> interactorPrimaryIDs = InteractionUtils.getInteractorPrimaryIDs( interaction );
        List<Interaction> interactionsWithSameInteractors =
                interactionDao.getByInteractorsPrimaryId( true, interactorPrimaryIDs.toArray( new String[interactorPrimaryIDs.size()] ) );

        for ( Interaction interactionWithSameInteractor : interactionsWithSameInteractors ) {
            String interactionCrc = crcCalculator.crc64( interaction );
            String interactionWithSameInteractorCrc = crcCalculator.crc64( interactionWithSameInteractor );

            if ( interactionCrc.equals( interactionWithSameInteractorCrc ) ) {
                return interactionWithSameInteractor.getAc();
            }
        }

        return null;
    }

}
