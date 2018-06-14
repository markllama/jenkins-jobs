//
// Don't run lots, it gets confused
//
properties(
  [
    disableConcurrentBuilds()
  ]
)

node('container') {
    stage("simple echo") {
        sh "echo it ran"
    }
}
