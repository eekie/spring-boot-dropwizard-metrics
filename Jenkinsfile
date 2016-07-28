node ('linux'){

    stage 'Build and Test'
//    env.PATH = "${tool 'Maven 3'}/bin:${env.PATH}"
    checkout scm

    def v = version()
    if (v) {
        echo "Building version ${v}"
    }

    echo "My branch is: ${env.BRANCH_NAME}"

    if (env.BRANCH_NAME = 'master') {
        //input 'Ready to go?'
        echo "My branch is: ${env.BRANCH_NAME}"
    }

    def mvnHome = tool 'Maven 3'
    sh "${mvnHome}/bin/mvn -B -Psonar -Dmaven.test.failure.ignore package"
    step([$class: 'ArtifactArchiver', artifacts: '**/target/*.jar', fingerprint: true])
    step([$class: 'JUnitResultArchiver', testResults: '**/target/surefire-reports/TEST-*.xml'])

//    stage 'Sonar'
//    sh '${mvnHome}/bin/mvn -Psonar sonar:sonar'

}

node ('docker'){

    stage 'Build docker image'
    checkout scm
    sh 'docker build -t eekie/dropwizard-metrics -f src/main/docker/Dockerfile' .

}

def version() {
  def matcher = readFile('pom.xml') =~ '<version>(.+)</version>'
  matcher ? matcher[0][1] : null
}
