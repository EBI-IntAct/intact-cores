/**
 * Copyright 2008 The European Bioinformatics Institute, and others.
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
package uk.ac.ebi.intact.core.util;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.net.URLDecoder;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

/**
 * Allows resource to be loaded properly even if they are located in a file of which the path contains spaces
 * (translated into %20).  
 *
 * @author Samuel Kerrien (skerrien@ebi.ac.uk)
 * @version $Id$
 * @since 1.8.1
 */
public class URLDecoderResourceLoader extends PathMatchingResourcePatternResolver {

    @Override
    public Resource getResource( String location ) {
        try {
            return super.getResource( URLDecoder.decode( location, "UTF-8" ) );
        } catch ( UnsupportedEncodingException e ) {
            throw new RuntimeException( e );
        }
    }

    @Override
    public Resource[] getResources( String locationPattern ) throws IOException {
        Resource[] resources = super.getResources( locationPattern );

        List<Resource> decodedResources = new ArrayList<Resource>( resources.length );
        for ( int i = 0; i < resources.length; i++ ) {
            Resource resource = resources[i];
            String decodedResource = URLDecoder.decode( resource.getURL().toString(), "UTF-8" );
            decodedResources.add( getResourceLoader().getResource(decodedResource) );
        }

        return decodedResources.toArray( new Resource[resources.length] );
    }
}
