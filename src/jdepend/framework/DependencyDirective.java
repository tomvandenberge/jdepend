package jdepend.framework;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A <code>DependencyDirective</code> represents a set of dependency
 * instructions to which analysed code can be compared.
 * <p>
 * Concrete subclasses implement the comparing functionality.
 * 
 * @author Tom van den Berge
 */
public abstract class DependencyDirective {

    protected Map<String, JavaPackage> packages = new HashMap<String, JavaPackage>();
    protected final Set<JavaPackage> packageFilter;
    protected MatchMode matchMode = MatchMode.BOTH; 

    protected enum MatchMode {
    	AFFERENT_ONLY,
    	EFFERENT_ONLY,
    	BOTH
    }
    
    /**
     * The created directive applies to all analysed classes.
     */
    protected DependencyDirective() {
    	// No filtering.
    	this.packageFilter = null;
    }
    
	/**
	 * The created directive only applies to analysed classes specified by the
	 * package filter.
	 */
    protected DependencyDirective(Set<JavaPackage> packageFilter) {
    	this.packageFilter = new HashSet<JavaPackage>(packageFilter);
    }

    /**
	 * This directive will verify afferent couplings only. Efferent couplings
	 * will be ignored.
	 */
    public void matchAfferentOnly() {
    	this.matchMode = MatchMode.AFFERENT_ONLY;
    }
    
    /**
	 * This directive will verify efferent couplings only. Afferent couplings
	 * will be ignored.
	 */
    public void matchEfferentOnly() {
    	this.matchMode = MatchMode.EFFERENT_ONLY;
    }
    
    /**
	 * This directive will verify all couplings (afferent and efferent). This is
	 * the default setting.
	 */
    public void matchAll() {
    	this.matchMode = MatchMode.BOTH;
    }
    
    /**
	 * Adds the specified package to this directive. The returned JavaPackage
	 * can be used to couple to other packages.
	 * 
	 * @param packageName
	 *            the name of the package.
	 * @return the added package.
	 */
    public JavaPackage addPackage(String packageName) {
        JavaPackage jPackage = (JavaPackage) packages.get(packageName);
        if (jPackage == null) {
            jPackage = new JavaPackage(packageName);
            addPackage(jPackage);
        }
        return jPackage;
    }

    /**
     * Adds the specified package to this directive.
     * 
     * @param jPackage the package to add.
     */
    public void addPackage(JavaPackage jPackage) {
        if (!packages.containsValue(jPackage)) {
            packages.put(jPackage.getName(), jPackage);
        }
    }

	/**
	 * Returns all configured packages of this directive.
	 * 
	 * @return the packages.
	 */
    public Collection<JavaPackage> getPackages() {
        return packages.values();
    }

    /**
     * Returns true if the specified packages follow this directive.
     * 
     * @param packages the packages to verify against this directive.
     * @return true if the packages follow this directive, otherwise false.
     */
    public abstract boolean followsDirective(Collection<JavaPackage> packages);
    
    /**
	 * Returns the packages from {@code packages} that match the configured
	 * package filter. If no package filter is configured, the specified
	 * collection is returned.
	 * 
	 * @param packages the packages to filter.
	 * @return the packages that match the package filter, or {@code packages}
	 *         if no package filter is configured.
	 */
    protected Collection<JavaPackage> applyPackageFilter(Collection<JavaPackage> packages) {
    	return packageFilter == null ? packages : filter(packages, packageFilter);
    }
    
    /**
     * Returns all {@code packages} specified in {@code filter}. Only the name is used to compare the packages.
     *  
     * @param packages the packages to be filtered.
     * @param filter the packages to pass.
     * @return all packages with names specified in {@code filters}.
     */
    protected static Collection<JavaPackage> filter(Collection<JavaPackage> packages, Collection<JavaPackage> filter) {
    	List<JavaPackage> residue = new ArrayList<JavaPackage>(packages);
    	residue.retainAll(filter);
    	return residue;
    }
}
