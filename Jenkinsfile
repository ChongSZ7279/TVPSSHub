pipeline {
    agent any

    tools {
        jdk 'jdk-11'
        maven 'maven-3'
    }

    options {
        timestamps()
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timeout(time: 30, unit: 'MINUTES')
    }

    environment {
        MAVEN_OPTS = '-Dmaven.repo.local=.m2/repository'

        IMAGE_NAME = 'your-dockerhub-username/fyp-app'
        IMAGE_TAG = "${BUILD_NUMBER}"

        DOCKER_HOST = 'tcp://docker:2375'
        DOCKER_TLS_CERTDIR = ''
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }


        // 1. BUILD STAGE
        stage('Build') {
            steps {
                sh 'mvn -B -ntp clean compile'
            }
        }


        // 2. TEST STAGE
        stage('Test') {
            steps {
                sh 'mvn -B -ntp test'
            }

            post {
                always {
                    junit allowEmptyResults: true,
                          testResults: 'target/surefire-reports/*.xml'
                }
            }
        }


        // 3. LINT STAGE
        stage('Lint') {
            steps {
                sh '''
                    echo "Running code quality check..."

                    mvn checkstyle:check
                '''
            }
        }


        // 4. DEPLOY STAGE
        stage('Deploy') {

            steps {

                echo "Building Docker Image..."

                sh '''
                    docker build \
                    -t $IMAGE_NAME:$IMAGE_TAG \
                    -t $IMAGE_NAME:latest .
                '''


                echo "Pulling Docker Image..."

                sh '''
                    docker pull nginx:latest
                '''


                echo "Running JMeter Performance Test..."

                sh '''
                    mkdir -p jmeter-results

                    jmeter \
                    -n \
                    -t tests/performance-test.jmx \
                    -l jmeter-results/results.jtl \
                    -e \
                    -o jmeter-results/report
                '''
            }
        }

    }


    post {

        success {
            echo 'Pipeline completed successfully.'
        }


        failure {
            echo 'Pipeline failed. Check logs.'
        }


        always {

            archiveArtifacts(
                artifacts: 'jmeter-results/**',
                allowEmptyArchive: true
            )

            cleanWs()
        }
    }
}