//
// Model a remote git repository
//
import com.redhat.art.Version

class GitHubRepository {

    String owner
    String project
    String branch
    String path
    def pipeline
    
    //GitHubRepository(owner, project, branch=null, path=null, password=null, pipeline=null) {
    //    this.owner = owner
    //    this.project = project
    //    this.branch = branch
    //    this.path = path
    //    this.password = password
    //    this.pipeline = pipeline
    //}

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

    def clone() {
        pipeline.sh(
            returnStdout: true,
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
    def branches(pattern="") {

        if (this.pipeline == null) {
            return null
        }

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
    def releases(pattern="enterprise-") {
        // too clever: chain - get branch names, remove prefix, suffix
        def r = this.branches(repo_url, pattern + '*')
            .collect { it - pattern }
            .findAll { it =~ /^\d+((\.\d+)*)$/ }
            .collect { new Version(it) }
            .sort()

        return r
    }
}
