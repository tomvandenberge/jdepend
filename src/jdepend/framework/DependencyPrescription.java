package jdepend.framework;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * The <code>DependencyPrescription</code> class is a constraint that tests
 * whether two package-dependency graphs are equivalent.
 * <p>
 * This class is useful for writing package dependency assertions (e.g. JUnit).
 * For example, the following JUnit test will ensure that the 'ejb' and 'web'
 * packages only depend upon the 'util' package, and no others:
 * <p>
 * <blockquote>
 * 
 * <pre>
 * 
 * public void testDependencyConstraint() {
 * 
 *     JDepend jdepend = new JDepend();
 *     jdepend.addDirectory(&quot;/path/to/classes&quot;);
 *     Collection analyzedPackages = jdepend.analyze();
 * 
 *     DependencyPrescription constraint = new DependencyPrescription();
 * 
 *     JavaPackage ejb = constraint.addPackage(&quot;com.xyz.ejb&quot;);
 *     JavaPackage web = constraint.addPackage(&quot;com.xyz.web&quot;);
 *     JavaPackage util = constraint.addPackage(&quot;com.xyz.util&quot;);
 * 
 *     ejb.dependsUpon(util);
 *     web.dependsUpon(util);
 * 
 *     assertEquals(&quot;Dependency mismatch&quot;, true, constraint
 *             .followsDirective(analyzedPackages));
 * }
 * </pre>
 * 
 * </blockquote>
 * </p>
 * 
 * @author <b>Mike Clark</b> 
 * @author Clarkware Consulting, Inc.
 */
public class DependencyPrescription extends DependencyDirective {

	/**
	 * Creates a new prescription without a package filter. All dependencies for
	 * all analysed packages must be prescribed.
	 */
	public DependencyPrescription() {
		super();
	}
	
	/**
	 * Creates a new prescription with a package filter. Only the dependencies
	 * (both afferent and efferent) for the classes in the filter must be
	 * prescribed.
	 * 
	 * @param packageFilter the packages for which the dependencies are
	 *            prescribed.
	 */
	public DependencyPrescription(Set<JavaPackage> packageFilter) {
		super(packageFilter);
	}
	
	/**
	 * Indicates whether the specified packages match the packages in this
	 * prescription.
	 * 
	 * @return <code>true</code> if the packages match this prescription.
	 */
	@Override
	public boolean followsDirective(Collection<JavaPackage> analysedPackages) {
		if (packageFilter == null && analysedPackages.size() != packages.size()) {
			return false;
		}
		
		Collection<JavaPackage> packages = applyPackageFilter(analysedPackages);
		if (packages.isEmpty()) {
			return false;
		}
		
		for (JavaPackage pkg : packages) {
			if (!matchPackage(pkg)) {
				return false;
			}
		}
		return true;

	}

	private boolean matchPackage(JavaPackage analysedPackage) {

		JavaPackage prescribedPackage = (JavaPackage) packages.get(analysedPackage
				.getName());

		if (prescribedPackage != null) {
			if (equalsDependencies(prescribedPackage, analysedPackage)) {
				return true;
			}
		}

		return false;
	}

	private boolean equalsDependencies(JavaPackage a, JavaPackage b) {
		if (this.matchMode == MatchMode.AFFERENT_ONLY) {
			return equalsAfferents(a, b);
		} else if (this.matchMode == MatchMode.EFFERENT_ONLY) {
			return equalsEfferents(a, b);
		}
		return equalsAfferents(a, b) && equalsEfferents(a, b);
	}

	private boolean equalsAfferents(JavaPackage a, JavaPackage b) {
		if (!a.equals(b)) {
			return false;
		}
		return equalsDependencies(a.getAfferents(), b.getAfferents());
	}

	static boolean equalsEfferents(JavaPackage a, JavaPackage b) {
		if (!a.equals(b)) {
			return false;
		}
		return equalsDependencies(a.getEfferents(), b.getEfferents());
	}

	private static boolean equalsDependencies(Collection<JavaPackage> packagesA, Collection<JavaPackage> packagesB) {
		Set<String> components = new HashSet<String>();
		components.addAll(getComponents(packagesA));
		components.addAll(getComponents(packagesB));
		
		Collection<String> aDependencies = compressToComponents(packagesA, components);
		Collection<String> bDependencies = compressToComponents(packagesB, components);

		return aDependencies.equals(bDependencies);
	}
	
	static Set<String> getComponents(Collection<JavaPackage> packages) {
		Set<String> components = new HashSet<String>();
		for (JavaPackage pkg : packages) {
			if (pkg.isComponent()) {
				components.add(pkg.getName());
			}
		}
		return components;
	}
	
	/*
	 * Returns the names of all packages, or, if the package is part of one of
	 * the provided components, the name of the component.
	 * 
	 * A package is considered part of a component if the component name matches
	 * the package name or any of the package super-package names.
	 */
	static Set<String> compressToComponents(Collection<JavaPackage> packages, Set<String> components) {
		Set<String> compressed = new HashSet<String>();
		for (JavaPackage pkg : packages) {
			// Is the package itself a component?
			if (pkg.isComponent()) {
				compressed.add(pkg.getName());
				continue;
			}
			
			// Is the package part of one of the components?
			String name = pkg.getName();
			for (String component : components) {
				if (pkg.getName().equals(component) || pkg.getName().startsWith(component + ".")) {
					name = component;
					break;
				}
			}
			compressed.add(name);
		}
		return compressed;
	}
}
