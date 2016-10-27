package com.container;

import com.container.annotations.Copied;
import com.container.annotations.Denied;
import com.container.annotations.Report;
import com.container.annotations.Snowflake;
import com.container.exceptions.BeanCreationException;
import com.container.exceptions.DeniedBeanCreationException;
import com.container.exceptions.SnowflakeDoesNotExistException;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

import static java.nio.file.FileVisitResult.CONTINUE;

public class W1nter {
    private HashSet<String> setOfPaths = new HashSet<String>();
    private HashMap<String,Bean> createdBeans = new HashMap<String,Bean>();


    public W1nter() {
    }
    public W1nter(String packagePath) throws BeanCreationException, IOException {
        if (packagePath == null )
            throw new BeanCreationException("Package path is empty!");
        instantiateBeans(packagePath);
        this.setOfPaths.add(packagePath);
    }
    public void addSnowflakes(String packagePath) throws BeanCreationException, IOException {
        if (packagePath == null || packagePath.isEmpty())
            throw new BeanCreationException("Package path is empty!");
        instantiateBeans(packagePath);
        this.setOfPaths.add(packagePath);
    }
    public Object getSnowflake(String snowflakeName) throws SnowflakeDoesNotExistException, DeniedBeanCreationException, BeanCreationException {
        if (!createdBeans.containsKey(snowflakeName))
            throw new SnowflakeDoesNotExistException("W1nter does not contain snowflake with name: " + snowflakeName);
        return createdBeans.get(snowflakeName).createSnowflake();
    }
    private void instantiateBeans(String packagePath) throws BeanCreationException, IOException {
        SnowflakeFinder snowflakeFinder = new SnowflakeFinder();
        snowflakeFinder.parseSnowflakes(packagePath);
    }


    private class Bean{
        private boolean copied;
        private boolean denied;
        private String report;
        private final String snowFlakeName;
        private final Class<?> beanClass;
        private Object beanInstance;

        private Bean(String snowFlakeName, Class<?> beanClass) {
            this.snowFlakeName = snowFlakeName;
            this.beanClass = beanClass;
        }
        private boolean isCopied() {
            return copied;
        }
        private void setCopied(boolean copied) {
            this.copied = copied;
        }
        private boolean isDenied() {
            return denied;
        }
        private void setDenied(boolean denied) {
            this.denied = denied;
        }
        private String getReport() {
            return report;
        }
        private void setReport(String report) {
            this.report = report;
        }
        private Object createSnowflake() throws DeniedBeanCreationException, BeanCreationException {
            if (this.denied) throw new DeniedBeanCreationException();
            try {
                if (this.copied || beanInstance == null) {
                        beanInstance = beanClass.newInstance();
                } else {
                        beanInstance = beanClass.newInstance();
                }
            }
            catch (InstantiationException | IllegalAccessException e) {
                throw new BeanCreationException(e);
            }
            System.err.format("Snowflake %s of class: %s was created!%n", snowFlakeName,beanClass.getCanonicalName());
            if (this.report != null && !this.report.isEmpty())
                reportSnowflake();
            return beanInstance;
        }
        private void reportSnowflake(){
            PrintWriter out = null;
            try {
                out = new PrintWriter(new BufferedWriter(new FileWriter(this.report)));
            out.format("Class:%n  %s%n%n", beanClass.getCanonicalName());
            out.format("Modifiers:%n  %s%n%n",
                    Modifier.toString(beanClass.getModifiers()));

            out.format("Type Parameters:%n");
            TypeVariable[] tv = beanClass.getTypeParameters();
            if (tv.length != 0) {
                out.format("  ");
                for (TypeVariable t : tv)
                    out.format("%s ", t.getName());
                out.format("%n%n");
            } else {
                out.format("  -- No Type Parameters --%n%n");
            }

            out.format("Implemented Interfaces:%n");
            Type[] intfs = beanClass.getGenericInterfaces();
            if (intfs.length != 0) {
                for (Type intf : intfs)
                    out.format("  %s%n", intf.toString());
                out.format("%n");
            } else {
                out.format("  -- No Implemented Interfaces --%n%n");
            }

            out.format("Inheritance Path:%n");
            List<Class> l = new ArrayList<Class>();
            printAncestor(beanClass, l);
            if (l.size() != 0) {
                for (Class<?> cl : l)
                    out.format("  %s%n", cl.getCanonicalName());
                out.format("%n");
            } else {
                out.format("  -- No Super Classes --%n%n");
            }

            out.format("Annotations:%n");
            Annotation[] ann = beanClass.getAnnotations();
            if (ann.length != 0) {
                for (Annotation a : ann)
                    out.format("  %s%n", a.toString());
                out.format("%n");
            } else {
                out.format("  -- No Annotations --%n%n");
            }
            } catch (IOException e) {
                System.out.println(e);
            }
            finally {
                if (out!=null)
                    out.close();
            }
        }
        private  void printAncestor(Class<?> c, List<Class> l) {
            Class<?> ancestor = c.getSuperclass();
            if (ancestor != null) {
                l.add(ancestor);
                printAncestor(ancestor, l);
            }
        }
    }
    private class SnowflakeFinder {
        private SnowflakeFinder(){}
        private  void parseSnowflakes(String path) throws IOException, BeanCreationException {
            ArrayList<String> listOfJavaFiles = findJavaFiles(path);
            findSnowflakes(listOfJavaFiles);
        }
        private  ArrayList<String> findJavaFiles(String path) throws IOException {
            Path startingDir = Paths.get(System.getProperty("user.dir") + System.getProperty("file.separator") + path);
            Finder finder = new Finder();
            Files.walkFileTree(startingDir, finder);
            return finder.done();
        }

