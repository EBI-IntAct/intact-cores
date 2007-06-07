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
package uk.ac.ebi.intact.model.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.event.*;
import uk.ac.ebi.intact.context.IntactContext;
import uk.ac.ebi.intact.context.IntactSession;
import uk.ac.ebi.intact.context.RuntimeConfig;
import uk.ac.ebi.intact.model.Alias;
import uk.ac.ebi.intact.model.AnnotatedObject;
import uk.ac.ebi.intact.model.SearchItem;
import uk.ac.ebi.intact.model.Xref;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO comment this!
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>02-Oct-2006</pre>
 */
public class SearchItemSyncEventListener implements PostInsertEventListener, PostUpdateEventListener, PreDeleteEventListener {

    private static final Log log = LogFactory.getLog( SearchItemSyncEventListener.class );

    private IntactSession intactSession;

    public SearchItemSyncEventListener( IntactSession intactSession ) {
        this.intactSession = intactSession;
    }

    public void onPostInsert( PostInsertEvent postInsertEvent ) {
        if ( !RuntimeConfig.getCurrentInstance( intactSession ).isSynchronizedSearchItems() ) {
            return;
        }

        Object obj = postInsertEvent.getEntity();

        if ( obj instanceof AnnotatedObject ) {
            insertSearchItemForAnnotatedObject( ( AnnotatedObject ) obj, false );
        } else if ( obj instanceof Xref ) {
            insertSearchItemForXref( ( Xref ) obj );
        } else if ( obj instanceof Alias ) {
            insertSearchItemForAlias( ( Alias ) obj );
        } else {
            return;
        }
    }

    public void onPostUpdate( PostUpdateEvent postUpdateEvent ) {
        if ( !RuntimeConfig.getCurrentInstance( intactSession ).isSynchronizedSearchItems() ) {
            return;
        }

        Object obj = postUpdateEvent.getEntity();

        if ( obj instanceof AnnotatedObject ) {
            AnnotatedObject ao = ( AnnotatedObject ) obj;

            deleteSearchItemsForAnnotatedObject( ao );
            insertSearchItemForAnnotatedObject( ao, true );
        } else if ( obj instanceof Alias ) {
            Alias alias = ( Alias ) obj;
            deleteSearchItemsForAnnotatedObject( alias.getParent() );
            insertSearchItemForAnnotatedObject( alias.getParent(), true );
        } else if ( obj instanceof Xref ) {
            Xref xref = ( Xref ) obj;
            deleteSearchItemsForAnnotatedObject( xref.getParent() );
            insertSearchItemForAnnotatedObject( xref.getParent(), true );
        } else {
            return;
        }
    }

    public boolean onPreDelete( PreDeleteEvent preDeleteEvent ) {
        if ( !RuntimeConfig.getCurrentInstance( intactSession ).isSynchronizedSearchItems() ) {
            return false;
        }

        Object obj = preDeleteEvent.getEntity();

        if ( obj instanceof AnnotatedObject ) {
            deleteSearchItemsForAnnotatedObject( ( AnnotatedObject ) obj );
        } else if ( obj instanceof Xref ) {
            deleteSearchItemForXref( ( Xref ) obj );
        } else if ( obj instanceof Alias ) {
            deleteSearchItemForAlias( ( Alias ) obj );
        } else {
            return false;
        }

        return false;
    }

    private void insertSearchItemForAnnotatedObject( AnnotatedObject<? extends Xref, ? extends Alias> ao, boolean includeAliases ) {
        if ( log.isDebugEnabled() ) {
            log.debug( "Inserting SearchItems for AnnotatedObject: " + ao.getShortLabel() + " (" + ao.getAc() + ")" );
        }

        for ( SearchItem searchItem : searchItemsForAnnotatedObject( ao, includeAliases ) ) {
            if ( log.isDebugEnabled() ) {
                log.debug( "\t" + searchItem );
            }

            IntactContext.getCurrentInstance().getDataContext().getDaoFactory()
                    .getSearchItemDao().persist( searchItem );
        }
    }

