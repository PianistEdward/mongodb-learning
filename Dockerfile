# 使用官方的 OpenJDK 17 镜像作为基础镜像
FROM openjdk:17-jdk-slim

# 设置应用的工作目录
WORKDIR /david/demo

# 复制jar包到容器中
COPY target/mongo-learning.jar .

# 设置默认的启动命令
ENTRYPOINT ["java", "-jar", "/david/demo/mongo-learning.jar"]
