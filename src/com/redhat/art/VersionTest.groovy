package com.redhat.art
//@Library('art')
//import com.redhat.art.Version

// ===========================================================================
//
// tests
//
// ===========================================================================


class VersionTest {

    def env
    
    def VersionTest(pipeline_env=null) {
        env = pipeline_env
    }

    def test_constructor() {
        
        def pass_count = 0
        def fail_count = 0

        def v0 = new Version('1.2.3')

        def expected = [1, 2, 3]
        def actual = v0.v_array
               
        try {
            assert actual == expected
            pass_count++
        } catch (e) {
            env.echo("FAIL: new Version from string: actual: ${actual}, expected = ${expected}")
        }
            
        def v1 = new Version(v0)

        actual = v1.v_array
        try {
            assert actual == expected
            pass_count++
        } catch (e) {
            env.echo("FAIL: new Version from clone: actual: ${actual}, expected = ${expected}")
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

        try {
            values.each { assert new Version(it['input']).size() == it['size'] }
            values.each { assert new Version(it['input']).pad().size() >= 3  }
        } catch (size_error) {
           env.echo("error testing size: ${size_error}")
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
           env.echo "PASS: Version() - ${pass_count} tests passed"
        } else {
           env.echo "FAIL: Version() - ${pass_count} tests passed, ${fail_count} tests failed"
        }
    }

    @NonCPS
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

        samples.each {

            def input = new Version(it.input)
            def expected = it.imajor
            def actual = input.incrMajor()

            env.echo "BEGIN testing incrMajor"
            try {
                assert actual == expected
                pass_count++
            } catch (AssertionError e) {
                error_text << "FAIL: ${input} incrMajor(): actual: ${actual}, expected: {expected}"
                fail_count++
            }
            env.echo "END testing incrMajor"

            expected = it.iminor
            actual = input.incrMinor()
            
            env.echo "BEGIN testing incrMinor"
            try {
                assert actual == expected
                pass_count++
            } catch (AssertionError e) {
                error_text << "FAIL: ${input} incrMinor(): actual: ${actual}, expected: {expected}"
                fail_count++
            }
            env.echo "END testing incrMinor"
            
            expected = it.irev
            actual = input.incrRevision()
            
            env.echo "BEGIN testing incrRevision"
            try {
                assert actual == expected
                pass_count++
            } catch (AssertionError e) {
                error_text << "FAIL: ${input} incrRevision(): actual: ${actual}, expected: {expected}"
                fail_count++
            }
            env.echo "END testing incrRevision"
            
        }

        env.echo("TEST incr*() : pass: ${pass_count}, fail: ${fail_count}")
        error_text.each { env.echo(it) }
        
    }
}
