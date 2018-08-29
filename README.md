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
                   1.-su admin  --切换用户
                     -adduser admin  
                     -passwd admin
                     ------------------------
                   2.添加允许root启动-当次有效：
                     -bin/elasticsearch -Des.insecure.allow.root=true
                   3.添加允许root启动-永久有效：
                     -修改bin/elasticsearch,加上ES_JAVA_OPTS属性：
                     -ES_JAVA_OPTS = "-Des.insecure.allow.root=true"
                   4.在2.3启动时会报错：ERROR：D is not a recognized option
                     -创建用户组：groupadd esgroup
                     -创建用户：  useradd esuser -g esgroup -p espassword
                     -更改es文件夹及内部文件的所属用户及组：
                        -cd /opt
                        -chown -R esuser:esgroup elsaticsearch-6.3.2
                     -切换用户并运行：
                        -su esuser
                        -bin/elsaticsearch 
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
    
**分词安装:**      

    0.分词器，从一串文本中切除一个一个词条，并对每个词条标准化
      组成部分：3个
       .character filter:分词之前的预处理，过滤掉html标签，特殊符号转换等
       .tokenizer：分词
       .token filter：标准化
    1.内置分词器：
        ·standard   会将词汇单元转换为小写形式，去除停用词和标点符号，支持中文方式为单字切分
        ·simple     去掉数字类型字符，词汇统一为小写形式
        ·whitespace 去空格，不支持中文
        ·language   特定语言分词器，不支持中文
        ...
    2.下载位置：https://github.com/medcl/elasticsearch-analysis-ik/releases
      下载对应版本6.3.2，可直接下载已编译后的
    3.在/export/servers/es/elasticsearch-6.3.2/plugins目录下创建文件夹ik
      mkdir ik
      mv elasticsearch-analysis-ik-6.3.2.zip elasticsearch-6.3.2/plugins/ik/
      unzip elasticsearch-analysis-ik-6.3.2.zip
      rm -f elasticsearch-analysis-ik-6.3.2.zip
      重启es --看插件加载日志

      
      
     
**lucene相关:**   
    
    1.全文索引：对文本数据的索引（分词）
    
**Curl命令:**

    1.访问页面：curl www.baidu.com
    2.保存页面：curl -o tt.html www.baidu.com
    3.访问并显示响应头：curl -i www.baidu.com
    4.访问并显示通讯过程：curl -v www.baidu.com
    5.GET/POST/PUT/DELETE请求：curl -X GET/POST/PUT/DELETE url

**根据端口查询进程并杀死：**

    1. 查找占用的程序
    netstat -apn | grep 4040
    tcp        0      0 192.168.150.135:5601        0.0.0.0:*                   LISTEN      7208/./bin/../node/ 
    2. 杀死进程
    kill -9 7208

**ElasticSearch分布式架构解析：**
    
    1.ES分布式架构的透明隐藏特性：
        -自动分片机制
        -集群发现机制：自动加入集群
        -shard负载均衡：保证每个节点访问量几乎相同
        -自动数据路由：新增，查询时自动路由
    2.扩容机制：
        -场景：一个集群，6个节点（6台服务器），每个1T,现在需要扩容至8T
            -垂直扩容：不改变服务器节点数：购置两台2T的替换两台1T的
            -水平扩容：增加两个节点，即两台1T的服务器  --实际常用水平扩容：节省成本
    3.rebablance:增加或移除节点，会自动均衡
    4.master节点：主管创建删除索引，分片分配给节点等，稳定的主节点对集群健 康很重要
    5.节点对等：每个节点都能接收请求，每个节点接收到请求后都能把该请求路由到有相关数据的其他节点上，
        -接受原始请求的节点负责采集数据并返回给客户端
        -每个节点都可以处理请求
    6.分片和副本机制：
        -primary shard -主分片
        -replica shard -备份分片
        -一个索引包含m分片(shard),每个分片进行n次备份--副本
        -每个shard都是最小的工作单元，承载部分数据，每个shard都是一个lucene实例，有完整的建立索引处理请求的能力
        -增减节点时，shard会在nodes中自动负载均衡
        -所有分片数据相加才是所有数据，分片与分片数据不重复
        -备份是主分片的副本，负责容错，以及承担读请求负载
        -主分片的数量在创建索引时就已固定，备份分片数量可随时修改
        -默认primary=5，replica =1，所以默认就有10个shard，5个主，5个备
        -主分片和其备份，不能再同一个节点上：防止节点宕机，丢失主副本分片，起不到容错作用
    7.单节点环境下创建索引分析：--只有主分片没有备份，集群健康状况为yellow
    8.两节点环境下创建索引分析：
        -当单节点集群进行拓展时，新增节点原来没有创建的副本也会开始创建，此时集群健康由yellow变为green
        -当应用程序插入数据时，先到主分片，再有主分片同步至副本，故查询操作也可以走副本查询，减少节点压力
    9.水平扩容过程：
        -es会对shard进行负载均衡：
        -场景：
            node1  1 2 3
            node2  1 2 3
            新增节点后：--会进行自动均衡
            node1   1 3
            node2   2 3
            node3   1 2
        -对以上场景的扩容极限：为6个节点，每个节点放一个shard
        -针对以上场景怎么超出扩容极限？假设要扩容到九台
            由于主分片个数不能修改，故只能修改副本，即
            "number_of_shards": "3",  --不可改
            "number_of_replicas": "1",--可改，此处改为2，即可扩容到9台
        -集群容错性：最多容忍几台服务器宕机
            -相同的分片至少有一个是好的
            -此场景中，3个分片，6个shard
                node1   1 3
                node2   2 3
                node3   1 2
             以上不能同时宕机两台，最多接受1台宕机
            -针对以上场景提高容错性：--可接受宕机台数较多
                -增加副本：number_of_replicas = 2
                node1   1 3 2
                node2   2 3 1
                node3   1 2 3
                -通过增加副本，提高容错性，可接受两台服务器宕机
    10.ES的容错机制：见图
            -服务器宕机
            -重新选举master
            -重启宕机服务...
    11.同一个索引下尽量放相同类型的文档：见图
            减少shard压力
            
**文档id两种生产方式：**

    1.PUT指定：通常是数据导入即，mysql->es
    2.POST自动生成：长度20 使用Guid，能保证并发情况下生成的id不冲突，base64编码，url安全
    
**_source元数据分析：看查询**

**基于groovy脚本执行partial update：见-常用.md**

**POST更新文档的并发处理：**

    --重新获取文档数据及版本信息进行更新
    --retry_on_conflict=3表示更新失败后还可重新查找并更新数据3次
    POST /lib/user/4/_update?retry_on_conflict=3&version=5    
    
**文档数据路由原理：**
    
    1.数据路由;即查询或新增时，将数据添加在了哪个分片，这是由ES决定的，此称为数据路由
    2.路由算法：
        shard=hash(routing) % number_of_pirmary_shards
        -每次增删改都会有一个routing值，默认为文档_id值
        -hash取余，值介于0-（number_of_pirmary_shards-1）之间，文档在对应的shard上
        -routing可以手动指定，可以对负载均衡，提高批量读取性能有帮助


    
    

    
    
    

        