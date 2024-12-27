kill -9 "$(lsof -t -i :8080 -s TCP:LISTEN)"

rm nohup.out

./gradlew bootJar -x test

nohup java -jar build/libs/ChatGradeBot-0.0.1-SNAPSHOT.jar &