        private  void findSnowflakes(List<String> listOfClasses) throws BeanCreationException {
            for (String s :
                    listOfClasses) {
                try {
                    Class beanClass = Class.forName(s);
                    Snowflake sn = (Snowflake) beanClass.getAnnotation(Snowflake.class);
                    if (sn == null)
                        continue;
                    String snowflakeName = sn.snowflakeName();
                    if (createdBeans.containsKey(snowflakeName))
                        throw new BeanCreationException("Snowflake with name " + snowflakeName + " already exists!");
                    Bean bean = new Bean(snowflakeName, beanClass);
                    Denied denied = (Denied) beanClass.getAnnotation(Denied.class);
                    if (denied !=null)
                        bean.setDenied(true);
                    Copied copied = (Copied) beanClass.getAnnotation(Copied.class);
                    if (copied != null)
                        bean.setCopied(true);
                    Report report = (Report) beanClass.getAnnotation(Report.class);
                    if (report!=null && !report.destinationFile().isEmpty())
                        bean.setReport(report.destinationFile());
                    createdBeans.put(snowflakeName,bean);
                }
                catch (ClassNotFoundException e)
                {
                    throw new RuntimeException("Something is wrong with source code!");
                }
            }
        }

        private  class Finder
                extends SimpleFileVisitor<Path> {
            private final PathMatcher matcher;
            private int numMatches = 0;
            private ArrayList<String> listOfclasses = new ArrayList<>();
            private String prop = System.getProperty("user.dir");

            private Finder(){
                matcher = FileSystems.getDefault()
                        .getPathMatcher("glob:" + "*.java");
            }

            // Compares the glob pattern against
            // the file or directory name.
            private void find(Path file) {
                Path name = file.getFileName();
                if (name != null && matcher.matches(name)) {
                    numMatches++;
                    listOfclasses.add(file.toString().substring(prop.length()+5).split(".java")[0].replace('/','.'));
                    System.out.println(file);
                }
            }

            // Prints the total number of
            // matches to standard out.
            private ArrayList<String> done() {
                System.out.println("Matched: "
                        + numMatches);
                return listOfclasses;
            }

            // Invoke the pattern matching
            // method on each file.
            @Override
            public FileVisitResult visitFile(Path file,
                                             BasicFileAttributes attrs) {
                find(file);
                return CONTINUE;
            }

            // Invoke the pattern matching
            // method on each directory.
            @Override
            public FileVisitResult preVisitDirectory(Path dir,
                                                     BasicFileAttributes attrs) {
                find(dir);
                return CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file,
                                                   IOException exc) {
                System.err.println(exc);
                return CONTINUE;
            }
        }
    }
}
