pipeline {

    agent any

    tools {
        maven 'maven-3'
    }

    options {
        timestamps()
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timeout(time: 30, unit: 'MINUTES')
    }

    environment {
        MAVEN_OPTS = '-Dmaven.repo.local=.m2/repository'
        DOCKER_IMAGE = "ivlyntay/tvpsshub"
    }

    stages {

        stage('Checkout') {
            steps {
                echo "Checking out source code..."
                checkout scm
                bat 'git log -1 --oneline'
            }
        }

        stage('Build') {
            steps {
                echo "Building application..."
                bat 'mvn -B -ntp clean compile'
            }
        }

        stage('Test') {
            steps {
                echo "Running unit tests..."
                bat 'mvn -B -ntp test'
            }

            post {
                always {
                    junit allowEmptyResults: true,
                          testResults: 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Lint') {
            steps {
                echo "Running Checkstyle..."
                bat 'mvn checkstyle:check'
            }
        }

        stage('Performance Test (JMeter)') {
            steps {
                echo "Running JMeter performance test..."

                bat '''
                jmeter -n ^
                -t test/performance-test.jmx ^
                -l target/jmeter-results.jtl ^
                -e ^
                -o target/jmeter-report
                '''
            }

            post {
                always {
                    archiveArtifacts artifacts: 'target/jmeter-report/**', allowEmptyArchive: true
                }
            }
        }

        stage('Deploy') {
            steps {
                echo "Packaging Spring Boot application..."
                bat 'mvn -B -ntp package -DskipTests'
            }
        }

        stage('Build Docker Image') {
            steps {
                echo "Building Docker image..."

                bat '''
                docker build -t %DOCKER_IMAGE%:latest .
                '''
            }
        }

        stage('Tag Docker Image') {
            steps {
                script {

                    env.COMMIT_ID = bat(
                        script: '@echo off\r\ngit rev-parse --short HEAD',
                        returnStdout: true
                    ).trim()

                    bat """
                    docker tag ${DOCKER_IMAGE}:latest ${DOCKER_IMAGE}:${COMMIT_ID}
                    """
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'docker-token',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {

                    bat """
                    echo %DOCKER_PASS% | docker login -u %DOCKER_USER% --password-stdin
                    docker push ${env.DOCKER_IMAGE}:latest
                    docker push ${env.DOCKER_IMAGE}:${env.COMMIT_ID}
                    """
                }
            }
        }
    }

    post {

        success {
            echo "Pipeline completed successfully."
        }

        failure {
            echo "Pipeline failed."
        }

        always {
            cleanWs()
        }
    }
}