# SearchEngines

**搜索引擎--**

**_linux安装ES参考位置__：http://www.maoxiangyi.cn/index.php/archives/541/**
# _1.ElasticSearch_

**1.简介：**
    
    1.基于Apache lucene构建的看开源搜索引擎(lucene的复杂性)
    2.java编写，提供简单易用的resultful api
    3.轻松的横向扩展，可支持GB-TB—PB级的结构化或非结构化数据处理
    
**2.应用场景：**
    
    1.海量数据分析引擎
    2.站内搜索引擎
    3.数据仓库-分布式存储能力
    ————————
    1.实时分析公众对文章的回应
    2.站内实时搜索     
    3.实时日志监控平台 -- 百度
    
**3.前置技能**

    1.maven构建项目
    2.springboot的使用
    
**4.环境要求**

    1.idea
    2.jdk1.8+ --以ES最新版为例
    3.maven，nodejs环境6.0以上
    
**5.安装：**

    1.版本历史：1.x - 2.x -5.x
    2.选择最新版本的安装：
    3.下载地址：https://www.elastic.co/downloads/elasticsearch
    
    4.windows安装：
        1.在以上地址下载zip文件解压并进入目录：F:\install\ES\elasticsearch-6.3.2\bin
        2.> cd elasticsearch-5.1.0/bin  --进入目录
          > elasticsearch               --启动
        3.测试安装：localhost:9200来检查服务器是否已启动并正在运行，返回一个JSON对象
        4.默认端口修改：更改bin目录中的elasticsearch.yml文件中的http.port字段值来更改端口
        -----
    5.linux安装：--参见install-true文件
        1.地址如3
        2.解压启动：
            ./bin/elasticsearch
            ./bin/elasticsearch -d 后台运行方式启动
        3.错误：
            ·rg.elasticsearch.bootstrap.StartupException: java.lang.RuntimeException: can not run elasticsearch as root
                -不能以root用户启动
                -su admin  --切换用户
                -adduser admin  
                - passwd admin 
            ·/es/elasticsearch-6.3.2/config/jvm.options
                -没有jvm文件的权限，改一下所属的用户，注意执行以下命令需要切换到root执行  
                -chown admin /es/elasticsearch-6.3.2 -R
            · requires kernel 3.5+ with CONFIG_SECCOMPandCONFIG_SECCOMP_FILTERcompiledinatorg
                -警告linux版本太低不影响使用
            ·curl "http://127.0.0.1:9200" 能够正常访问，可是使用外网ip就提示拒绝链接
                -vim config/elasticsearch.yml
                -增加：network.host: 0.0.0.0
                -重启问题解决
            ·max virtual memory areas vm.max_map_count [65530] is too low, increase to at least [262144]
                -解决办法：切换root账户 vim /etc/sysctl.conf
                 -增加一行  vm.max_map_count=655360
                 -接着执行 sysctl -p
            ·max number of threads [1024] for user [lish] likely too low, increase to at least [2048]
                -解决：切换到root用户，进入limits.d目录下修改配置文件。
                -vi /etc/security/limits.d/90-nproc.conf 
                -修改如下内容：
                    * soft nproc 1024
                     #修改为
                    * soft nproc 2048
        4.常用命令：
            1.rpm -qa | grep ssh 可以看到系统中ssh安装包
            2.ps -ef | grep ssh查看ssh服务有没有运行
            3.service sshd start    --运行ssh
            4.netstat -ntlp         --ssh服务的网络连接情况
            ---
            如果还是无法远程连接：
                1.查看防火墙：
                    service iptables status
                2.查看本机windows网络是否开启(Vmnet1 Vmnet8)
        5.测试：
            http://192.168.150.133:9200/?pretty
            {
              "name" : "node03",
              "cluster_name" : "myes",
              "cluster_uuid" : "55u9gaJKSruJXpoXwSEXIw",
              "version" : {
                "number" : "6.3.2",
                "build_flavor" : "default",
                "build_type" : "tar",
                "build_hash" : "053779d",
                "build_date" : "2018-07-20T05:20:23.451332Z",
                "build_snapshot" : false,
                "lucene_version" : "7.3.1",
                "minimum_wire_compatibility_version" : "5.6.0",
                "minimum_index_compatibility_version" : "5.0.0"
              },
              "tagline" : "You Know, for Search"
            }
**6.ES-head安装：**

    0.cd /export/servers/es
    1.su root
    2.wget https://github.com/mobz/elasticsearch-head/archive/master.zip
    3.unzip master   解压为elasticsearch-head-master
    4.cd /export/servers/es/elasticsearch-head-master
    
    ------
    解压创建链接
    5.下载npm包：wget --no-check-certificate  https://nodejs.org/dist/v8.9.1/node-v8.9.1-linux-x64.tar.xz
    6.tar xf node-v8.9.1-linux-x64.tar.xz
    8.ln -s /usr/local/node-v8.9.1-linux-x64/bin/node /usr/local/bin/node
    9.ln -s /usr/local/node-v8.9.1-linux-x64/bin/npm /usr/local/bin/npm
    
    5.安装nodejs：yum install -y nodejs
    5.检查node环境：node -v   
    6.cd /export/servers/es/elasticsearch-head-master
    6.npm install   --node的包安装工具
        --报错：Failed at the phantomjs-prebuilt@2.1.16 install script.
        --使用命令：npm install phantomjs-prebuilt@2.1.14 --ignore-scripts
    7.npm run start --启动
        --报错： --报错：>> Local Npm module "grunt-contrib-jasmine" not found. Is it installed?
        --安装模块： npm install grunt-contrib-jasmine
    8.测试:localhost:9100
    
    ------
    启动ES之前需要修改配置：vim bin/elasticsearch.yml 添加：--解决跨域
        http.cors.enabled: true
        http.cors.allow-origin: "*"
    0.关闭防火墙
    1.启动es
    2.启动插件
    3.浏览器验证：head中输入es连接
    
    --卸载node，npm+++++++++++++++++++++++++++++
    yum remove nodejs npm -y
    +++++++++++++++++++++++++++++++++++++++++++
    
