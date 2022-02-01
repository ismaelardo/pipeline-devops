
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

						def ci_or_cd = verifyBranchName()

		                if (params.buildTool == "gradle") {
		                	def lst = ['Build & Unit test', 'SonarQube analysis', 'Run', 'Test', 'nexus']
		                	if (params.stage == ''){
		                		stges = lst
		                	}
		                	for (String st in stges){
		                		if (lst.contains(st)){
		                			gradle(st, verifyBranchName())
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
		                			maven(st, verifyBranchName())
		                			i=i+1
		                		}
		                	}		                    
		                }
		                if (i==0){
		                	sh 'exit 1'
		                }
					}
					
				}
			}
		}
	    post {
	    	/*
	        success {
	            slackSend color: 'good', message: "[${env.USER}][${env.JOB_NAME}][${params.buildTool}] Ejecución exitosa."
	        }
	        */

	        failure {
	            //slackSend color: 'danger', message: "[${env.USER}][${env.JOB_NAME}][${params.buildTool}] Ejecución fallida en stage ${STAGE}."
	            error "Ejecución fallida, no existe el stage ingresado"
	        }
	    }
	}

}

def verifyBranchName(){
	//def is_ci_or_cd = (env.GIT_BRANCH.contains('feature-')) ? 'CI' : 'CD'
	//return is_ci_or_cd
	if (env.GIT_BRANCH.contains('feature-') || env.GIT_BRANCH.contains('develop')) {
		return 'CI'
	} else {
		return 'CD'
	}
}

return this;
