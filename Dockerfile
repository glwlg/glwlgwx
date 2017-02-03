FROM 192.168.3.253:5000/tomcat8:latest
ADD target/glwlgwx /root/apache-tomcat-8.0.41/webapps/glwlgwx
ENTRYPOINT /root/apache-tomcat-8.0.41/bin/catalina.sh run
WORKDIR /root/apache-tomcat-8.0.41/webapps