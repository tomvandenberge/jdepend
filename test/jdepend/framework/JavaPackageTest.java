package jdepend.framework;

import junit.framework.TestCase;

public class JavaPackageTest extends TestCase {

	public void testPlainPackage() {
		JavaPackage javaPackage = new JavaPackage("com.fubar");
		assertEquals("com.fubar", javaPackage.getName());
		assertFalse(javaPackage.isComponent());
	}
	
	public void testComponent() {
		JavaPackage javaPackage = new JavaPackage("com.fubar.*");
		assertEquals("com.fubar", javaPackage.getName());
		assertTrue(javaPackage.isComponent());
	}
}
