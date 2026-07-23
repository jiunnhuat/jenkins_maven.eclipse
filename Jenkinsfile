pipeline {
    agent any
    tools {
        maven 'Maven'
        jdk 'JDK 17'
    }
    stages {
        stage("clean") {
            steps {
                echo "Start Clean"
                sh "mvn clean"
            }
        }
        stage("test") {
            steps {
                echo "Start Test"
                sh "mvn test"
            }
        }
        stage("build") {
            steps {
                echo "Start build"
                sh "mvn install -DskipTests"
            }
        }
        stage("scan") {
            steps {
                echo "Start scan"
                sh "mvn sonar:sonar"
            }
        }
    }
}