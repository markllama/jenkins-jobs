#!/usr/bin/groovy
package com.redhat.art

class Version implements Comparable<Version>{
    private v_array

    // new
    def Version(String v_string) {
        v_array = v_string.tokenize('.').collect { it as int }
    }

    // copy
    def Version(Version old) {
        v_array = old.v_array.collect { it }
    }

    def Version(int major, int minor, int revision=0) {
        v_array = [major, minor, revision]
    }

    def String toString() {
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

    // When incrementing one field, resets the sub-fields to 0
    Version incrMajor() {
        this.v_array[0]++
        this.v_array[1] = 0
        if (this.v_array.size() > 2) {
            this.v_array[2] = 0
        }
    }
    
    Version incrMinor() {
        this.v_array[1]++
        this.v_array[2] = 0
        if (this.v_array.size() > 2) {
            this.v_array[2] = 0
        }
    }

    Version incrRevision() {
        if (this.v_array.size() > 2) {
            this.v_array[2]++
        } else {
            this.v_array[2] = 1
        }

    }
}

//return this

main() {
    println hello
}
