/*
	forma de invocación de método call:
	def ejecucion = load 'script.groovy'
	ejecucion.call()
*/

def call(stages, String pipelineType){
    figlet 'Gradle'
    if (pipelineType == 'CI'){

        figlet 'Integración Contínua'

        stage('Build & Unit test') {
            STAGE = env.STAGE_NAME
            if (stages == STAGE) {
                figlet "Stage: ${env.STAGE_NAME}"
                sh './gradlew clean build'
            }       
        }

        stage('SonarQube analysis'){
            STAGE = env.STAGE_NAME
            if (stages == STAGE) {
                figlet "Stage: ${env.STAGE_NAME}"
                def scannerHome = tool 'sonar-scanner';
                withSonarQubeEnv('sonarqube-server'){
                    sh "${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=ejemplo-gradle-nuevo -Dsonar.sources=src -Dsonar.java.binaries=build"
                }
            }
        }

        stage('Run') {
            STAGE = env.STAGE_NAME
            if (stages == STAGE) {
                figlet "Stage: ${env.STAGE_NAME}"
                sh "nohup bash ./gradlew bootRun &"
                sleep 20
            }
        }

        stage('Test') {
            STAGE = env.STAGE_NAME
            if (stages == STAGE) {
                figlet "Stage: ${env.STAGE_NAME}"
                sh "curl -X GET 'http://localhost:8081/rest/mscovid/test?msg=testing'"
            }
        }
        
        stage('nexus') {
            STAGE = env.STAGE_NAME
            if (stages == STAGE) {
                figlet "Stage: ${env.STAGE_NAME}"
                nexusPublisher nexusInstanceId: 'nexus_test',
                nexusRepositoryId: 'test_nexus',
                packages: [
                    [
                        $class: 'MavenPackage',
                        mavenAssetList: [
                            [classifier: '', extension: '', filePath: "${env.WORKSPACE}/build/libs/DevOpsUsach2020-0.0.1.jar"]
                        ],
                        mavenCoordinate: [
                            artifactId: 'DevOpsUsach2020',
                            groupId: 'com.devopsusach2020',
                            packaging: 'jar',
                            version: '0.0.1'
                        ]
                    ]
                ]
            }
        }


    } else {

        figlet 'Delivery Contínuo'
        stage('DownloadSnapshotJar') {
            STAGE = env.STAGE_NAME
            if (stages == STAGE) {
                figlet "Stage: ${env.STAGE_NAME}"                
            }
        }

        stage('RunSnapshotJar') {
            STAGE = env.STAGE_NAME
            if (stages == STAGE) {
                figlet "Stage: ${env.STAGE_NAME}"                
            }
        }

        stage('TestSnapshotJar') {
            STAGE = env.STAGE_NAME
            if (stages == STAGE) {
                figlet "Stage: ${env.STAGE_NAME}"                
            }
        }

        stage('UploadReleaseJar') {
            STAGE = env.STAGE_NAME
            if (stages == STAGE) {
                figlet "Stage: ${env.STAGE_NAME}"                
            }
        }
    }

	

    

    

    
}

return this;