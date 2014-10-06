package jdepend.framework;

import java.io.IOException;

/**
 * @author <b>Mike Clark</b>
 * @author Clarkware Consulting, Inc.
 */

public class ConstraintTest extends JDependTestCase {

    private JDepend jdepend;

    public ConstraintTest(String name) {
        super(name);
    }

    protected void setUp() {
        super.setUp();
        PackageFilter filter = new PackageFilter();
        filter.addPackage("java.*");
        filter.addPackage("javax.*");
        jdepend = new JDepend(filter);
    }

    public void testMatchPass() {

        DependencyConstraint constraint = new DependencyConstraint();

        JavaPackage expectedA = constraint.addPackage("A");
        JavaPackage expectedB = constraint.addPackage("B");

        expectedA.dependsUpon(expectedB);

        JavaPackage actualA = new JavaPackage("A");
        JavaPackage actualB = new JavaPackage("B");

        actualA.dependsUpon(actualB);

        jdepend.addPackage(actualA);
        jdepend.addPackage(actualB);

        assertEquals(true, jdepend.dependencyMatch(constraint));
    }

    public void testMatchFail() {

        DependencyConstraint constraint = new DependencyConstraint();

        JavaPackage expectedA = constraint.addPackage("A");
        JavaPackage expectedB = constraint.addPackage("B");
        JavaPackage expectedC = constraint.addPackage("C");

        expectedA.dependsUpon(expectedB);

        JavaPackage actualA = new JavaPackage("A");
        JavaPackage actualB = new JavaPackage("B");
        JavaPackage actualC = new JavaPackage("C");

        actualA.dependsUpon(actualB);
        actualA.dependsUpon(actualC);

        jdepend.addPackage(actualA);
        jdepend.addPackage(actualB);
        jdepend.addPackage(actualC);

        assertEquals(false, jdepend.dependencyMatch(constraint));
    }

    public void testJDependConstraints() throws IOException {
        
        jdepend.addDirectory(getBuildDir());

        jdepend.analyze();

        DependencyConstraint constraint = new DependencyConstraint();

        JavaPackage junitframework = constraint.addPackage("junit.framework");
        JavaPackage junitui = constraint.addPackage("junit.textui");
        JavaPackage framework = constraint.addPackage("jdepend.framework");
        JavaPackage text = constraint.addPackage("jdepend.textui");
        JavaPackage xml = constraint.addPackage("jdepend.xmlui");
        JavaPackage swing = constraint.addPackage("jdepend.swingui");
        JavaPackage orgjunitrunners = constraint.addPackage("org.junit.runners");
        JavaPackage jdependframeworkp2 = constraint.addPackage("jdepend.framework.p2");
        JavaPackage jdependframeworkp3 = constraint.addPackage("jdepend.framework.p3");
        JavaPackage jdependframeworkp1 = constraint.addPackage("jdepend.framework.p1");
        JavaPackage orgjunit = constraint.addPackage("org.junit");

        framework.dependsUpon(junitframework);
        framework.dependsUpon(junitui);
        text.dependsUpon(framework);
        xml.dependsUpon(framework);
        xml.dependsUpon(text);
        swing.dependsUpon(framework);
        framework.dependsUpon(jdependframeworkp2);
        framework.dependsUpon(jdependframeworkp3);
        framework.dependsUpon(jdependframeworkp1);
        framework.dependsUpon(orgjunitrunners);
        framework.dependsUpon(orgjunit);

        assertEquals(true, jdepend.dependencyMatch(constraint));
    }
}