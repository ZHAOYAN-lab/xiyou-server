docker run -d --name postgres --restart=always -p 5432:5432 -e POSTGRES_PASSWORD=xiyou0#00Admin -e ALLOW_IP_RANGE=0.0.0.0/0 -v /xiyou/postgres/data:/var/lib/postgresql/data postgres

docker run -d --name postgis --restart=always -p 5432:5432 -e POSTGRES_PASSWORD=xiyou0#00Admin -e ALLOW_IP_RANGE=0.0.0.0/0 -v /xiyou/postgis/data:/var/lib/postgresql/data postgis/postgis

docker run -d --name tdengine --restart=always -p 6030:6030 -p 6041:6041 -p 6043-6049:6043-6049 -p 6043-6049:6043-6049/udp -v /xiyou/tdengine/taos:/var/lib/taos tdengine/tdengine
https://docs.taosdata.com/develop/connect/#!

docker run -d --name influxdb --restart=always -p 8086:8086 -v /xiyou/influxdb/lib:/var/lib/influxdb2 -v /xiyou/influxdb/etc:/etc/influxdb2 influxdb
influxdb xiyou0#00Admin ifengniao xiyou
-Nm3whjoORTj5fTTUdJP9yxCIHB3FYcgKcDhcfk-lLK0fRTzxGIotG5caxqW7jBYVcZI_jVKN5AhpCpbbU4Row==

docker run -d --name redis --restart=always -p 6379:6379 -v /xiyou/redis/data:/data -v /xiyou/redis/conf/redis.conf:/etc/redis/redis.conf redis redis-server /etc/redis/redis.conf

docker run -d --name emqx --restart=always -p 1883:1883 -p 8883:8883 -p 8083:8083 -p 8084:8084 -p 18083:18083 emqx/emqx

docker run -d --name nginx --restart=always -p 80:80 -p 443:443 -v /xiyou/nginx/nginx.conf:/etc/nginx/nginx.conf -v /xiyou/nginx/conf.d:/etc/nginx/conf.d -v /xiyou/nginx/log:/var/log/nginx -v /xiyou/nginx/html:/usr/share/nginx/html nginx