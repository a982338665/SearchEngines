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
        ----
        --由于数据位置计算是由主分片个数进行计算的，所以他不能改变，否则将无法再对应分片找到正确数据
        
**写一致性原理金额qurom：**

    --在进行写操作时，
        -consistency=one    primary_shards-当前主分片下只有要有一个活跃就执行写操作
        -consistency=all    包含所有主副分片必须全部活跃才可以进行写操作
        -consistency=qurom  默认值，大部分shard活跃才能执行
    -qurom机制：
        int((primary+num_of_replica)/2)+1
        ex:
        3个primary，一个replica则至少是 int((3+1)/2)+1=3 个shard活跃
        注意：可能出现shard分配不齐的问题
        1.如果主分片1个，副本一个则活跃shard=2，因为主副本不能在一个节点上，所以依旧不能执行写操作
        2.1主3副，两个节点则活跃shard=3，当活跃个数没有达到要求时，es默认等待一分钟，若在等待期间
          活跃shard的个数没有增加，则显示timeout
        
    PUT /lib/user/1?consistency=one
    {
          "name":"tom",
          "age":25,
          "birthday":"1985-12-12",
          "address":{
              "country":"china",
              "province":"guangdong"
          }
    }         
 
**分页查询中的deep paging问题:**   
    
    1.deep paging：深度查询-比如一个索引有三个primary shard，分别存储了6000条数据，我们需要得到第一百页的数据
      每页10条?
      错误：
        1.需要查询的范围是990-999这十条数据
        2.从每个shard中搜索990-999这十条数据，得到30条数据返回给协调节点进行排序
        3.排序后取十条数据就是要搜索的数据
        --
        4.错误原因：
            -1.排序是根据_score分数确定的
            -2.每个shard查询查来的数据的_score可能层次不齐
            -3.所以按此种方式拿到的数据并不正确
       正确：
        1.每个shard都应该查询出0-999，共3000条数据，返回给协调节点进行排序
        2.取出第一百页的10条数据，返回客户端
    2.deep paging性能问题：
        1.耗费网络带宽，因为搜索过深，各shard要传输大量数据给协调节点，消耗网络
        2.消耗内存，传来的数据会保存在内存中
        3.消耗cpu，内存数据排序消耗cpu
        4.鉴于性能问题，此种应减少使用 

**字符串排序问题:按字母前后**   

    0.ES为非字符串类型的数据创建了正排索引，所以可以排序，对字符串类型的没有创建，所以不能
    1.索引的mapping不可直接修改，删了重建
    2.字符串要排序必须要对字段索引两次：
        -索引分词 -搜索
        -不分词   -排序
    3.mapping创建：
        PUT /lib7
        {
          "settings":{
            "number_of_shards": 3, 
            "number_of_replicas": 0
          },
          "mappings":{
            "user":{
              "properties": {
                "name":{"type": "text","analyzer": "ik_max_word"},
                "address":{"type": "text","analyzer": "ik_max_word"},
                "age":{"type": "integer"},
                "interests":{
                    "type": "text",             --分词搜索
                    "analyzer": "ik_max_word",
                    "fields":{
                        "raw":{
                            "type":"keyword"    --不分词后建立正排索引
                        }
                    },
                    "fielddate":true            --不分词后建立正排索引
                },
                "birthday":{"type": "date"}
              }
            }
          }
        }   
    4.按字符串排序：
    --分词后排序：
        GET /lib7/user/_search
        {
            "query":{
                "match_all":{}    
            },
            "sort":{
                "interests":{
                    "order":"desc"
                }
            }
        }
     --不分词排序：
            GET /lib7/user/_search
            {
                "query":{
                    "match_all":{}    
                },
                "sort":{
                    "interests.raw":{
                        "order":"desc"
                    }
                }
            }    

**相关度分数_score的计算：TF/IDF算法**         

    1.TF(Term Frequency):查询的文本中的词条在document中出现的次数，次数越多相关度越高
        -搜索内容：hello world
        -结果1：hello，hi
        -结果2：hello world
        --显然结果2>1
    2.IDF(Inverse Document Frequency)：查询的文本中的词条在索引的所有文档中出现了多少次，次数越多，相关度越低
        -搜索内容：hello world
        -结果1：hello，hi
        -结果2：my world
        --hello在索引的所有文档中出现了200次，world出现了100次，那么2>1
    3.Field-length:字段长度规约 norm:field越长，相关度越低
    4.查看分数的计算方式：
        GET /lib7/user/_search?explain=true {...}
        GET /lib7/user/_search?_explain {...}

**doc_value解析：**

    1.lucene在构建倒排索引时，会额外建立一个有序的正排索引，此字段则用来表示是否要创建正排索引
    2.存储在磁盘，节省内存
    3.对排序分组，聚合操作的性能提高
    4.默认对不分词字段开启，对分词字段无效-需要把fielddata=true
    5.关闭正排索引：--则该字段无法排序
        PUT /lib7
        {
          "settings":{
            "number_of_shards": 3, 
            "number_of_replicas": 0
          },
          "mappings":{
            "user":{
              "properties": {
                "name":{"type": "text","analyzer": "ik_max_word"},
                "address":{"type": "text","analyzer": "ik_max_word"},
                "age":{
                    "type": "integer",
                    "doc_values":false  --关闭创建正排索引
                 },
                "birthday":{"type": "date"}
              }
            }
          }
        }   

