/**
 * Copyright (c) 2002-2006 The European Bioinformatics Institute, and others.
 * All rights reserved. Please see the file LICENSE
 * in the root directory of this distribution.
 */
package uk.ac.ebi.intact.annotation.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import sun.misc.URLClassPath;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Utilities to deal with annotations
 *
 * @author Bruno Aranda (baranda@ebi.ac.uk)
 * @version $Id$
 * @since <pre>29-Jun-2006</pre>
 */
public class AnnotationUtil {

    private static final Log log = LogFactory.getLog(AnnotationUtil.class);
    private static final String FILE_SEPARATOR = System.getProperty("file.separator");

    /**
     * Gathers a list of classes with a defined Annotation
     *
     * @param annotationClass The annotation to look for
     * @param jarPath         The path to the jar
     *
     * @return The list of classes with the annotation
     *
     * @throws IOException thrown if something goes wrong when reading the jar
     */
    public static Collection<Class> getClassesWithAnnotationFromJar(Class<? extends Annotation> annotationClass, String jarPath) throws IOException {
        return getClassesWithAnnotationFromJar(annotationClass, jarPath, null, null);
    }

    /**
     * Gathers a list of classes with a defined Annotation
     *
     * @param annotationClass The annotation to look for
     * @param jarPath         The path to the jar
     *
     * @return The list of classes with the annotation
     *
     * @throws IOException thrown if something goes wrong when reading the jar
     */
    public static Collection<Class> getClassesWithAnnotationFromJar(Class<? extends Annotation> annotationClass, String jarPath, ClassLoader classLoader) throws IOException {
        return getClassesWithAnnotationFromJar(annotationClass, jarPath, null, classLoader);
    }

    /**
     * Gathers a list of classes with a defined Annotation in a package
     *
     * @param annotationClass The annotation to look for
     * @param jarPath         The path to the jar
     *
     * @return The list of classes with the annotation
     *
     * @throws IOException thrown if something goes wrong when reading the jar
     */
    public static Collection<Class> getClassesWithAnnotationFromJar(Class<? extends Annotation> annotationClass, String jarPath, String packageName) throws IOException {
        return getClassesWithAnnotationFromJar(annotationClass, jarPath, packageName, null);
    }

    /**
     * Gathers a list of classes with a defined Annotation in a package
     *
     * @param annotationClass The annotation to look for
     * @param jarPath         The path to the jar
     *
     * @return The list of classes with the annotation
     *
     * @throws IOException thrown if something goes wrong when reading the jar
     */
    public static Collection<Class> getClassesWithAnnotationFromJar(Class<? extends Annotation> annotationClass, String jarPath, String packageName, ClassLoader classLoader) throws IOException {
        Set<Class> annotatedClasses = new HashSet<Class>();
        JarFile jarFile = new JarFile( URLDecoder.decode( jarPath, "UTF-8" ) );
        Enumeration<JarEntry> e = jarFile.entries();

        while (e.hasMoreElements()) {
            JarEntry entry = e.nextElement();

            Class clazz = getAnnotatedClass(annotationClass, entry.getName(), classLoader);

            if (clazz != null) {

                if (packageName != null && clazz.getPackage().getName().equals(packageName)) {
                    annotatedClasses.add(clazz);
                } else if (packageName == null) {
                    annotatedClasses.add(clazz);
                }
            }
        }

        jarFile.close();

        return annotatedClasses;
    }

    /**
     * Returns the classes contained in those directories present in the classpath
     *
     * @param annotationClass annotation to look for
     *
     * @return Classes containing the annotation
     */
    public static Collection<Class> getClassesWithAnnotationFromClasspathDirs(Class<? extends Annotation> annotationClass) {
        //recover the classpath
        Collection<String> classpathItems = getClasspathElements();
        classpathItems.addAll(getDirsFromStackTrace());

        Set<Class> annotatedClasses = new HashSet<Class>();

        for (String classpathItem : classpathItems) {
            File ciFile = new File(classpathItem);
            
            if (ciFile.isDirectory()) {
                annotatedClasses.addAll(getClassesWithAnnotationFromDir(annotationClass, ciFile));
            }
        }

        return annotatedClasses;
    }

