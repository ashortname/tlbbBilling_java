# billing_java
这是一个用 java 编写的验证程序  

# 参考了[@liuguang](https://github.com/liuguangw/billing_go)的程序

# 用到的lib  
  -  fastjson-1.2.59.jar  
  -  mysql-connector-java-5.1.46-bin.jar

# 环境要求  
  -  JDK1.8  
# 配置文件  
  -  config.json  
  ```
  {
    "ip": "127.0.0.1",//billing服务器的ip，默认127.0.0.1即可
    "port": 12680,//billing服务器监听的端口(与服务器配置的端口相同)
    "db_host": "127.0.0.1",//MySQL服务器的ip或者主机名
    "db_port": 3306,//MySQL服务器端口
    "db_user": "root",//MySQL用户名
    "db_password": "root",//MySQL密码
    "db_name": "web",//账号数据库名(一般为web)
    "allow_old_password": false,//只有在老版本MySQL报old_password错误时,才需要设置为true
    "auto_reg": true,//用户登录的账号不存在时,是否引导用户进行注册
    "allow_ips": [],//允许的服务端连接ip,为空时表示允许任何ip,不为空时只允许指定的ip连接。配置如["192.168.1.1","192.168.1.2"]，IP之间以逗号隔开并用双引号括起
    "transfer_number": 1000 //兑换参数，有的版本可能要设置为1才能正常兑换,有的则是1000
    "isLog": true,//是否开启日志
    "allow_point": true//是否开启点数兑换功能
}
  ```
# 如何运行  
  -  [点此下载或自行编译jar包](https://github.com/ashortname/billing_java/releases)：  
```bash
# 进入jar包所在目录  
java -jar [包名].jar
```
