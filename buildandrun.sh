git pull origin

kill -9 "$(lsof -t -i :8080 -s TCP:LISTEN)"

rm nohup.out

rm build/libs/ChatGradeBot-0.0.1-SNAPSHOT.jar

./gradlew bootJar -x test

nohup java -jar build/libs/ChatGradeBot-0.0.1-SNAPSHOT.jar &