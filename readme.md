# 1.项目简介 #

这是一个程序oj项目,主要包括两个大的部分的功能,一部分是利用jwt的单点登录服务,另一个部分是程序oj的服务.程序oj部分重点包括代码提交和代码判断两个部分. 单点登录部分生成的jwt这里直接放进了sql数据库中,后续考虑用redis改.

代码提交判断部分目前就做了java, python, c++的远程判题服务.

可以直接运行,也可以将判题服务放进代码沙箱进行,这一部分打包进了out/artifacts/programio_code_jar里面.

# 2.项目运行 #

如果要使用ai帮助debug需要先申请一个ai的api和secretkey,这里使用的是百度千帆ai,将src/main/java/com/example/programiocode/service/AiCodeService.java路径下的api和secretkey改成自己的api就行(直接在application.yml文件里配置似乎会出现问题).

## 2.1直接运行 ##

1.先运行sql文件夹底下的default_sql.sql文件(sql文件的密码是随机设置的,不能直接用作测试);

2.运行两个服务,两个服务目前分别放在localhost:15000和localhost:16000端口上.

## 2.2 用docker作为代码沙箱 ##

1.先运行sql文件夹底下的default_sql.sql文件;

2.构建docker镜像

运行下列命令创建镜像,dockerfile文件中包含三种语言的环境以及programio-code服务:

```bash
docker build --tag programio-code-docker-image --file Dockerfile .
```

如果只创建一个docker镜像的代码沙箱,运行下列命令直接启动docker容器:

```bash
docker run -d -p 15000:15000 --name programio-container programio-code-docker-image
```

## 2.3 用k8s动态管理容器生命周期 ##

1.先运行sql文件夹底下的default_sql.sql文件;

2.构建docker镜像

运行下列命令创建镜像,dockerfile文件中包含三种语言的环境以及programio-code服务,这里就不需要启动docker容器了:

```bash
docker build --tag programio-code-docker-image --file Dockerfile .
```

3.配置k8s管理docker容器,这里使用的是minicube,并采用已经生成的本地镜像programio-code-docker-image:

k8s的相关配置写在了k8sConfiguration.yaml文件中,这里直接运行下列命令即可:

```bash
minikube image load programio-code-docker-image

kubectl apply -f k8sConfiguration.yaml
```





ps:1.代码运行内存检测应该要修改,这一部分有点问题

2.前端页面目前只用html写了一个登录的页面和一个程序提交的页面进行测试
