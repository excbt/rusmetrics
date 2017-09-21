node {
    stage('checkout') {
        checkout scm
    }

    try {

        notifyBuild('STARTED')

        // uncomment these 2 lines and edit the name 'node-4.6.0' according to what you choose in configuration
        // def nodeHome = tool name: 'node-4.6.0', type: 'jenkins.plugins.nodejs.tools.NodeJSInstallation'
        // env.PATH = "${nodeHome}/bin:${env.PATH}"

        def mavenHome = tool name: 'M3'
        env.PATH = "${mavenHome}/bin:${env.PATH}"


        stage('clean') {
            sh "chmod +x ./mvnw"
            sh "./mvnw clean"
        }

        stage('backend tests') {
            try {
                sh "./mvnw test"
            } catch(err) {
                currentBuild.result = "FAILED"
                throw err
            } finally {
                step([$class: 'JUnitResultArchiver', testResults: '**/target/surefire-reports/TEST-*.xml'])
            }
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
        
    } finally {
        notifyBuild(currentBuild.result)
    }        

}

def notifyBuild(String buildStatus = 'STARTED') {
  // build status of null means successful
  buildStatus =  buildStatus ?: 'SUCCESSFUL'
 
  // Default values
  def colorName = 'RED'
  def colorCode = '#FF0000'
  def subject = "${buildStatus}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'"
  def summary = "${subject} (${env.BUILD_URL})"
  def details = """<p>STARTED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]':</p>
    <p>Check console output at "<a href="${env.BUILD_URL}">${env.JOB_NAME} [${env.BUILD_NUMBER}]</a>"</p>"""
 
  // Override default values based on build status
  if (buildStatus == 'STARTED') {
    color = 'YELLOW'
    colorCode = '#FFFF00'
  } else if (buildStatus == 'SUCCESSFUL') {
    color = 'GREEN'
    colorCode = '#00FF00'
  } else {
    color = 'RED'
    colorCode = '#FF0000'
  }
 
  // Send notifications
  slackSend (color: colorCode, message: summary)
 
//   hipchatSend (color: color, notify: true, message: summary)
 
//   emailext (
//       subject: subject,
//       body: details,
//       recipientProviders: [[$class: 'DevelopersRecipientProvider']]
//     )
}

