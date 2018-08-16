//
// Model a remote git repository
//
package com.redhat.art

import com.redhat.art.Version

class GitHubRepository {

    String owner
    String project
    String branch
    String path
    String password
    def pipeline
    
    GitHubRepository(owner, project, branch="", path=null, password="", pipeline=null) {
        this.owner = owner
        this.project = project
        this.branch = branch
        this.path = (path ? path : project)
        this.password = password
        this.pipeline = pipeline
    }

    def getRemote() {
        return "git@github.com:${this.owner}/${this.project}.git"
    }

    def getUrl() {
        return "https://github.com/${this.owner}/${this.project}.git"
    }

    def getSpecfile() {
        return this.project + ".spec"
    }

    def getSpecpath() {
        return [path, this.specfile].join('/')
    }

    @NonCPS
    def clone() {
        this.pipeline.sh(
            returnStdout: false,
            script: [
                "git clone",
                this.branch ? "--branch ${this.branch}" : "",
                this.remote,
                this.path
            ].join(' ')
        )
    }
    
    /*
     * Retrive the branch list from a remote repository
     * @param repo_url a git@ repository URL.
     * @param pattern a matching pattern for the branch list. Only return matching branches
     *
     * @return a list of branch names.  Removes the leading ref path
     *
     * Requires SSH_AGENT to have set a key for access to the remote repository
     */
    @NonCPS
    def branches(pattern="", newpipe=null) {

        branch_text = pipeline.sh(
            returnStdout: true,
            script: [
                "git ls-remote ${this.remote} ${pattern}",
                "awk '{print \$2}'",
                "cut -d/ -f3"
            ].join(" | ")
        )
        
        return branch_text.tokenize("\n")
    }

    /**
     * Retrive a list of release numbers from the OCP remote repository
     * @param repo_url a git@ repository URL.
     * @return a list of OSE release numbers.
     *
     * Get the branch names beginning with 'enterprise-'.
     * Extract the release number string from each branch release name
     * Sort in version order (compare fields as integers, not strings)
     * Requires SSH_AGENT to have set a key for access to the remote repository
     */
    @NonCPS
    def releases(pattern="enterprise-", newpipe=null) {

        // too clever: chain - get branch names, remove prefix, suffix
        def r = this.branches(pattern + '*')
            .collect { it - pattern }
            .findAll { it =~ /^\d+((\.\d+)*)$/ }
            .collect { new Version(it) }
            .sort()

        return r
    }
}
