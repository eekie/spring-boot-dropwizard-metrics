node ('linux'){

    stage 'Build and Test'
    env.PATH = "${tool 'Maven 3'}/bin:${env.PATH}"
    checkout scm
    sh 'mvn clean package'

    stage 'Sonar'
    sh 'mvn -Psonar verify'

}

node ('docker'){

    stage 'Build docker image'
    checkout scm
    sh 'docker-compose -f src/main/docker/docker-compose.yml build'

}
