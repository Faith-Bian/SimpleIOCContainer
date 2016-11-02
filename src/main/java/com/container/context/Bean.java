package com.container.context;

import com.container.context.exceptions.BeanCreationException;
import com.container.context.exceptions.DeniedBeanCreationException;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Bean instances are used to contain information about classes, marked as snowflakes.</p>
 * <p>The are responsible for snowflake instantiation and reporting.</p>
 */
public class Bean {
    private boolean copied;
    private boolean denied;
    private String report;
    private final String snowFlakeName;
    private final Class<?> beanClass;
    private Object beanInstance;


    /**
     * Constructs bean instance with specified snowflake name and class, that was marked with that annotation.
     * @param snowFlakeName name specified in the value of Snowflake annotation <b>Snowflake(snowflakeName = "Mindy")</b>.
     *                      Cannot be null.
     * @param beanClass class marked with Snowflake annotation. Cannot be null.
     */
    public Bean(String snowFlakeName, Class<?> beanClass) {
        if (snowFlakeName == null)
            throw new NullPointerException("Snowflake name is null!");
        if (beanClass == null)
            throw new NullPointerException("Bean class is null!");
        this.snowFlakeName = snowFlakeName;
        this.beanClass = beanClass;
    }

    /**
     *
     * @return Returns true if class is not a singleton.
     */
    public boolean isCopied() {
        return copied;
    }

    /**
     * Sets the
     * @param copied
     */
    public void setCopied(boolean copied) {
        this.copied = copied;
    }

    public boolean isDenied() {
        return denied;
    }

    public void setDenied(boolean denied) {
        this.denied = denied;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        if (report!=null && !report.isEmpty())
        this.report = report;
    }

    public Object createSnowflake() throws DeniedBeanCreationException, BeanCreationException {
        if (this.denied) throw new DeniedBeanCreationException();
        try {
            if (this.copied || beanInstance == null) {
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
