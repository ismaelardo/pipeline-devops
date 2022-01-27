
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
		}

		parameters {
			choice(name: 'buildTool', choices: ['gradle', 'maven'], description: 'Indicar herramienta de construcción')
		}

		stages{
			stage('Pipeline'){
				steps{
					script{
						println 'Pipeline'
						
		                if (params.buildTool == "gradle") {
		                    gradle()
		                } else {
		                    maven()
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
