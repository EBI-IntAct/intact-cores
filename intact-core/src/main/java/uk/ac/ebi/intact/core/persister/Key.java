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
package uk.ac.ebi.intact.core.persister;

/**
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
class Key {

    private String uniqueString;

    Key(String uniqueString) {
        if (uniqueString == null) {
            throw new NullPointerException("uniqueString must not be null");
        }
        this.uniqueString = uniqueString;
    }

    public String getUniqueString() {
        return uniqueString;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Key key = (Key) o;

        if (!uniqueString.equals(key.uniqueString)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return uniqueString.hashCode();
    }

    @Override
    public String toString() {
        return uniqueString;
    }
}