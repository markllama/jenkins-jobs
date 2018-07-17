package com.redhat.art
//@Library('art')
//import com.redhat.art.Version
//import junit.

// ===========================================================================
//
// tests
//
// ===========================================================================


class VersionTest extends GroovyTestCase {

    def env
    
    def VersionTest(pipeline_env=null) {
        env = pipeline_env
    }

    def test_constructor() {
        
        def pass_count = 0
        def fail_count = 0

        // string constructor
        def v0 = new Version('1.2.3')

        def expected = [1, 2, 3]
        def actual = v0.v_array
               
        try {
            assert actual == expected
            pass_count++
        } catch (AssertionError e) {
            env.echo("FAIL: new Version from string: actual: ${actual}, expected = ${expected}")
        }

        // clone constructor
        def v1 = new Version(v0)

        try {
            assert !v1.is(v0) 
        } catch (e) {
            env.echo("FAIL: cloned Version is identical to the original")
        }

        actual = v1.v_array
        try {
            assert actual == expected
            pass_count++
        } catch (AssertionError e) {
            env.echo("FAIL: new Version from clone: actual: ${actual}, expected = ${expected}")
        }
        
        if (fail_count == 0) {
           env.echo "PASS: Version() - ${pass_count} tests passed"
        } else {
           env.echo "FAIL: Version() - ${pass_count} tests passed, ${fail_count} tests failed"
        }
    }


    def test_clone() {
        def pass_count = 0
        def fail_count = 0
        def input_values = [
            new Version("3.2.1")
        ]
        def clone 
        def actual
        def expected
        
        input_values.each {
            try {
                clone = it.clone()
                expected = true
                actual = (it == clone)
                assert actual == expected
            } catch (AssertionError e) {
                env.echo("FAIL: Version.clone() it = ${it}, clone = ${clone} - actual: ${actual}, expected: ${expected}")
            }
        }
        
        if (fail_count == 0) {
           env.echo "PASS: Version() - ${pass_count} tests passed"
        } else {
           env.echo "FAIL: Version() - ${pass_count} tests passed, ${fail_count} tests failed"
        }
    }

    def test_pad() {

        def pass_count = 0
        def fail_count = 0
        
        def values = [
            [input: '1.2.3',   size: 3, padded: '1.2.3'],
            [input: '1.2',     size: 2, padded: '1.2.0'],
            [input: '1.2.0',   size: 3, padded: '1.2.0'],
            [input: '1',       size: 1, padded: '1.0.0'],
            [input: '1.2.3.4', size: 4, padded: '1.2.3.4'],
        ]

        values.each {
            try {
                assert new Version(it['input']).size() == it['size']
                pass_count++
            } catch (AssertionError size_error) {
                fail_count++
                env.echo("error testing size: ${size_error}")
            }
        }

        values.each {
            try {
                assert new Version(it['input']).pad().size() >= 3
                pass_count++
            } catch (AssertionError size_error) {
                fail_count++
                env.echo("error testing size: ${size_error}")
            }
        }

        if (fail_count == 0) {
           env.echo "PASS: pad() - ${pass_count} tests passed"
        } else {
           env.echo "FAIL: pad() - ${pass_count} tests passed, ${fail_count} tests failed"
        }

    }
    
    def test_cmp() {

        def pass_count = 0
        def fail_count = 0
        
        // initial values
        def values = [
            "1.2",
            "3.0",
            "3.0.2",
            "3.0.5",
            "3.1",
            "3.10",
            "3.10.1",
            "3.11",
            "5.3",
            "6" // can you do single digit ones?
        ]

        def versions = values.collect { new Version(it) }
        
        // test "equals"
        def expected = 0
        versions.each {
            def actual = (it <=> it)
            try {
                assert actual == expected
                pass_count++
            } catch (AssertionError e) {
                fail_count++
               env.echo "FAIL: ${it}.cmp(${it} - expected: ${expected}, actual: ${actual}"
            }
        }

        /// test equality of different length versions
        def shortVer = new Version("3.1")
        def longVer = new Version("3.1.0")
        def actual = shortVer <=> longVer
        try {
            assert actual == expected
            pass_count++
        } catch (AssertionError e) {
            fail_count++
           env.echo "FAIL: ${shortVer}.cmp(${longVer}) - expected: ${expected}, actual: ${actual}"
        }

        // test "less than"
        expected = -1
        (0..(versions.size() - 2)).each {
            actual = versions[it] <=> versions[it + 1]
            try {
                assert actual == expected
                pass_count++
            } catch (AssertionError e) {
                fail_count++
               env.echo "FAIL: ${versions[it]}.cmp(${versions[it + 1]}) - expected: ${expected}, actual: ${actual}"
            }
        }

        /// test different length versions
        /// test equality of different length versions
        expected = 0
        shortVer = new Version("3.1")
        longVer = new Version("3.1.0")
        actual = shortVer <=> longVer
        try {
            assert actual == expected
            pass_count++
        } catch (AssertionError e) {
            fail_count++
           env.echo "FAIL: ${shortVer}.cmp({$longVer})- expected: ${expected}, actual: ${actual}"
        }

        // test "greater than"
        expected = 1
        (1..(values.size() - 1)).each {
            actual = versions[it] <=> versions[it - 1]
            try {
                assert actual == expected
                pass_count++
            } catch (AssertionError e) {
                fail_count++
               env.echo "FAIL: cmp_version(${values[it]}, ${values[it - 1]}) - expected: ${expected}, actual: ${actual}"
            }
        }
        
        // test different length versions
        longVer = new Version("3.1.1")
        shortVer = new Version("3.1")
        actual = longVer <=> shortVer
        try {
            assert actual == expected
            pass_count++
        } catch (AssertionError e) {
            fail_count++
           env.echo "FAIL: Version(\"3.1.1\"}, \"3.1\") - expected: ${expected}, actual: ${actual}"
        }

        if (fail_count == 0) {
           env.echo "PASS: cmp() - ${pass_count} tests passed"
        } else {
           env.echo "FAIL: cmp() - ${pass_count} tests passed, ${fail_count} tests failed"
        }
    }

