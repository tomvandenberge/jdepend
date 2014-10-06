package jdepend.framework;

import java.util.Arrays;
import java.util.HashSet;
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
}