**7.ES分布式安装：**
    
    1.ls -a 显示隐藏文件
    -----
    2.主要修改位置：config/elasticsearch.yml文件，具体内容看文件夹
    -----
    
**8.使用：**
    
    1.节点 是 Elasticsearch 运行的实例。集群 是一组有着同样cluster.name的节点，它们协同工作，互相分享数据，
      提供了故障转移和扩展的功能。当然一个节点也可以是一个集群。
    2.与ES的通讯：
        1.使用java：Elasticsearch 内置了两个客户端
            节点客户端: 负责将请求转发到正确的节点上
                    节点客户端以一个 无数据节点 的身份加入了一个集群。
                    换句话说，它自身是没有任何数据的，但是他知道什么数据在集群中的哪一个节点上，
                    然后就可以请求转发到正确的节点上并进行连接。
            传输客户端: 
                    更加轻量的传输客户端可以被用来向远程集群发送请求。
                    他并不加入集群本身，而是把请求转发到集群中的节点
            两个客户端都使用 Elasticsearch 的 传输 协议，
                1.通过9300端口与 java 客户端进行通信
                2.集群中的各个节点也是通过9300端口进行通信
                3.若这个端口被禁止了，那么你的节点们将不能组成一个集群。
                4.Java 的客户端的版本号必须要与 Elasticsearch 节点所用的版本号一样，不然他们之间可能无法识别
        2.使用其他语言：
                通过9200端口与 Elasticsearch 的 RESTful API 进行通信
                可以使用行命令 curl 来与 Elasticsearch 通信
    3.相关概念：
        1.集群：同一个集群下的多个节点服务
        2.节点：服务
        3.索引：含有相同属性的文档集合                            --等同于sql中的database
        4.类型：索引可以定义一个或多个类型，文档必须属于一个类型    --等同于sql里的table
        5.文档：是可以被索引的基本数据单位                        --等同于sql里的具体记录
        6.分片：每个索引都有多个分片，每个分片是一个Lucene索引
        7.备份：拷贝一份分片就完成了分片的备份
        ----
        假设一个索引(数据库)的数据量很大，会造成硬盘压力过大，使得搜索速度下降，分片能够分摊访问压力
        分片还允许用户水平拆分，及分布式操作
            
        备份好处：
        当一个主分片故障时，备份分片可代替工作，提高可用性，还支持搜索。
        ES在创建索引时，会默认创建5个分片，一个备份，此数量可修改
        分片数量在创建索引时需要指定，后期不可修改
    4.基本用法：
        1.API基本格式：http://<ip>:<port>/<索引>/<类型>/<文档id>
        2.常用http动词：GET/PUT/POST/DELETE
        -------------------------------------
        创建索引：
        打开head插件---点击新建索引：索引名小写切不能有中划线
         
**ES翻译文档:https://es.xiaoleilu.com/010_Intro/05_What_is_it.html**

**ElasticSearch配置说明：**
        
        1.配置文件位于config目录：--见目录
            ·elasticsearch.yml
            ·jvm.options
            ·log4j2.propertoes
        2.ES的Dev和Pro模式说明：
           1.以transport地址是否绑在localhost/127.0.0.1为判断标准network.host
           2.dev模式启动会warning的方式提供配置检查异常
           3.pro模式启动会以error方式提示配置检查异常并退出-->即需要修改network.host
        3.参数修改的第二种方式：
            bin/elasticsearch -Ehttp.port=19200
        4.ElasticSearch本地启动集群的方式：
            1.bin/elasticsearch -默认启动方式：9200
            -- 注意path.data在同台机器下不能再同一路径下
            2.bin/elasticsearch -Ehttp.port=8200 -Epath.data=node2
            3.bin/elasticsearch -Ehttp.port=9200 -Epath.data=node3
            -- 依次启动使用浏览器验证节点集群信息
            4.127.0.0.1:8200/_cat/nodes?v
            -- 查看集群状态浏览器输入
            5.127.0.0.1:8200/_cluster/stats
            
**Kibana的安装与运行：**

    1.www.elastic.co/downloads/kibana --下载相应版本
        --uname -a 查看当前系统版本
    2.tar -zxvf kibana-6.3.2-linux-x86_64
    3.vim config/kibana.yml
      Set elasticsearch.url to point at your Elasticsearch instance
      elasticsearch.url=192.168.150.134:9200
    4.bin/kibana    --运行
        
    ——————————
    5.kibana配置说明:/config文件夹下
        ·kibana.yml关键配置：
            -server.host/server.port 访问kibana用的地址端口，如需外网访问，则此处应修改
            -elasticsearch.url 待访问的es地址
    6.kibana常用功能：
        ·Discover数据搜索查看
        ·Visualize图表制作
        ·Dashboard仪表盘制作
        ·Timelion时序数据的高级可视化分析
        ·DevTools开发者工具
        ·Managerment-kibana配置管理
    7.ES常用术语：
        ·Document文档数据
        ·Index索引
        ·Type索引中的数据类型
        ·Field字段，文档的属性
        ·Query DSL查询语法
    8.ElasticSearch CRUD
        ·create 文档
        ·read   文档
        ·update 文档
        ·delete 文档
    9.
        