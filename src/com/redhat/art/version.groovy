#!/usr/bin/groovy
package com.redhat.art

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


//return this

