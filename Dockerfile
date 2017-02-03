FROM 192.168.3.253:5000/centos-tomcat8-jdk1.8:latest
ADD target/glwlgwx /root/apache-tomcat-8.0.41/webapps/glwlgwx
ENV JAVA_HOME /usr/java/jdk1.8.0_121
ENV PATH $PATH:$JAVA_HOME/bin
ENTRYPOINT /root/apache-tomcat-8.0.41/bin/startup.sh && tail -F /root/apache-tomcat-7.0.70/logs/catalina.out
#WORKDIR /root/apache-tomcat-8.0.41/webapps