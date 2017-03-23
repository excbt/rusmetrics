node {
    stage('checkout') {
        checkout scm
    }

    // uncomment these 2 lines and edit the name 'node-4.6.0' according to what you choose in configuration
    // def nodeHome = tool name: 'node-4.6.0', type: 'jenkins.plugins.nodejs.tools.NodeJSInstallation'
    // env.PATH = "${nodeHome}/bin:${env.PATH}"

    def mavenHome = tool name: 'M3'
    env.PATH = "${mavenHome}/bin:${env.PATH}"


    stage('clean') {
        withMaven(maven: 'M3') {

        // Run the maven build
        sh "mvn clean"
        }
    }

    stage('packaging') {
        withMaven(maven: 'M3') {
            sh "mvn package -DskipTests"
        }    
    }

    stage('install') {
        withMaven(maven: 'M3') {
            sh "mvn install -DskipTests"
        }    
    }

    stage('archive') {
        archive includes:'**/target/*.war', excludes:'**/target/*-sources.jar' 
    }    
}
