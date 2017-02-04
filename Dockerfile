FROM reg.meiren.com:5000/centos-tomcat8-jdk1.8:latest
ADD ./target/glwlgwx.war /root/apache-tomcat-8.0.41/webapps/
ENV JAVA_HOME /usr/java/jdk1.8.0_121
ENV PATH $PATH:$JAVA_HOME/bin
ENTRYPOINT /root/apache-tomcat-8.0.41/bin/startup.sh && tail -F /root/apache-tomcat-8.0.41/logs/catalina.out
#WORKDIR /root/apache-tomcat-8.0.41/webapps