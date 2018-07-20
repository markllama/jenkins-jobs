#!/usr/bin/groovy
//package com.redhat.art

import com.redhat.art.VersionString

class VersionStringTest extends GroovyTestCase {

    //protected VersionString v0
    //protected VersionString v1
    
    //protected void setUp() {

    //}
    
    void testConstructorString() {
        def vs = new VersionString('3.2.1')

        def expected = [3, 2, 1]
        def actual = vs.v_array

        assertEquals(actual, expected)
    }

    void testConstructorVersionString() {
        def vs0 = new VersionString('3.2.1')
        def vs1 = new VersionString(vs0)

        def expected = [3, 2, 1]
        def actual = vs1.v_array

        assertEquals(actual, expected)
    }
    
    void testConstructorVersionStrin() {
        def v_array = [3, 2, 1]
        def vs1 = new VersionString(v_array)

        def expected = [3, 2, 1]
        def actual = vs1.v_array

        assertEquals(actual, expected)
    }

    void testCompareTo() {

        def inputs = [
            [
                testValue: new VersionString('3.2.1'),
                greater: [
                    new VersionString('3.2.1.1'),
                    new VersionString('3.2.2'),
                    new VersionString('3.3.0'),
                    new VersionString('3.3'),
                    new VersionString('4.0.0'),
                    new VersionString('4.0'),
                    new VersionString('4'),
                ],
                less: [
                    new VersionString('3.2.0'),
                    new VersionString('3.1.9.1'),
                    new VersionString('2.3.0'),
                    new VersionString('1.0.0'),
                    new VersionString('3.2'),
                    new VersionString('2.9'),
                    new VersionString('2'),                    
                ],
                equal: [
                    new VersionString('3.2.1'),
                    new VersionString('3.2.1.0'),
                ]
            ]
        ]

        inputs.each { c -> 
            c.greater.each { t ->  assert t > c.testValue }
            c.less.each { t -> assert t < c.testValue }
            c.equal.each { t -> assert t == c.testValue }
        }
    }

    void testToString() { }

    void testPropertyMajor() { }
    void testPropertyMinor() { }
    void testPropertyRevision() { }

    void testPropertyMajorMinor() { }

    void testIncrMajor() { }

    void testIncrMinor() { }

    void testIncrRevision() { }
}

// mock the NonCPS annotation for groovy testing
@interface NonCPS {}
