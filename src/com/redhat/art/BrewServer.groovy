//
// This library describes a class of objects to manage interactions with the
// Brew/Koji build system
//
package com.redhat.art

class BrewServer {
    def url
    def principal
    def keytab
    def pipeline

    BrewServer(Map init) {
        this.url = init.url
        this.principal = init.principal
        this.keytab = init.keytab
        this.pipeline = init.pipeline
    }

    def login() {
        pipeline.sh "kinit -k -t ${keytab} ${principal}"
    }
}

//class BrewTask {
//
//}

