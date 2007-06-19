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
package uk.ac.ebi.intact.core.unit;

import org.junit.internal.runners.InitializationError;
import org.junit.internal.runners.TestClassRunner;
import org.junit.runner.Description;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;

import java.lang.reflect.Method;

/**
 * Runner for intact tests
 *
 * WARN: currently (idea7.0m1b), the listener is not correctly registered in IDEA, so the intact test configuration will be ignore
 * and tests could fail when being run in IDEA
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 */
public class IntactTestRunner extends TestClassRunner {

    private final static ThreadLocal<Method> testMethod = new ThreadLocal<Method>();

    protected static void setTestMethod(Method method) {
        testMethod.set(method);
    }

    public static Method getTestMethod() {
        return testMethod.get();
    }


    public IntactTestRunner(final Class<?> klass) throws InitializationError {
        super(klass);
    }

    @Override
    public void run(RunNotifier notifier) {
        notifier.addListener(new MethodListener());
        super.run(notifier);
    }


    private static class MethodListener extends RunListener {

        @Override
        public void testStarted(Description description) {
            if (description.isTest()) {
                String className = parseTestClassName(description);
                String methodName = parseTestMethodName(description);

                if (className != null && methodName != null) {
                    try {
                        Method method = Class.forName(className).getMethod(methodName, null);
                        testMethod.set(method);
                    } catch (Exception e) {
                        throw new IntactTestException(e);
                    }
                }
            }
        }

        private static String parseTestMethodName(Description description) {
            String name = description.getDisplayName();

            if (name != null) {
                int last = name.lastIndexOf('(');
                return last < 0 ? name : name.substring(0, last);
            }
            return null;
        }

        private static String parseTestClassName(Description description) {
            String name = description.getDisplayName();

            if (name != null) {
                int last = name.lastIndexOf('(');
                return last < 0 ? null : name.substring(last + 1, name.length() - 1);
            }
            return null;
        }
    }

}