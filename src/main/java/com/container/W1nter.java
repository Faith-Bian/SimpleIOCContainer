package com.container;

import com.container.context.Bean;
import com.container.context.exceptions.BeanCreationException;
import com.container.context.exceptions.DeniedBeanCreationException;
import com.container.context.exceptions.SnowflakeDoesNotExistException;

import java.io.*;
import java.util.*;


public class W1nter {
    private HashSet<String> setOfPaths = new HashSet<String>();
    private HashMap<String,Bean> createdBeans = new HashMap<String,Bean>();


    public W1nter() {
    }

    public W1nter(String packagePath) throws BeanCreationException, IOException {
        if (packagePath == null )
            throw new NullPointerException("Package path is null!");
        this.setOfPaths.add(packagePath);
        instantiateBeans(packagePath);
    }

    public Set<String> getSetOfPaths() {
        return Collections.unmodifiableSet(setOfPaths);
    }

    public Map<String, Bean> getCreatedBeans() {
        return Collections.unmodifiableMap(createdBeans);
    }

    public void addSnowflakes(String packagePath) throws BeanCreationException, IOException {
        if (packagePath == null)
            throw new NullPointerException("Package path is null!");
        this.setOfPaths.add(packagePath);
        instantiateBeans(packagePath);
    }

    public Object getSnowflake(String snowflakeName) throws SnowflakeDoesNotExistException, DeniedBeanCreationException, BeanCreationException {
        if (snowflakeName == null)
            throw new BeanCreationException();
        if (!createdBeans.containsKey(snowflakeName))
            throw new SnowflakeDoesNotExistException("W1nter does not contain snowflake with name: " + snowflakeName);
        return createdBeans.get(snowflakeName).createSnowflake();
    }

    private void instantiateBeans(String packagePath) throws BeanCreationException, IOException {
        SnowflakeFinder snowflakeFinder = new SnowflakeFinder(createdBeans);
        snowflakeFinder.parseSnowflakes(packagePath);
    }
}
