v = load("src/com/redhat/art/version.groovy")

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

//main()
