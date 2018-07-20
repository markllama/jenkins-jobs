package com.redhat.art

class RpmIdentifier {

    def Version version
    def Version release

    def RpmIdentifier(Version v, Version r) {
        version = v
        release = r
    }

    def RpmIdentifier(string v_string, string r_string) {
        version = new Version(v_string)
        release = new Version(r_string)
    }


    
}
