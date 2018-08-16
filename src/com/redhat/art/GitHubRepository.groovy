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
    String package_name
    String password
    def pipeline

    GitHubRepository(owner, project, branch="", path=null, package_name=null, password="", pipeline=null) {
        this.owner = owner
        this.project = project
        this.branch = branch
        this.path = path ? path : project
        this.package_name = package_name ? package_name : project
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
        return package_name + ".spec"
    }

    def getSpecpath() {
        return [path, specfile].join('/')
    }

    //@NonCPS
    def clone() {
        pipeline.echo("Cloning repo ${remote}")
        pipeline.sh(
            returnStdout: false,
            script: [
                "git clone",
                branch ? "--branch ${branch}" : "",
                remote,
                path
            ].join(' ')
        )
        pipeline.echo("Cloning repo ${remote}")
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
    def branches(pattern="") {
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
    def releases(pattern="enterprise-") {

        // too clever: chain - get branch names, remove prefix, suffix
        def r = this.branches(pattern + '*')
            .collect { it - pattern }
            .findAll { it =~ /^\d+((\.\d+)*)$/ }
            .collect { new Version(it) }
            .sort()

        return r
    }

    /*
     *
     */
    @NonCPS
    def addRemote(remote_name, remote_project) {
        // git remote add ${remote_name} ${remote_spec}
        def remote_spec = "git@github.com:${owner}/${remote_project}.git"
        pipeline.dir(path) {
            pipeline.sh(
                script: "git remote add ${remote_name} ${remote_spec} --no-tags"
            )
        }
    }
}
