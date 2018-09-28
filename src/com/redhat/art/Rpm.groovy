package com.redhat.art

import com.redhat.art.GitHubRepository
import com.redhat.art.RpmSpec

class Rpm {

    GitHubRepository repo
    String collection
    def pipeline

    def Rpm(Map init) {
        this.repo = init.repo
        this.collection = init.collection
        this.pipeline = init.pipeline
    }

    String getSpecpath() {
        return repo.specpath
    }

    RpmSpec getSpec() {
        return new RpmSpec([filename: repo.specpath, pipeline: pipeline])
    }

    def tag(Map args) {

        String version_spec
        if (args.version && args.release) {
            version_spec = "--use-version ${args.version} --use-release ${args.release}"
        } else {
            version_spec = "--keep-version"
        }
        
        pipeline.echo("Tagging with ${version_spec}")
        
        def build_cmd = [
            "tito tag",
            (args.debug ? '--debug' : ''),
            '--accept-auto-changelog',
            version_spec
        ].join(' ')
        
        if (collection) {
            build_cmd = "scl enable ${collection} '${build_cmd}'"
        }

        pipeline.echo("tagging with cli: ${build_cmd}")
        
        pipeline.dir(repo.path) {
            pipeline.sh(
                script: build_cmd
            )
        }
    }

    def build(destination="./BUILD", debug=false) {
        
        def build_cmd = [
            "tito build",
            (debug ? '--debug' : ''),
            '--offline',
            '--rpm',
            '--output', destination,
        ].join(' ')

        if (collection) {
            build_cmd = "scl enable ${collection} '${build_cmd}'"
        }

        pipeline.dir(repo.path) {
            pipeline.sh(
                script: build_cmd
            )
        }
    }

    def release(scratch=true, debug=false) {
        def s = spec
        s.load()
        def version = new Version(s.version)
        def brew_task_id
        pipeline.dir(repo.path) {
            def tito_output = pipeline.sh(
                returnStdout: true,
                script: [
                    'tito release',
                    (debug ? '--debug' : ''),
                    '--yes',
                    '--test',
                    (scratch ? '--scratch' : ''),
                    "aos-${version.majorminor}"
                ].join(' ')
            )

            if (debug) {
                pipeline.echo "--- tito output\n${tito_output} ----"
            }

            def tito_lines = tito_output.tokenize('\n')
            def task_line = tito_lines.find{ it =~ /^Created task: / }
            if (debug) {
                pipeline.echo "task line: '${task_line}'"
            }
            def task_matcher = task_line =~ /^Created task:\s+([0-9]+),*/
            if (debug) {
                pipeline.echo "task matches: ${task_matcher[0]}"
            }
            // check if the matcher has any results
            brew_task_id = task_matcher[0][1]

            def url_line = tito_lines.find { it =~ /^Task info: / }
            if (debug) {
                pipeline.echo("Build URL line: ${url_line}")
            }
            def url_match = url_line =~ /^Task info: (.*)$/
            if (debug) {
                pipeline.echo("url matches: ${url_match[0]}")
            }
            def brew_task_url = url_match[0][1]

            pipeline.echo "${repo.package_name} rpm brew task: ${brew_task_id}"
    
            try {
                watch_task_command = "brew watch-task ${brew_task_id}"
                pipeline.sh(
                    script: watch_task_command
                )
            } catch (build_err) {
                pipeline.echo "Error in ${repo.package_name} build task: ${brew_task_url}"
                pipeline.echo "brew watch task error: ${build_err}"
                throw build_err
            }
        }

        return brew_task_id
    }
}
