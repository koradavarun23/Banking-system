pipeline {
    agent any

    tools {
        maven 'Maven'
    }

    triggers {
        pollSCM('H/2 * * * *')
    }

    environment {
        DEPLOY_DIR = 'C:\\banking-deploy'
        APP_PORT   = '9090'
    }

    options {
        timestamps()
        timeout(time: 20, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                bat 'mvn -B clean compile'
            }
        }

        stage('Test') {
            steps {
                bat 'mvn -B test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Package') {
            steps {
                bat 'mvn -B package -DskipTests'
            }
        }

        stage('Archive') {
            steps {
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }

        stage('Deploy') {
            environment {
                BUILD_ID = 'dontKillMe'
                JENKINS_NODE_COOKIE = 'dontKillMe'
            }
            steps {
                powershell '''
                $ErrorActionPreference = 'Stop'
                $dir  = $env:DEPLOY_DIR
                $port = [int]$env:APP_PORT
                New-Item -ItemType Directory -Force -Path $dir | Out-Null

                Get-NetTCPConnection -LocalPort $port -State Listen -ErrorAction SilentlyContinue |
                    Select-Object -ExpandProperty OwningProcess -Unique |
                    ForEach-Object { Stop-Process -Id $_ -Force }
                Start-Sleep -Seconds 3

                if (Test-Path "$dir\\banking-system.jar") {
                    Copy-Item "$dir\\banking-system.jar" "$dir\\banking-system.previous.jar" -Force
                }

                Copy-Item "target\\banking-system-1.0.0.jar" "$dir\\banking-system.jar" -Force

                Start-Process -FilePath java `
                    -ArgumentList '-jar', "$dir\\banking-system.jar", "--server.port=$port" `
                    -RedirectStandardOutput "$dir\\app.log" `
                    -RedirectStandardError  "$dir\\app-error.log" `
                    -WindowStyle Hidden
                '''
            }
        }

        stage('Health Check') {
            steps {
                powershell '''
                $url = "http://localhost:$($env:APP_PORT)/api/accounts"
                $ok = $false
                for ($i = 0; $i -lt 20; $i++) {
                    try {
                        $r = Invoke-WebRequest -UseBasicParsing $url -TimeoutSec 3
                        if ($r.StatusCode -eq 200) { $ok = $true; break }
                    } catch { }
                    Start-Sleep -Seconds 3
                }
                if (-not $ok) { Write-Host "Health check FAILED for $url"; exit 1 }
                Write-Host "App is healthy at $url"
                '''
            }
        }
    }

    post {
        success { echo "Deployed! Open http://localhost:${env.APP_PORT}" }
        failure { echo 'Pipeline failed' }
        always  { deleteDir() }
    }
}