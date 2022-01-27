/*
	forma de invocación de método call:
	def ejecucion = load 'script.groovy'
	ejecucion.call()
*/

def call(){
  
  	stage('Compile') {
  		STAGE = env.STAGE_NAME
        sh './mvnw clean compile -e'
        sh 'exit 1'
    }
    stage('Test') {
    	STAGE = env.STAGE_NAME
        sh './mvnw clean test -e'
    }
    stage('Jar') {
    	STAGE = env.STAGE_NAME
        sh './mvnw clean package -e'
    }
    stage('Run') {
    	STAGE = env.STAGE_NAME
        sh 'JENKINS_NODE_COOKIE=dontKillMe nohup bash mvnw spring-boot:run &'
    }
    stage('Testing') {
    	STAGE = env.STAGE_NAME
        sh "curl -X GET 'http://localhost:8081/rest/mscovid/test?msg=testing'"
    }
}

return this;