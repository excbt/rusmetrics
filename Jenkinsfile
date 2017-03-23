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
        sh "chmod +x ./mvnw"
        sh "./mvnw clean"
    }

    stage('packaging') {
        sh "./mvnw package -Dmaven.test.skip=true -Pprod"
    } 

    stage('archive-WAR') {
        archive includes:'**/target/*.war', excludes:'**/target/*-sources.jar' 
    }    

    stage('archive-Original') {
        archive includes:'**/target/*.original', excludes:'**/target/*-sources.jar' 
    }    

}