    def test_equals() {
        def pass_count = 0
        def fail_count = 0

        def actual
        def expected

        def samples[
            [v0: new Version("1.2.3"), v1: new Version("1.2.3"), equal: true],
            [v0: new Version("3.2.3"), v1: new Version("3.2.3"), equal: true],
            [v0: new Version("4.2"), v1: new Version("4.2.0"), equal: true],
            [v0: new Version("2.2.0"), v1: new Version("2.2"), equal: true],
        ]
        // test equal


        // test not-equal

        samples.each {
            expected = it.equal
            actual = it.v0 == it.v1

            try {
                assert actual == expected
                pass_count++
            } catch (AssertionError e) {
                fail_count++
                env.echo "FAIL: equals(): input: ${it.v0} == ${it.v1}, expected: ${expected}, actual: ${actual}"
            }
        }
        
        if (fail_count == 0) {
           env.echo "PASS: equals() - ${pass_count} tests passed"
        } else {
           env.echo "FAIL: equals() - ${pass_count} tests passed, ${fail_count} tests failed"
        }

    }

    def test_increments() {

        def pass_count = 0
        def fail_count = 0

        def error_text = []
        
        // test incrMajor
        def samples = [
            [
                input: new Version('3.2.1'),
                imajor: new Version('4.0.0'),
                iminor: new Version('3.3.0'),
                irev: new Version('3.2.2')
            ]
        ]

        env.echo "Samples: ${samples}"
        
        samples.each {

            env.echo "input version: ${it.input}"
            def input = new Version(it.input)            
            def expected = it.imajor
            def actual = input.incrMajor()

            env.echo "BEGIN testing incrMajor"
            env.echo "input: ${it.input}, comparing actual: ${actual}, expected: ${expected}"
            try {
                assert actual == expected
                pass_count++
            } catch (AssertionError e) {
                error_text << "FAIL: ${it.input} incrMajor(): actual: ${actual}, expected: ${expected}"
                env.echo error_text[-1]
                fail_count++
            }
            env.echo "END testing incrMajor"

            input = new Version(it.input)            
            expected = it.iminor
            actual = input.incrMinor()
            
            env.echo "BEGIN testing incrMinor"
            env.echo("comparing actual: ${actual}, expected: ${expected}")
            try {
                assert actual == expected
                pass_count++
            } catch (AssertionError e) {
                error_text << "FAIL: ${it.input} incrMinor(): actual: ${actual}, expected: ${expected}"
                env.echo error_text[-1]
                fail_count++
            }
            env.echo "END testing incrMinor"
            
            input = new Version(it.input)            
            expected = it.irev
            actual = input.incrRevision()
            
            env.echo "BEGIN testing incrRevision"
            env.echo "comparing actual: ${actual}, expected: ${expected}"
            try {
                assert actual == expected
                pass_count++
            } catch (AssertionError e) {
                error_text << "FAIL: ${it.input} incrRevision(): actual: ${actual}, expected: ${expected}"
                env.echo error_text[-1]
                fail_count++
            }
            env.echo "END testing incrRevision"
            
        }

        env.echo("TEST incr*() : pass: ${pass_count}, fail: ${fail_count}")
        //env.echo error_text
        error_text.each { env.echo(it) }
            
        if (fail_count == 0) {
           env.echo "PASS: incr*() - ${pass_count} tests passed"
        } else {
           env.echo "FAIL: incr*() - ${pass_count} tests passed, ${fail_count} tests failed"
        }
    }
}
