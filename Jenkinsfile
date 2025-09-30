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
      image: gradle:jdk21
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

        stage('Update Manifests') {
            steps {
                container('gradle') {
                    script {
                        sh "git config --global --add safe.directory ${env.WORKSPACE}"

                        def imageTag = sh(returnStdout: true, script: 'git rev-parse --short HEAD').trim()
                        def fullImageName = "${env.DOCKERHUB_USERNAME}/${env.IMAGE_NAME}:${imageTag}"

                        withCredentials([sshUserPrivateKey(credentialsId: 'jenkins_token', keyFileVariable: 'GIT_SSH_KEY')]) {
                            sshagent(['github-ssh-key-for-infra']) {
                                sh 'rm -rf infra'
                                sh "git clone ${INFRA_REPO_URL} infra"
                                dir('infra') {
                                    sh """
                                    sed -i 's|image: .*${env.IMAGE_NAME}.*|image: ${fullImageName}|g' ${env.MANIFEST_PATH}
                                    """
                                    sh 'git config --global user.email "jenkins@ci.bot"'
                                    sh 'git config --global user.name "Jenkins CI Bot"'
                                    sh 'git add .'
                                    sh "git commit -m \"Update Backend image to ${imageTag}\""
                                    sh 'git push origin main'
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}