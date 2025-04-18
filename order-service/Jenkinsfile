pipeline {
    agent any
    environment {
        JAVA_HOME = tool 'JDK21' // The name you set in Jenkins UI
        PATH = "${JAVA_HOME}/bin:${PATH}"
    }

    stages {
        stage('Check Java Version') {
            steps {
                sh 'java -version'
            }
        }

        stage('Checkout Code') {
            steps {
                git branch: 'main', url: 'https://github.com/michaeljosephroddy/order-service.git'
            }
        }

        stage('Build and Package') {
            steps {
                sh "mvn clean package verify"
        }
}

        stage('Static Code Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                sh "mvn sonar:sonar -Dsonar.projectKey=sonar-project-local -Dsonar.projectName='sonar-project-local'"
            }
        }
}

        stage('Build and Push Docker Image') {
            steps {
                sh 'docker build -t michaelroddy04/online-store-service .'
                 withDockerRegistry([credentialsId: 'dockerhub-credentials', url: '']) {
                    sh 'docker tag online-store-service michaelroddy04/online-store-service'
                    sh 'docker push michaelroddy04/online-store-service'
                 }
            }
        }

        stage('Run Ansible for Automated Deployment') {
            steps {
                sh 'chmod 400 lab1webserverkeypair.pem'
                sh 'ansible-playbook -i inventory.ini deploy.yml'
            }
        }

    }
}