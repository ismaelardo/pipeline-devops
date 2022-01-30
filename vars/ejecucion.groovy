
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
			string(name: 'stage', description: 'Indica que etapas se desean ejecutar')
		}

		stages{
			stage('Pipeline'){
				steps{
					script{
						int i = 0
						String[] stges 
						stges = params.stage.split(';') 
						println 'Pipeline'
		                if (params.buildTool == "gradle") {
		                	def lst = ['Build & Unit test', 'SonarQube analysis', 'Run', 'Test', 'nexus']
		                	if (params.stage == ''){
		                		stges = lst
		                	}
		                	for (String st in stges){
		                		if (lst.contains(st)){
		                			gradle(st)
		                			i = i+1
		                		}
		                	}
		                } else {
		                	def lst = ['Compile', 'Test', 'Jar', 'Run', 'Testing']
		                	if (params.stage == ''){
		                		stges = lst
		                	}
		                	for (String st in stges){
		                		if (lst.contains(st)){
		                			maven(st)
		                			i=i+1
		                		}
		                	}		                    
		                }
		                if (i==0){
		                	exit 1
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
