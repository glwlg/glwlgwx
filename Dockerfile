FROM 192.168.3.253:5000/tomcat8:latest
ADD target/glwlgwx /root/apache-tomcat-8.0.41/webapps/glwlgwx
ADD /apps/jdk-8u121-linux-x64.rpm /jdk-8u121-linux-x64.rpm
RUN rpm -ivh /jdk-8u121-linux-x64.rpm
ENV JAVA_HOME /usr/java/jdk1.8.0_121
ENV PATH $PATH:$JAVA_HOME/bin
ENTRYPOINT /root/apache-tomcat-8.0.41/bin/catalina.sh run
WORKDIR /root/apache-tomcat-8.0.41/webapps