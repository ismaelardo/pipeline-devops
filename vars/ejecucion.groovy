
/*
	forma de invocación de método call:
	def ejecucion = load 'script.groovy'
	ejecucion.call()
*/

def call(){
  
  	pipeline {

		agent any
		
		environment {
		    STAGE = ''
		    MAVEN_COMPILE = 'False'
		    MAVEN_TEST = 'False'
		}

		parameters {
			choice(name: 'buildTool', choices: ['gradle', 'maven'], description: 'Indicar herramienta de construcción')
			string(name: 'stage', description: 'Indica que etapas se desean ejecutar')
		}

		stages{
			stage('Pipeline'){
				steps{
					script{
						println 'Pipeline'
		                if (params.buildTool == "gradle") {
		                	def lst = ['Build & Unit test', 'SonarQube analysis', 'Run', 'Test', 'nexus']
		                	if (lst.contains(params.stage)){

		                		gradle()
		                	}
		                } else {
		                	def lst = ['Compile', 'Test']
		                	if (lst.contains(params.stage)){
		                		if (params.stage == 'Compile'){
		                			MAVEN_COMPILE = 'True'
		                			println MAVEN_COMPILE
		                			println '------------------'
		                			println "${MAVEN_COMPILE}"
		                		}
		                		maven()
		                	}
		                    
		                }
					}
					
				}
			}
		}
	    post {
	        success {
	            slackSend color: 'good', message: "[${env.USER}][${env.JOB_NAME}][${params.buildTool}] Ejecución exitosa."
	        }

	        failure {
	            slackSend color: 'danger', message: "[${env.USER}][${env.JOB_NAME}][${params.buildTool}] Ejecución fallida en stage ${STAGE}."
	            error "Ejecución fallida en stage ${STAGE}"
	        }
	    }
	}

}

return this;
