package com.container;

import com.container.context.Bean;
import com.container.context.annotations.Copied;
import com.container.context.annotations.Denied;
import com.container.context.annotations.Report;
import com.container.context.annotations.Snowflake;
import com.container.context.exceptions.BeanCreationException;
import org.reflections.Reflections;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

class SnowflakeFinder {
    private final Map<String, Bean> createdBeans;


    SnowflakeFinder(Map<String, Bean> createdBeans){
        this.createdBeans = createdBeans;
    }

    void parseSnowflakes(String path) throws IOException, BeanCreationException {
        Set<Class<?>> setOfJavaFiles = findSnowflakes(path);
        createBeans(setOfJavaFiles);
    }

    private Set<Class<?>> findSnowflakes(String path) throws IOException {
        return new Reflections(path).getTypesAnnotatedWith(Snowflake.class);
    }

    private  void createBeans(Set<Class<?>> setOfClasses) throws BeanCreationException {
        for ( Class beanClass : setOfClasses) {
                Snowflake sn = (Snowflake) beanClass.getAnnotation(Snowflake.class);
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
    }
}

