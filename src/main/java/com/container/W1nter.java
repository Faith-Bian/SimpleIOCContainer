/**
 * The package contains classes used to provide the functionality of W1nter container.
 */
package com.container;

import com.container.context.Bean;
import com.container.context.exceptions.BeanCreationException;
import com.container.context.exceptions.DeniedBeanCreationException;
import com.container.context.exceptions.SnowflakeDoesNotExistException;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;


/**
 * W1nter class is a primitive IoC/DI container that can create classes marked with Snowflake annotation.
 * For example:
 * <pre>
 *     {@code com.animal}
 *     {@code @Snowflake(snowflakeName = "Mindy")}
 *     {@code public class Fox }
 *          {@code public int age = 25;}
 * </pre>
 *
 * We can do the following:
 * <pre>
 *      {@code Fox mindy = (Fox) winter.getSnowflake("Mindy");}
 *      {@code System.out.println(mindy.age); // prints 25}
 * </pre>
 */
public class W1nter {
    /**
     * The value is used to store all the paths that were added to W1nter container.
     */
    private HashSet<String> setOfPaths = new HashSet<String>();
    /**
     * The value is used to store snowflake names and corresponding bean instances.
     * (Bean instances are not the instances of class marked with @Snowflake.
     * Bean instances are used to store information about snowflakes
     * and are responsible for creation of instances of classes
     * marked as snowflakes.)
     */
    private HashMap<String, Bean> createdBeans = new HashMap<String, Bean>();

    /**
     * Default constructor.
     */
    public W1nter() {
    }

    /**
     * W1nter container created with this constructor will search "Snowflake" classes in the specified path.
     * @param packagePath package path. If null, NullPointerException will be thrown.
     * @throws BeanCreationException will be thrown if constructor cannot instantiate "snowflake" classes.
     */
    public W1nter(String packagePath) throws BeanCreationException {
        if (packagePath == null) {
            throw new NullPointerException("Package path is null!");
        }
        this.setOfPaths.add(packagePath);
        instantiateBeans(packagePath);
    }

    /**
     * @return unmodifiable set of paths that were specified for this instance of W1nter container
     */
    public Set<String> getSetOfPaths() {
        return Collections.unmodifiableSet(setOfPaths);
    }

    /**
     * @return unmodifiable map with bean name - bean instance pair.
     */
    public Map<String, Bean> getCreatedBeans() {
        return Collections.unmodifiableMap(createdBeans);
    }

    /**
     * Adds directory that may contain classes marked with snowflake annotation.
     * @param packagePath path to be added. If Null, NullPointerException will be thrown.
     * @throws BeanCreationException will be thrown if W1nter container cannot instantiate beans.
     */
    public void addSnowflakes(String packagePath) throws BeanCreationException {
        if (packagePath == null) {
            throw new NullPointerException("Package path is null!");
        }
        this.setOfPaths.add(packagePath);
        instantiateBeans(packagePath);
    }

    /**
     * Returns an instance of class that was marked with snowflake annotation by snowflake name.
     * @param snowflakeName name specified in the snowflake annotation. For example:
     *                      <pre>
     *                      {@code @Snowflake (snowflakeName = "Mindy)}
     *                      </pre>
     *                      If snowflake name is null. NullPointerException wil be thrown.
     * @return an instance of class
     * @throws SnowflakeDoesNotExistException will be thrown if W1nter container does not contain
     * snowflake with name specified in the parameter.
     * @throws DeniedBeanCreationException wil be thrown if class is marked with @Denied annotation.
     * @throws BeanCreationException will be thrown if container cannot instantiate beans.
     */
    public Object getSnowflake(String snowflakeName) throws SnowflakeDoesNotExistException,
                                                    DeniedBeanCreationException, BeanCreationException {
        if (snowflakeName == null) {
            throw new BeanCreationException();
        }
        if (!createdBeans.containsKey(snowflakeName)) {
            throw new SnowflakeDoesNotExistException("W1nter does not contain snowflake with name: " + snowflakeName);
        }
        return createdBeans.get(snowflakeName).createSnowflake();
    }

    /**
     * This method finds and intantiates beans.
     * @param packagePath path of the package.
     * @throws BeanCreationException will be thrown if container cannot instantiate bean.
     */
    private void instantiateBeans(String packagePath) throws BeanCreationException {
        SnowflakeFinder snowflakeFinder = new SnowflakeFinder(createdBeans);
        snowflakeFinder.parseSnowflakes(packagePath);
    }
}
