FROM openjdk:17-oracle
# 基础镜像 开发所使用的Java版本 本项目为Java17
#FROM openjdk:17-oracle
# 维护者信息 添加信息
#MAINTAINER wind <wudong@ifengniao.com>
WORKDIR /xiyou/xiyoucloud
#挂载卷 冒号前为宿主机地址 冒号后为需要挂载的卷
#VOLUME /home/xiyou/xiyoucloud:/xiyou/xiyoucloud
# 设置容器时区为当前时区 无需改动
#RUN /bin/cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \&& echo 'Asia/Shanghai' >/etc/timezone
# 复制主机文件至镜像内，复制的目录需放置在 Dockerfile 文件同级目录下 根据自己的项目进行修改
ADD target/xiyou-cloud-0.0.1-SNAPSHOT.jar /xiyou/xiyoucloud/app.jar
# 容器启动执行命令 这个使用指定生效的配置文件用的可以不加 "--spring.profiles.active=test"
ENTRYPOINT ["java", "-XX:MetaspaceSize=256m", "-XX:MaxMetaspaceSize=256m", \
"-Xms2048m", "-Xmx2048m", "-Xmn768m", "-Xss256k", "-XX:SurvivorRatio=8", "-Xlog:gc", \
"-Djava.security.egd=file:/dev/./urandom", "-jar", "/xiyou/xiyoucloud/app.jar", \
"--spring.profiles.active=docker"]
# 声明容器提供服务端口 根据自己的情况修改
#EXPOSE 8001