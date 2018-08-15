#!/usr/bin/groovy

def cmp_version(String v0, String v1) {
    // compare two version strings.
    // return:
    //   v0 < v1: -1
    //   v0 > v1:  1
    //   v0 = v1:  0

    // split both into arrays on dots (.)
    try {
        a0 = v0.tokenize('.').collect { it as int }
        a1 = v1.tokenize('.').collect { it as int }
    } catch (convert_error) {
        error("Invalid version strings: ${v0} or ${v1} - ${convert_error}")
    }

    // extend both to 3 fields with zeros if needed
    while (a0.size() < 3) { a0 << 0 }
    while (a1.size() < 3) { a1 << 0 }

    // zip these two together for comparison
    // major.minor.revision
    mmr = [a0, a1].transpose()

    // if any pair do not match, return the result
    for (field in mmr) {
        t = field[0].compareTo(field[1])
        if (t != 0) { return t }
    }
    return 0
}



String[] a = ['3.10', '2.9', '3.1', '3.5.1']

//println(" a = ${a}")
//a.sort()


b = a.sort{ x, y -> cmp_version(x, y) }

println(" a = ${a}")
println(" b = ${b}")
