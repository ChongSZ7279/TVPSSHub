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
                echo "Running tests..."
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
                echo "Running code quality check..."
                bat 'mvn checkstyle:check'
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

// pipeline {

//     agent any

//     tools {
//         jdk 'jdk-11'
//         maven 'maven-3'
//     }


//     options {
//         timestamps()
//         buildDiscarder(logRotator(numToKeepStr: '10'))
//         timeout(time: 30, unit: 'MINUTES')
//     }

//     environment {
//         MAVEN_OPTS = '-Dmaven.repo.local=.m2/repository'
//         IMAGE_NAME = 'tanyunxi/tvpss'
//         IMAGE_TAG = "${BUILD_NUMBER}"
//         DOCKER_HOST = 'tcp://docker:2375'
//         DOCKER_TLS_CERTDIR = ''
//         DOCKER_CREDENTIALS = 'dockerhub-login'
//     }


//     stages {
//         stage('Checkout') {
//             steps {
//                 checkout scm
//             }
//         }

//         stage('Build') {
//             steps {
//                 sh 'mvn -B -ntp clean compile'
//             }
//         }

//         stage('Test') {
//             steps {
//                 sh 'mvn -B -ntp test'
//             }

//             post {
//                 always {
//                     junit allowEmptyResults: true,
//                           testResults: 'target/surefire-reports/*.xml'
//                 }

//             }

//         }

//         stage('Lint') {
//             steps {
//                 sh '''
//                 echo "Running code quality check..."
//                 mvn checkstyle:check

//                 '''
//             }
//         }

//         // 4. JMETER PERFORMANCE TEST
//         stage('JMeter Test') {
//             steps {
//                 sh '''
//                 echo "Running JMeter Test..."
//                 mkdir -p jmeter-results
//                 jmeter \
//                 -n \
//                 -t tests/performance-test.jmx \
//                 -l jmeter-results/results.jtl \
//                 -e \
//                 -o jmeter-results/report
//                 '''
//             }
//         }

//         stage('Docker Build') {
//             steps {
//                 echo "Building Docker Image..."
//                 sh '''
//                 docker build \
//                 -t $IMAGE_NAME:$IMAGE_TAG \
//                 -t $IMAGE_NAME:latest .
//                 '''
//             }
//         }

//         // 6. DOCKER PUSH
//         stage('Docker Push') {
//             steps {
//                 echo "Pushing Image to Docker Hub..."


//                 withCredentials([usernamePassword(
//                     credentialsId: "${DOCKER_CREDENTIALS}",
//                     usernameVariable: 'DOCKER_USER',
//                     passwordVariable: 'DOCKER_PASS'
//                 )]) {


//                     sh '''

//                     echo $DOCKER_PASS | docker login \
//                     -u $DOCKER_USER \
//                     --password-stdin
//                     docker push $IMAGE_NAME:$IMAGE_TAG
//                     docker push $IMAGE_NAME:latest
//                     '''
//                 }
//             }
//         }

//         // 7. DEPLOY
//         stage('Deploy') {
//             steps {
//                 echo "Deploying Application..."
//                 sh '''
//                 docker pull $IMAGE_NAME:latest
//                 docker stop fyp-app || true
//                 docker rm fyp-app || true
//                 docker run -d \
//                 --name fyp-app \
//                 -p 8080:8080 \
//                 $IMAGE_NAME:latest

//                 '''
//             }
//         }
//     }

//     post {
//         success {
//             echo 'Pipeline completed successfully.'
//         }
//         failure {
//             echo 'Pipeline failed. Check logs.'
//         }

//         always {
//             archiveArtifacts(
//                 artifacts: 'jmeter-results/**',
//                 allowEmptyArchive: true
//             )
//             cleanWs()
//         }
//     }
// }
