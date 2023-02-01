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
package uk.ac.ebi.intact.core.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.type.AbstractStandardBasicType;

/**
 * Utilities to help with the login
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class LogUtils {

    private static final Log log = LogFactory.getLog(LogUtils.class);

    private static final Logger[] LOG_HIBERNATE_SQL = { LogManager.getLogger("org.hibernate.SQL"),
                                                        LogManager.getLogger("org.hibernate.type"),
                                                        LogManager.getLogger(AbstractStandardBasicType.class)};

    /**
     * Whether to log or not SQL statements
     *
     * @param showSql
     */
    public static void setPrintSql(boolean showSql) {
        if (showSql) {
            for (Logger logger : LOG_HIBERNATE_SQL) {
                setLoggerLevel(logger, Level.DEBUG);
            }
        } else {
            for (Logger logger : LOG_HIBERNATE_SQL) {
                setLoggerLevel(logger, Level.INFO);
            }
        }
    }

    /**
     * Change the output level of a log
     *
     * @param loggerName
     * @param level
     */
    public static void setLoggerLevel(String loggerName, Level level) {
        Logger logger = LogManager.getLogger(loggerName);

        if (logger == null && log.isWarnEnabled()) {
            log.warn("Trying to change level for log, but logger was null: "+loggerName);
        }

        setLoggerLevel(logger, level);
    }

    /**
     * Change the output level of a log
     *
     * @param level
     */
    public static void setLoggerLevel(Logger logger, Level level) {
        if (logger != null) {
            if (log.isInfoEnabled()) log.info("Changing log level: " + logger.getName() + " to " + level.toString());

            logger.setLevel(level);
        } else {
            log.warn("Trying to change level for log, but logger was null");
        }
    }

}