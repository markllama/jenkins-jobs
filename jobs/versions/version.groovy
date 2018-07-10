#!/usr/bin/groovy

class Version implements Comparable<Version>{
    private v_array
    def Version(v_string) {
        v_array = v_string.tokenize('.').collect { it as int }
    }

    def java.lang.String toString() {
        return v_array.collect { it.toString() }.join('.')
    }

    def size() { return v_array.size() }
    
    def major() { return v_array[0] }
    def minor() { return v_array[1] }
    def revision() {
        if (v_array.size() > 2) {
            return v_array[2]
        }

        error("Version string too small: no revision field")
    }

    def maj_min() {
        return v_array[0..1].collect { it.toString() }.join('.')
    }

    def pad(length=3) {
        while (v_array.size() < length) {
            println("v_array = ${v_array}, size = ${v_array.size()}")
            v_array << 0
        }
        return this
    }

    @Override
    int compareTo(Version obj) {
        // compare two version strings.
        // return:
        //   v0 < v1: -1
        //   v0 > v1:  1
        //   v0 = v1:  0

        pad()
        obj.pad()
        
        // zip these two together for comparison
        // major.minor.revision
        def mmr = [v_array, obj.v_array].transpose()

        // if any pair do not match, return the result
        for (field in mmr) {
            if (field[0] != field[1]) {
                return (field[0] <=> field[1])
            }
        }
        return 0
    }

    @Override
    boolean equals(obj) {
        return (this <=> obj) == 0
    }

    //static sort(Version[] a) {
    //    return this.sort { x, y -> this.cmp(x, y) }
    //}

    //Version incMajor() {
    //    
    //}
    
    //Version incMinor() {
    //    
    //}

    //Version incRevision() {
    //    
    //}
}


class RpmId {
    Version version
    Version revision
}

// ===========================================================================
//
// tests
//
// ===========================================================================

def test_pad() {
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
        println("error testing size: ${size_error}")
    }    
}


def test_cmp() {

    pass_count = 0
    fail_count = 0
    
    // initial values
    values = [
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

    versions = values.collect { new Version(it) }
    
    // test "equals"
    expected = 0
    versions.each {
        actual = (it <=> it)
        try {
            assert actual == expected
            pass_count++
        } catch (AssertionError e) {
            fail_count++
            println "FAIL: ${it}.cmp(${it} - expected: ${expected}, actual: ${actual}"
        }
    }

    /// test equality of different length versions
    shortVer = new Version("3.1")
    longVer = new Version("3.1.0")
    actual = shortVer <=> longVer
    try {
        assert actual == expected
        pass_count++
    } catch (AssertionError e) {
        fail_count++
        println "FAIL: ${shortVer}.cmp(${longVer}) - expected: ${expected}, actual: ${actual}"
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
            println "FAIL: ${versions[it]}.cmp(${versions[it + 1]}) - expected: ${expected}, actual: ${actual}"
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
        println "FAIL: ${shortVer}.cmp({$longVer})- expected: ${expected}, actual: ${actual}"
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
            println "FAIL: cmp_version(${values[it]}, ${values[it - 1]}) - expected: ${expected}, actual: ${actual}"
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
        println "FAIL: cmp_version(\"3.1.1\"}, \"3.1\") - expected: ${expected}, actual: ${actual}"
    }

    if (fail_count == 0) {
        println "PASS: cmp_versopm() - ${pass_count} tests passed"
    } else {
        println "FAIL: cmp_version() - ${pass_count} tests passed, ${fail_count} tests failed"
    }
}


def main() {
    println("yep")

    def v = new Version('1.2.3')
    def v0 = new Version('1.2.3')
    def v1 = new Version('1.2.1')
    def v2 = new Version('2.2.4')
    
    println("Version = " + v )
    println("Major/Minor = " + v.maj_min())

    println("${v} == ${v0}: ${v == v0}")
    println("${v} == ${v1}: ${v == v1}")

    
    println("${v} > ${v1}: ${v <=> v1}")
    println("${v} < ${v2}: ${v <=> v2}")

    test_pad()

    test_cmp()
}

main()
