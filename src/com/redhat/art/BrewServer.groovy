//
// This library describes a class of objects to manage interactions with the
// Brew/Koji build system
//
package com.redhat.art

class BrewServer {
    def url
    def principle
    def keytab
    def pipeline

    BrewServer(Map init) {
        url = init.url
        principal = init.principal
        keytab = init.keytab
        pipeline = init.pipeline
    }

    def login() {
        pipeline.sh "kinit -k -t ${keytab} ${principal}"
    }
}

//class BrewTask {
//
//}