**基于scroll技术滚动搜索大量数据：**
      
      1.若要一次性查出10万数据，性能很差，此时一般会采用scroll滚动查询，一批一批查，直到所有数据查完
      2.原理：scroll查询会在第一次搜索时候，保存一个当时的视图快照，之后根据旧的视图快照提供搜索数据
             如果期间数据变更，是不会让用户看到的  
      3.采用基于_doc进行排序的方式，性能较高
      4.每次发送scroll请求，需指定参数--时间窗口，每次搜索请求只要在这个时间窗口内完成就可以了
      5.示例：
        -1.第一次查询：
            GET /lib/user/_search?scroll=1m     ---指定这批数据在1分钟内查询完成
            {
                "query":{
                    "match_all":{}
                },
                "sort":{"_doc"},                ---不使用_score排序
                "size":3                        ---指明此批数据的查询条数
            }
        -2.执行完第一次查询，结果会返回快照id：scroll_id
        -3.之后的查询：
             GET /lib/user/_search/scroll
             {
                "scroll":"1m",
                "scroll_id":"此处填写上一批查询后返回的scroll_id"
             }

**dynamic mapping策略：动态-**
    
        --dynamic=strict    --报错
        --dynamic=false     --忽略新字段
        --dynamic=true      --创建新字段
        PUT /lib7
        {
          "settings":{
            "number_of_shards": 3, 
            "number_of_replicas": 0
          },
          "mappings":{
            "user":{
              "date_detection":false       --默认为true，表示将日期格式yyyy-mm-dd识别为日期类型，false表示不识别
              "dynamic":"strict"           --表示若在user对象中添加内容时加了新字段，则报错，不会默认创建
              "properties": {
                "name":{"type": "text","analyzer": "ik_max_word"},
                "address":{
                    "type": "object",       --由于address是object类型的，所以其中可以包含其他字段
                    "dynamic":true,         --表示当此字段中添加了新的字段，则去创建新字段
                    "analyzer": "ik_max_word"
                },
              }
            }
          }
        } 
        --动态映射模板：
        PUT /my_index 
        {
            "mappings":{
                "my_type":{
                    "dynameic_templates":{
                        "en":{
                            "match":"*_en",                 --以_en结尾的字段类型
                            "match_mapping_type":"string",  --并且该字段为string类型
                            "mapping":{
                                "type":"text",              --自动创建映射关系type=text
                                "analyzer":"english"        --使用英文分词器 
                            }
                        }
                    }
                }
            }
        }    
        --使用模板：
        PUT /my_index/my_type/5
        {
            "title_en":"this is my dog"
        }
        --未使用模板：
        PUT /my_index/my_type/3
        {
            "title":"this is my cat"
        }
        --查询测试：
        GET my_index/my_type/_search
        {
            "query":{
                "match":{
                    "_all":"is"
                    
                }
            }
        }
        查询结果为id=3的，因为id=3的使用的是标准分词器，倒排索引有这个词条，所以可查询
        而id=5使用的是英文分词器
        英文分词器会将is a an 等英文设置为停用词，所以查询不出来
        
# _重建索引并保证应用程序不重启_   

    1.添加文档-索引mapping未创建，此处自动创建
        PUT /index1/type/1
        {
            "content":"2018-02-05"  --会被默认识别为date类型，实际是text类型
        }
    2.查看mapping
        GET /index1/type/_mapping   --查询可知content：date
    3.content添加text类型报错
        PUT /index1/type/1
        {
            "content":"i am teacher"  
        }
    4.适应需求，需要改date类型为string类型：--报错：表示一旦创建则不能修改mapping字段类型
        PUT /index1/_mapping/type
        {
            "properties":{
                "content":{
                    "type":"text"
                }
            }
        }
    5.解决方式：新建索引，类型确定为string，将旧索引数据导入新索引中
      问题：若新建索引，那么应用程序使用的是原来索引，就会导致要重启应用程序，为了不重启应用，
            将采用别名方式
    6.解决具体步骤：
        --为旧索引创建别名：
            PUT /index1/_alias/index2
        --创建新索引：
            PUT /newIndex
            {
                "mappngs":{
                    "type1":{
                        "properties":{
                            "content":{
                                "type":text
                            }
                        }
                    }
                }
            }
        --旧索引数据导入新索引中：--考虑到原索引数据量很大
        --使用scroll方式查询，使用bulk批量添加，进行索引导入
            ...     
        --把新的索引和别名进行关联：
            POST /_aliases
            {
                "actions":[
                    {"remove":{"index":"index1","alias":"index2"}},
                    {"add":{"index":"newindex","alias":"index2"}},
                ]
            }
        --使用别名进行查询，已切换
            GET index2/type1/_search