    /**
     * Returns the classes contained in the classpath
     * NOTE: this method is not recommended as it can take a long time
     *
     * @param annotationClass annotation to look for
     *
     * @return Classes containing the annotation
     */
    public static Collection<Class> getClassesWithAnnotationFromClasspath(Class<? extends Annotation> annotationClass) throws IOException {
        Set<Class> annotatedClasses = new HashSet<Class>();
        annotatedClasses.addAll(getClassesWithAnnotationFromClasspathJars(annotationClass));
        annotatedClasses.addAll(getClassesWithAnnotationFromClasspathDirs(annotationClass));
        return annotatedClasses;
    }

    /**
     * Returns the classes contained in those jars present in the classpath
     * NOTE: this method is not recommended as it can take a long time
     *
     * @param annotationClass annotation to look for
     *
     * @return Classes containing the annotation
     */
    public static Collection<Class> getClassesWithAnnotationFromClasspathJars(Class<? extends Annotation> annotationClass) throws IOException {
        //recover the classpath
        Collection<String> classpathItems = getClasspathElements();

        Set<Class> annotatedClasses = new HashSet<Class>();

        for (String classpathItem : classpathItems) {
            File ciFile = new File(classpathItem);

            if (!ciFile.isDirectory() && classpathItem.endsWith(".jar")) {
                annotatedClasses.addAll(getClassesWithAnnotationFromJar(annotationClass, ciFile.toString()));
            }
        }

        return annotatedClasses;
    }

    /**
     * Returns the classes contained the annotation in a directory. It searches the subdirectories recursively
     *
     * @param annotationClass annotation to look for
     * @param dir             Directory to use, recursive search in its subdirectories
     *
     * @return Classes containing the annotation
     */
    public static Collection<Class> getClassesWithAnnotationFromDir(Class<? extends Annotation> annotationClass, File dir) {
        return getClassesWithAnnotationFromDir(annotationClass, dir, dir, Thread.currentThread().getContextClassLoader());
    }

    /**
     * Returns the classes contained the annotation in a directory. It searches the subdirectories recursively
     *
     * @param annotationClass annotation to look for
     * @param dir             Directory to use, recursive search in its subdirectories
     *
     * @return Classes containing the annotation
     */
    public static Collection<Class> getClassesWithAnnotationFromDir(Class<? extends Annotation> annotationClass, File dir, ClassLoader classLoader) {
        return getClassesWithAnnotationFromDir(annotationClass, dir, dir, classLoader);
    }

