#!/usr/bin/groovy
package com.redhat.art


import com.redhat.art.VersionString

class VersionStringTest extends GroovyTestCase {

    void testConstructor() {
        def vs = new VersionString('3.2.1')

        def expected = [3, 2, 1]
        def actual = vs.v_array

        assertEquals(actual, expected)
    }
}

// mock the NonCPS annotation for groovy testing
@interface NonCPS {}
