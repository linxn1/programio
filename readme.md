构建docker镜像
docker build --tag programio-code-docker-image --file Dockerfile .
启动docker容器
docker run -d -p 15000:15000 --name programio-container programio-code-docker-image


配置k8s

minikube image load programio-code-docker-image

kubectl apply -f k8sConfiguration.yaml