    private static Collection<Class> getClassesWithAnnotationFromDir(Class<? extends Annotation> annotationClass, File dir, File parentDir, ClassLoader classLoader) {
        Set<Class> classesFromDir = new HashSet<Class>();

        File[] classFiles = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".class");
            }
        });

        for (File classFile : classFiles) {
            String classFileWithoutDir = classFile.toString().substring(parentDir.toString().length());
            Class annotatedClass = AnnotationUtil.getAnnotatedClass(annotationClass, classFileWithoutDir, classLoader);

            if (annotatedClass != null) {
                classesFromDir.add(annotatedClass);
               }
        }

        File[] subdirs = dir.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });

        for (File subdir : subdirs) {
            Collection<Class> classesFromSubdir = getClassesWithAnnotationFromDir(annotationClass, subdir, parentDir, classLoader);
            classesFromDir.addAll(classesFromSubdir);
        }

        return classesFromDir;
    }

    /**
     * Returns the Class if the provided String is a FQN class that contains the annotation
     *
     * @param annotationClass The annotation to look for
     * @param classFilename   The fully qualified name of the class as a String
     *
     * @return the Class if contains the annotation, otherwise returns null.
     */
    public static Class getAnnotatedClass(Class<? extends Annotation> annotationClass, String classFilename) {
        return getAnnotatedClass(annotationClass, classFilename, null);
    }


    /**
     * Returns the Class if the provided String is a FQN class that contains the annotation
     *
     * @param annotationClass The annotation to look for
     * @param classFilename   The fully qualified name of the class as a String
     *
     * @return the Class if contains the annotation, otherwise returns null.
     */
    public static Class getAnnotatedClass(Class<? extends Annotation> annotationClass, String classFilename, ClassLoader classLoader) {
        if (classFilename.endsWith(".class")) {
            String fileDir;
            String className;

            // windows conversion
            classFilename = classFilename.replaceAll("\\\\", "/");

            if (classFilename.contains("/")) {
                fileDir = classFilename.substring(0, classFilename.lastIndexOf("/"));
                className = classFilename.substring(classFilename.lastIndexOf("/") + 1, classFilename.indexOf(".class"));
            } else {
                fileDir = "";
                className = classFilename;
            }

            String packageName = fileDir.replaceAll("/", ".");

            if (packageName.startsWith("."))
                packageName = packageName.substring(1, fileDir.length());

            try {
                // Try to get the class from the entry
                String completeClassName = packageName + "." + className;
                Class clazz;

                if (classLoader != null) {
                    clazz = classLoader.loadClass(completeClassName);
                } else {
                    clazz = Class.forName(completeClassName);
                }

                if (isAnnotationPresent(clazz, annotationClass)) {
                    return clazz;
                }

            } catch (Throwable e) {
                log.debug("Error loading class " + packageName + "." + className + ": " + e);
            }
        }

        // if the file does not have the annotation return null
        return null;
    }

    /**
     * @since 1.6
     */
    public static boolean isAnnotationPresent(Class clazz, Class<? extends Annotation> annotClass) {
        if (clazz == null) {
            return false;
        }

        for (Annotation a : clazz.getAnnotations())
        {
            if (a.annotationType().equals(annotClass)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Gets the fields of a class with a certain annotation
     */
    public static List<Field> fieldsWithAnnotation(Class clazz, Class<? extends Annotation> annotationClass) {
        List<Field> fields = new ArrayList<Field>();

        for (Field field : clazz.getFields()) {
            if (field.getAnnotation(annotationClass) != null) {
                fields.add(field);
            }
        }

        return fields;
    }

    /**
     * Gets the declared fields of a class with a certain annotation
     */
    public static List<Field> declaredFieldsWithAnnotation(Class clazz, Class<? extends Annotation> annotationClass) {
        List<Field> fields = new ArrayList<Field>();

        for (Field field : clazz.getDeclaredFields()) {
            if (field.getAnnotation(annotationClass) != null) {
                fields.add(field);
            }
        }

        return fields;
    }

    /**
     * Gets the methods of a class with a certain annotation
     */
    public static List<Method> methodsWithAnnotation(Class clazz, Class<? extends Annotation> annotationClass) {
        List<Method> methods = new ArrayList<Method>();

        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getAnnotation(annotationClass) != null) {
                methods.add(method);
            }
        }

        return methods;
    }


    /**
     * Gets the declared methods of a class with a certain annotation
     */
    public static List<Method> declaredMethodsWithAnnotation(Class clazz, Class<? extends Annotation> annotationClass) {
        List<Method> methods = new ArrayList<Method>();

        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getAnnotation(annotationClass) != null) {
                methods.add(method);
            }
        }

        return methods;
    }

    private static Collection<String> getClasspathElements() {
        String classPath = System.getProperty("java.class.path");

        URL[] classpathElements = URLClassPath.pathToURLs(classPath);

        Set<String> classPathItems = new HashSet<String>();

        for (URL cpElem : classpathElements) {
            classPathItems.add(cpElem.getFile());
        }

        return classPathItems;
    }

    /**
     * Goes through the stacktrace to get the dirs where the classes are (when they are not in a jar)
     */
    private static Collection<String> getDirsFromStackTrace() {
        Set<String> dirs = new HashSet<String>();

        for (StackTraceElement ste : Thread.currentThread().getStackTrace())
        {
            String className = ste.getClassName();
            Class clazz = null;

            try {
                clazz = Class.forName(className);

                String resClass = "/"+className.replaceAll("\\.","/")+".class";

                URL resUrl = clazz.getResource(resClass);

                String completeDir = null;
                if (resUrl != null) {
                    completeDir = resUrl.getFile();

                    if (!completeDir.contains(".jar!")) {
                        String dir = completeDir.substring(0, completeDir.length() - resClass.length());

                        if (dir != null && new File(dir).isDirectory()) {
                            dirs.add(dir);
                        }
                    }
                }


            } catch (ClassNotFoundException e) {
                // nothing
            }
        }

        return dirs;
    }
    
}
