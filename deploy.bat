@echo off
echo [1/3] Cleaning project...
call .\gradlew clean

echo [2/3] Building bootJar...
call .\gradlew bootJar

echo [3/3] Deploying Docker containers...
docker-compose down
docker-compose up --build -d

echo Deployment complete.
pause