**ES数据备份与恢复:_snapshot**   

    1.指定备份仓库：在es下的config/elasticsearch.yml文件中加入：--可指定多个，逗号隔开
        ·docker启动的ES要写容器内部的路径：
            path.repo: ["/usr/share/elasticsearch/data"]
        ·直接启动的仓库指定：
            path.repo: ["/mount/backups", "/mount/longterm_backups"]
    2.创建仓库：
        PUT /_snapshot/my_back HTTP/1.1
        Host: 127.0.0.1:9200
        Content-Type: application/json
        Cache-Control: no-cache
        Postman-Token: 62b94792-c6c7-4f4e-66aa-a5f1d3e621a4
        
        
        {
            "type": "fs", 
            "settings": {
                "location": "/usr/share/elasticsearch/data/my_backup" 
            }
        }
    3.查看已创建仓库：
        GET /_snapshot/my_backup HTTP/1.1
        Host: 127.0.0.1:9202
        Cache-Control: no-cache
        Postman-Token: a441a868-3ac1-224d-dbdf-547e63f7206d
    4.数据备份全部索引--同步等待:同步参数wait_for_completion=true
        PUT /_snapshot/my_backup/snapshot_1?wait_for_completion=true HTTP/1.1
        Host: 127.0.0.1:9202
        Cache-Control: no-cache
        Postman-Token: 33251f23-5993-0aa9-8021-4dfc2d6352f4
        Content-Type: multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW
        
        {
            "snapshot": {
                "snapshot": "snapshot_1",
                "uuid": "FgoGhRGpRJaDHIKBykNGUA",
                "version_id": 5050299,
                "version": "5.5.2",
                "indices": [
                    "index_name",
                    "index_name2",
                    "index_name3"
                ],
                "state": "SUCCESS",
                "start_time": "2018-09-04T07:01:36.509Z",
                "start_time_in_millis": 1536044496509,
                "end_time": "2018-09-04T07:01:37.141Z",
                "end_time_in_millis": 1536044497141,
                "duration_in_millis": 632,
                "failures": [],
                "shards": {
                    "total": 15,
                    "failed": 0,
                    "successful": 15
                }
            }
        }
    5.数据备份全部索引--异步后台
        PUT /_snapshot/my_backup/snapshot_2 HTTP/1.1
        Host: 127.0.0.1:9202
        Cache-Control: no-cache
        Postman-Token: b23d8249-b4f3-7dcf-f438-351adc226988
        Content-Type: multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW
        
        {
            "accepted": true
        }
    6.数据备份部分索引--指定索引：备份名称
        PUT /_snapshot/my_backup/skykingkong_es_testnf HTTP/1.1
        Host: 127.0.0.1:9202
        Cache-Control: no-cache
        Postman-Token: 09c0f7d4-7348-c04f-b229-0991840f73f5
        
        {
            "indices": "skykingkong_es_testnf"
        }
    7.查看指定备份信息：
        GET /_snapshot/my_backup/test-20180824 HTTP/1.1
        Host:127.0.0.1:9202
        Cache-Control: no-cache
        Postman-Token: fe361f1a-7b6f-ed57-444c-06b6a128d97c
    7.查看指定备份信息-detail：
        GET /_snapshot/my_backup/snapshot_3/_status HTTP/1.1
        Host: 127.0.0.1:9202
        Cache-Control: no-cache
        Postman-Token: c06d9221-ae57-cd47-9365-c3d650087a85
    8.查看所有备份信息-all：
        GET /_snapshot/my_backup/_all HTTP/1.1
        Host: 127.0.0.1:9202
        Cache-Control: no-cache
        Postman-Token: 73382292-439b-ff5a-55c2-2b384fa43507
    9.删除备份信息
        DELETE /_snapshot/my_backup/snapshot_3 HTTP/1.1
        Host: 127.0.0.1:9202
        Cache-Control: no-cache
        Postman-Token: 2c82ab5b-f0d4-a220-ff43-28967ecf0c54
        Content-Type: multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW
    10.删除原来索引：
        DELETE /test-20180824 HTTP/1.1
        Host: 127.0.0.1:9202
        Cache-Control: no-cache
        Postman-Token: 08e3bfdb-99d4-eede-b0bd-e3f1b46fd73e
        Content-Type: multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW
    11.关闭索引：_snapshot只能对关闭的索引进行数据恢复
        POST /skykingkong_es_testnf/_close HTTP/1.1
        Host: 127.0.0.1:9202
        Content-Type: application/json
        Cache-Control: no-cache
        Postman-Token: a6f6bc34-61df-1f1b-b776-800f1db4eff5
    12.索引恢复：--灾备：一般而言es使用分片备份足够，特大灾备使用_snapshot
        POST /_snapshot/my_backup/skykingkong_es_testnf2/_restore?wait_for_completion=true HTTP/1.1
        Host: 127.0.0.1:9202
        Content-Type: application/json
        Cache-Control: no-cache
        Postman-Token: 1671a8b5-8d6d-3964-be6f-8cdd2bcc222f
        {
            "indices": "skykingkong_es_testnf"
        }
            
    


        
        