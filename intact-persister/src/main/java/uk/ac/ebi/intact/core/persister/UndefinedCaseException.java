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
 * Used to throw an exception when a case is possible but not behaviour logic has been implemented to handle it
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class UndefinedCaseException extends RuntimeException {

    public UndefinedCaseException() {
    }

    public UndefinedCaseException(Throwable cause) {
        super(cause);
    }

    public UndefinedCaseException(String message) {
        super("Case not defined: "+message);
    }

    public UndefinedCaseException(String message, Throwable cause) {
        super("Case not defined: "+message, cause);
    }
}