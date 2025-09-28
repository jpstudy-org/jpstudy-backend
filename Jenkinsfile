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
      image: docker:20.10.17
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
        DOCKERHUB_USERNAME = ''
        DOCKERHUB_CREDENTIALS_ID = ''
        REPO_NAME = 'jpstudy'
        INFRA_REPO_URL = ''
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
    }
}