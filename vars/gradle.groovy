/*
	forma de invocación de método call:
	def ejecucion = load 'script.groovy'
	ejecucion.call()
*/

def call(){

	stage('Build & Unit test') {
		STAGE = env.STAGE_NAME
        sh './gradlew clean build'
                    //println "Stage: ${env.STAGE_NAME}"
    }

    stage('SonarQube analysis'){
    	STAGE = env.STAGE_NAME
        def scannerHome = tool 'sonar-scanner';
        withSonarQubeEnv('sonarqube-server'){
            sh "${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=ejemplo-gradle-nuevo -Dsonar.sources=src -Dsonar.java.binaries=build"
        }
    }

    stage('Run') {
    	STAGE = env.STAGE_NAME
        sh "nohup bash ./gradlew bootRun &"
        sleep 20

    }

    stage('Test') {
    	STAGE = env.STAGE_NAME
        sh "curl -X GET 'http://localhost:8081/rest/mscovid/test?msg=testing'"
                //println "Stage: ${env.STAGE_NAME}"
    }
    
    stage('nexus') {
    	STAGE = env.STAGE_NAME
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

return this;