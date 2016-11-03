/**
 * The package contains classes used to provide the functionality of W1nter container.
 */
package com.container;

import com.container.context.Bean;
import com.container.context.annotations.Copied;
import com.container.context.annotations.Denied;
import com.container.context.annotations.Report;
import com.container.context.annotations.Snowflake;
import com.container.context.exceptions.BeanCreationException;
import org.reflections.Reflections;
import java.util.Map;
import java.util.Set;

/**
 * The class is responsible for finding and creating snowflakes.
 */
class SnowflakeFinder {
    /**
     * The value is used to store snowflake name - bean pair.
     */
    private final Map<String, Bean> createdBeans;

    /**
     * @param createdBeans Should be a map representing Snowflake name(String) and corresponding bean instance.
     */
    SnowflakeFinder(Map<String, Bean> createdBeans) {
        this.createdBeans = createdBeans;
    }

    /**
     * Parses the path and finds snowflakes. Found snowflake will be created,
     * their names and instances will be added to the map specified int the constructor.
     * @param path Package path. (Example: "my.project")
     * @throws BeanCreationException will be throw if snowflakes cannot be instantiated.
     */
    void parseSnowflakes(String path) throws BeanCreationException {
        Set<Class<?>> setOfJavaFiles = findSnowflakes(path);
        createBeans(setOfJavaFiles);
    }

    /**
     * This method finds the classes marked with snowflake annotation.
     * @param path package path.
     * @return set of classes
     */
    private Set<Class<?>> findSnowflakes(String path) {
        return new Reflections(path).getTypesAnnotatedWith(Snowflake.class);
    }

    /**
     * This method is responsible for bean instantiation for each class in the specified set.
     * @param setOfClasses the set of classes to be created.
     * @throws BeanCreationException will be thrown if bean cannot be instantiated.
     */
    private  void createBeans(Set<Class<?>> setOfClasses) throws BeanCreationException {
        for (Class beanClass : setOfClasses) {
                Snowflake sn = (Snowflake) beanClass.getAnnotation(Snowflake.class);
                String snowflakeName = sn.snowflakeName();
                if (createdBeans.containsKey(snowflakeName)) {
                    throw new BeanCreationException("Snowflake with name " + snowflakeName + " already exists!");
                }
                Bean bean = new Bean(snowflakeName, beanClass);
                Denied denied = (Denied) beanClass.getAnnotation(Denied.class);
                if (denied != null) {
                    bean.setDenied(true);
                }
                Copied copied = (Copied) beanClass.getAnnotation(Copied.class);
                if (copied != null) {
                    bean.setCopied(true);
                }
                Report report = (Report) beanClass.getAnnotation(Report.class);
                if (report != null && !report.destinationFile().isEmpty()) {
                    bean.setReport(report.destinationFile());
                }
                createdBeans.put(snowflakeName, bean);
        }
    }
}

