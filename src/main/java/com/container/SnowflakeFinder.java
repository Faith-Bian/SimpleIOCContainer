package com.container;

import com.container.context.Bean;
import com.container.context.annotations.Copied;
import com.container.context.annotations.Denied;
import com.container.context.annotations.Report;
import com.container.context.annotations.Snowflake;
import com.container.context.exceptions.BeanCreationException;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.nio.file.FileVisitResult.CONTINUE;

class SnowflakeFinder {
    private final Map<String, Bean> createdBeans;


    SnowflakeFinder(Map<String, Bean> createdBeans){
        this.createdBeans = createdBeans;
    }

    void parseSnowflakes(String path) throws IOException, BeanCreationException {
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
