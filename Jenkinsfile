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

        stage('Build with Kaniko') {
                    steps {
                        podTemplate(
                            cloud: 'kubernetes',
                            namespace: 'jenkins',
                            yaml: """
        apiVersion: v1
        kind: Pod
        spec:
          containers:
          - name: kaniko
            image: gcr.io/kaniko-project/executor:v1.9.0-debug
            command:
            - cat
            tty: true
            volumeMounts:
            - name: dockerhub-config
              mountPath: /kaniko/.docker
              readOnly: true
          volumes:
          - name: dockerhub-config
            secret:
              secretName: dockerhub-config
              items:
              - key: .dockerconfigjson
                path: config.json
        """
                        ) {
                            node(POD_LABEL) {
                                checkout scm

                                script {
                                    sh "git config --global --add safe.directory ${env.WORKSPACE}"
                                    def imageTag = sh(returnStdout: true, script: 'git rev-parse --short HEAD').trim()
                                    def fullImageName = "${env.DOCKERHUB_USERNAME}/${env.IMAGE_NAME}:${imageTag}"

                                    container('kaniko') {
                                        sh """
                                        /kaniko/executor --dockerfile=\$(pwd)/Dockerfile --context=\$(pwd) --destination=${fullImageName}
                                        """
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}