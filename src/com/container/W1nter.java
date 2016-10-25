package com.container;

import com.container.exceptions.BeanCreationException;
import com.container.exceptions.DeniedBeanCreationException;
import com.container.exceptions.SnowflakeDoesNotExistException;

import java.util.HashMap;
import java.util.HashSet;

public class W1nter {
    private HashSet<String> setOfPaths = new HashSet<String>();
    private HashMap<String,Bean> createdBeans = new HashMap<String,Bean>();


    public W1nter() {
    }
    public W1nter(String packagePath) throws BeanCreationException {
        if (packagePath == null || packagePath.isEmpty())
            throw new BeanCreationException("Package path is empty!");
        instantiateBeans(packagePath);
        this.setOfPaths.add(packagePath);
    }
    public void addSnowflakes(String packagePath) throws BeanCreationException{
        if (packagePath == null || packagePath.isEmpty())
            throw new BeanCreationException("Package path is empty!");
        instantiateBeans(packagePath);
        this.setOfPaths.add(packagePath);
    }
    public Object getSnowflake(String snowflake) throws SnowflakeDoesNotExistException, DeniedBeanCreationException {
        if (!createdBeans.containsKey(snowflake))
            throw new SnowflakeDoesNotExistException("W1nter does not contain snowflake with name: " + snowflake);
        return createdBeans.get(snowflake).createSnowflake();
    }
    private void instantiateBeans(String packagePath) throws BeanCreationException{
    }


    private class Bean{
        private boolean copied;
        private boolean denied;
        private String report;
        private final String snowFlakeName;

        private Bean(String snowFlakeName) {
            this.snowFlakeName = snowFlakeName;
        }
        private Bean(String report, String snowFlakeName) {
            this.report = report;
            this.snowFlakeName = snowFlakeName;
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
        private Object createSnowflake() throws DeniedBeanCreationException {
            if (this.denied) throw new DeniedBeanCreationException();
            return null;
        }
    }
}