    private void insertSearchItemForAlias( Alias alias ) {
        if ( log.isDebugEnabled() ) {
            log.debug( "Inserting SearchItems for Alias: " + alias.getName() + " (" + alias.getAc() + "); Parent AC: " + alias.getParentAc() );
        }

        if ( !isAliasSearchable( alias ) ) {
            return;
        }

        SearchItem searchItem = searchItemForAlias( alias );

        if ( log.isDebugEnabled() ) {
            log.debug( "\t" + searchItem );
        }

        IntactContext.getCurrentInstance().getDataContext().getDaoFactory()
                .getSearchItemDao().persist( searchItem );
    }

    private void insertSearchItemForXref( Xref xref ) {
        if ( log.isDebugEnabled() ) {
            log.debug( "Inserting SearchItems for Xref: " + xref.getPrimaryId() + " (" + xref.getAc() + "); Parent AC: " + xref.getParentAc() );
        }

        SearchItem searchItem = searchItemForXref( xref );

        if ( log.isDebugEnabled() ) {
            log.debug( "\t" + searchItem );
        }

        IntactContext.getCurrentInstance().getDataContext().getDaoFactory()
                .getSearchItemDao().persist( searchItem );
    }

    private void deleteSearchItemsForAnnotatedObject( AnnotatedObject<? extends Xref, ? extends Alias> ao ) {
        if ( log.isDebugEnabled() ) {
            log.debug( "Deleting SearchItems for AnnotatedObject: " + ao.getShortLabel() + " (" + ao.getAc() + ")" );
        }

        IntactContext.getCurrentInstance().getDataContext().getDaoFactory()
                .getSearchItemDao().deleteByAc( ao.getAc() );
    }

    private void deleteSearchItemForAlias( Alias alias ) {
        if ( log.isDebugEnabled() ) {
            log.debug( "Deleting SearchItems for Alias: " + alias.getName() + " (" + alias.getAc() + "); Parent AC: " + alias.getParentAc() );
        }

        if ( !isAliasSearchable( alias ) ) {
            return;
        }

        SearchItem searchItem = searchItemForAlias( alias );

        IntactContext.getCurrentInstance().getDataContext().getDaoFactory()
                .getSearchItemDao().delete( searchItem );
    }

    private void deleteSearchItemForXref( Xref xref ) {
        if ( log.isDebugEnabled() ) {
            log.debug( "Deleting SearchItems for Xref: " + xref.getPrimaryId() + " (" + xref.getAc() + "); Parent AC: " + xref.getParentAc() );
        }

        SearchItem searchItem = searchItemForXref( xref );

        IntactContext.getCurrentInstance().getDataContext().getDaoFactory()
                .getSearchItemDao().delete( searchItem );
    }

    private static List<SearchItem> searchItemsForAnnotatedObject( AnnotatedObject<? extends Xref, ? extends Alias> ao, boolean includeAliases ) {
        List<SearchItem> searchItems = new ArrayList<SearchItem>();
        searchItems.add( new SearchItem( ao.getAc(), ao.getAc(), ao.getClass().getName(), "ac" ) );
        searchItems.add( new SearchItem( ao.getAc(), ao.getShortLabel(), ao.getClass().getName(), "shortlabel" ) );

        if ( ao.getFullName() != null ) {
            searchItems.add( new SearchItem( ao.getAc(), ao.getFullName(), ao.getClass().getName(), "fullname" ) );
        }

        if ( includeAliases ) {
            for ( Alias alias : ao.getAliases() ) {
                if ( alias.getCvAliasType() != null ) {
                    searchItems.add( new SearchItem( ao.getAc(), alias.getName(), ao.getClass().getName(), alias.getCvAliasType().getShortLabel() ) );
                } else {
                    log.warn( "Couldn't insert SearchItem for Alias, as it cvAliasType is null: " + alias.getName() );
                }
            }
        }

        return searchItems;
    }

    private static SearchItem searchItemForAlias( Alias alias ) {
        return new SearchItem( alias.getParentAc(), alias.getName(),
                               alias.getParent().getClass().getName(), alias.getCvAliasType().getShortLabel() );
    }

    private static SearchItem searchItemForXref( Xref xref ) {
        return new SearchItem( xref.getParentAc(), xref.getPrimaryId(),
                               xref.getParent().getClass().getName(), "xref" );
    }

    private static boolean isAliasSearchable( Alias alias ) {
        return alias.getCvAliasType() != null;
    }
}
