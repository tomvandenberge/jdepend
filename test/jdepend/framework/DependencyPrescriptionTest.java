package jdepend.framework;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static jdepend.framework.DependencyPrescription.compressToComponents;
import static jdepend.framework.DependencyPrescription.equalsEfferents;
import static jdepend.framework.DependencyPrescription.getComponents;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

public class DependencyPrescriptionTest extends TestCase {

	private JDepend jdepend = new JDepend();

    public void testMatchPass() {
        DependencyPrescription constraint = new DependencyPrescription();
        JavaPackage expectedA = constraint.addPackage("A");
        JavaPackage expectedB = constraint.addPackage("B");

        expectedA.dependsUpon(expectedB);

        JavaPackage actualA = new JavaPackage("A");
        JavaPackage actualB = new JavaPackage("B");

        actualA.dependsUpon(actualB);

        jdepend.addPackage(actualA);
        jdepend.addPackage(actualB);

        assertEquals(true, jdepend.followsDirective(constraint));
    }
    
    /**
     * A default prescription must specify all dependencies -- no more and no less.
     * This test prescribes one dependency, while there are actually 2.
     */
    public void testMatchInsufficientDependenciesPrescribed() {
    	// Prescription: A -> B 
		DependencyPrescription prescription = new DependencyPrescription();
		JavaPackage expectedA = prescription.addPackage("A");
		JavaPackage expectedB = prescription.addPackage("B");
		expectedA.dependsUpon(expectedB);

		// Actual: A -> B <- C
		JavaPackage actualA = new JavaPackage("A");
		JavaPackage actualB = new JavaPackage("B");
		JavaPackage actualC = new JavaPackage("C");
		actualA.dependsUpon(actualB);
		actualC.dependsUpon(actualB);

		jdepend.addPackage(actualA);
		jdepend.addPackage(actualB);
		jdepend.addPackage(actualC);

		assertEquals(false, jdepend.followsDirective(prescription));
    }

    public void testMatchTooManyDependenciesPrescribed() {
    	// Prescription: A -> B -> C
    	DependencyPrescription prescription = new DependencyPrescription();
    	JavaPackage expectedA = prescription.addPackage("A");
    	JavaPackage expectedB = prescription.addPackage("B");
    	JavaPackage expectedC = prescription.addPackage("C");
    	expectedA.dependsUpon(expectedB);
    	expectedB.dependsUpon(expectedC);
    	
    	// Actual: A -> B 
    	JavaPackage actualA = new JavaPackage("A");
    	JavaPackage actualB = new JavaPackage("B");
    	actualA.dependsUpon(actualB);
    	
    	jdepend.addPackage(actualA);
    	jdepend.addPackage(actualB);
    	
    	assertEquals(false, jdepend.followsDirective(prescription));
    }
    
    public void testPrescriptionWithPackageFilter() {
    	// Prescription: filter on A only: A -> B
    	Set<JavaPackage> filter = new HashSet<JavaPackage>(Arrays.asList(new JavaPackage("A")));
    	DependencyPrescription prescription = new DependencyPrescription(filter);
    	JavaPackage expectedA = prescription.addPackage("A");
    	JavaPackage expectedB = prescription.addPackage("B");
    	expectedA.dependsUpon(expectedB);
    	
    	// Actual: A -> B <- C
    	JavaPackage actualA = new JavaPackage("A");
    	JavaPackage actualB = new JavaPackage("B");
    	JavaPackage actualC = new JavaPackage("C");
    	actualA.dependsUpon(actualB);
    	actualC.dependsUpon(actualB);
    	
    	jdepend.addPackage(actualA);
    	jdepend.addPackage(actualB);
    	jdepend.addPackage(actualC);
    	
    	assertEquals(true, jdepend.followsDirective(prescription));
    }
    
    public void testComponentEfferents() {
    	JavaPackage fu = new JavaPackage("com.fu");
    	JavaPackage util = new JavaPackage("org.util.*");
    	fu.dependsUpon(util);
    	
    	JavaPackage actualFu = new JavaPackage("com.fu");
    	JavaPackage actualUtil = new JavaPackage("org.util.text");
    	actualFu.dependsUpon(actualUtil);
    	
    	assertTrue(equalsEfferents(fu, actualFu));
    }
    
    public void testGetComponents() {
    	Set<String> components = getComponents(asList(new JavaPackage("com.bar.package"), new JavaPackage("com.fu.component.*")));
    	assertEquals(Collections.singleton("com.fu.component"), components);
    }
    
    public void testCompressToComponents() {
    	List<JavaPackage> packages = asList(new JavaPackage("com.component.a"), new JavaPackage("com.component.b"), new JavaPackage("com.component"), new JavaPackage("org.fubar.xyz"), new JavaPackage("java.lang.*"));
    	Set<String> expected = new HashSet<String>(asList("com.component", "org.fubar.xyz", "java.lang"));
    	
    	Set<String> compressed = compressToComponents(packages, singleton("com.component"));
    	assertEquals(expected, compressed);
    }
    
    public void testEqualsEfferents() {
    	// Actual dependencies
    	JavaPackage fu = new JavaPackage("com.fu");
    	JavaPackage x = new JavaPackage("org.component.x");
    	JavaPackage y = new JavaPackage("org.component.y");
    	JavaPackage bar = new JavaPackage("com.bar.xyz");
    	fu.dependsUpon(x);
    	fu.dependsUpon(y);
    	fu.dependsUpon(bar);
    	
    	// Prescribed dependencies
    	JavaPackage pFu = new JavaPackage("com.fu");
    	// org.component.x and org.component.y are one component
    	JavaPackage component = new JavaPackage("org.component.*");
    	// regular package
    	JavaPackage pBar = new JavaPackage("com.bar.xyz");
    	pFu.dependsUpon(component);
    	pFu.dependsUpon(pBar);
    	
    	assertTrue(equalsEfferents(fu, pFu));
    	assertTrue(equalsEfferents(pFu, fu));
    }

    public void testNotEqualsEfferents() {
    	JavaPackage fu = new JavaPackage("com.fu");
    	fu.dependsUpon(new JavaPackage("org.component.x"));
    	fu.dependsUpon(new JavaPackage("org.component.y"));
    	// this dependency is not prescribed
    	fu.dependsUpon(new JavaPackage("com.bar.xyz"));
    	
    	JavaPackage pFu = new JavaPackage("com.fu");
    	JavaPackage component = new JavaPackage("org.component.*");
    	pFu.dependsUpon(component);
    	
    	assertFalse(equalsEfferents(fu, pFu));
    	assertFalse(equalsEfferents(pFu, fu));
    }
}
