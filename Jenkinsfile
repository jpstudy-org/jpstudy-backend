pipeline {
    agent {
        kubernetes {
            yaml """
apiVersion: v1
kind: Pod
metadata:
    labels:
      jenkins-build: "true"
spec:
    containers:
    - name: gradle
      image: eclipse-temurin:21-jdk
      command:
      - cat
      tty: true
    - name: docker
      image: docker:git
      command:
      - cat
      tty: true
      volumeMounts:
        - name: docker-sock
          mountPath: /var/run/docker.sock
    volumes:
      - name: docker-sock
        hostPath:
          path: /var/run/docker.sock
            """
        }
    }

    environment {
        DOCKERHUB_USERNAME = 'chinoel'
        DOCKERHUB_CREDENTIALS_ID = 'DockerHub'
        REPO_NAME = 'jpstudy'
        INFRA_REPO_URL = 'git@github.com:jpstudy-org/infra.git'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Set Dynamic Variables') {
            steps {
                script {
                    echo "This build is for branch: '${env.BRANCH_NAME}'"

                    if (env.BRANCH_NAME == 'main') {
                        env.IMAGE_NAME = 'jpstudy-backend'
                        env.MANIFEST_PATH = 'apps/backend/prod/deployment.yaml'
                    }
                    else if (env.BRANCH_NAME == 'dev' || env.BRANCH_NAME == null) {
                        env.IMAGE_NAME = 'jpstudy-backend-dev'
                        env.MANIFEST_PATH = 'apps/backend/dev/deployment.yaml'
                    }
                    else {
                        error "Unsupported branch"
                    }
                }
            }
        }

        stage('Build & Test') {
            steps {
                container('gradle') {
                    sh 'chmod +x ./gradlew'
                    sh './gradlew clean build'
                }
            }
        }

        stage('Build & Push Image') {
            steps {
                container('docker:git') {
                    script {

                        sh 'git config --global --add safe.directory ${env.WORKSPACE}'

                        def imageTag = sh(returnStdout: true, script: 'git rev-parse --short HEAD').trim()
                        def fullImageName = "${env.DOCKERHUB_USERNAME}/${env.IMAGE_NAME}:${imageTag}"

                        withCredentials([usernamePassword(credentialsId: env.DOCKERHUB_CREDENTIALS_ID, usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                            sh "echo ${DOCKER_PASS} | docker login -u ${DOCKER_USER} --password-stdin"
                        }

                        echo "Building Docker image: ${fullImageName}"
                        sh "docker build -t ${fullImageName} ."

                        echo "Pushing Docker image: ${fullImageName}"
                        sh "docker push ${fullImageName}"
                    }
                }
            }
        }


    }
}