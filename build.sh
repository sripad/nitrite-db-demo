mvn package
docker build -t nitrite-db-demo .
docker run -p 8080:8080 -t nitrite-db-demo