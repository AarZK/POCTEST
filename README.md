> 程序用于 poc 测试时造一些简单的并发读、写以及转账等场景

## 配置文件

程序中使用 dbcp 连接池，程序使用的配置文件 dbcp.properties 为连接池使用的一些 jdbc 连接信息。

```properties
########DBCP配置文件##########
#驱动名
driverClassName=com.mysql.jdbc.Driver
#url
url=jdbc:mysql://192.168.1.201:3306/test?useSSL=false
#用户名
username=root
#密码
password=root
#初试连接数
initialSize=30
#最大活跃数
maxTotal=30
#最大idle数
maxIdle=10
#最小idle数
minIdle=5
#最长等待时间(毫秒)
maxWaitMillis=1000
#程序中的连接不使用后是否被连接池回收
#removeAbandoned=true
removeAbandonedOnMaintenance=true
removeAbandonedOnBorrow=true
#连接在所指定的秒数内未使用才会被删除(秒)
removeAbandonedTimeout=5
```

## 调用方式

将程序打包，使用`java -jar`的方式调用 jar 包，唯一参数为配置文件路径

![调用方式.jpg](https://i.loli.net/2019/12/20/5HJ2LjyknMVTrud.jpg)

## 操作菜单说明

### 1.创建表

执行该操作后会在配置文件中指定的库下创建名为 people 的表：

![调用方式.jpg](https://i.loli.net/2019/12/20/4ojvbsXGOz5UwR8.jpg)

> 已有该表的话会被 drop 后重建

### 2.[并发插入](https://github.com/DiUS/java-faker)

执行该操作并根据提示输入后向表中连续并发插入数据，数据内容除 seq 字段递增其他都由 [java-faker](https://github.com/DiUS/java-faker) 随机生成：

![并发插入.jpg](https://i.loli.net/2019/12/20/mXW8fPQ5TvVM37t.jpg)

数据内容如下：

![数据内容.jpg](https://i.loli.net/2019/12/20/leCvLd7GXxsDRUI.jpg)

### 3.查询数据量

获取当前 people 表总记录数

![数据量.jpg](https://i.loli.net/2019/12/20/XfgQyJ5dMAREnZ6.jpg)

### 4.并发查询

并发多次查询 country 字段为随机值的记录数（可能为 0）：

![并发查询.jpg](https://i.loli.net/2019/12/20/wvFJknoHzXUt9dx.jpg)

查询内容如下：

![并发查询内容.jpg](https://i.loli.net/2019/12/20/CTwf5FAjVIJbPdS.jpg)

### 5.创建缩影

people 表原索引如下：

![原索引.jpg](https://i.loli.net/2019/12/20/RB1sv5HWoP6kpY4.jpg)

执行操作 *5* 后：

![新建索引.jpg](https://i.loli.net/2019/12/20/6qmM8nBfew7SF5x.jpg)

### 6.删除索引

删除 *idx_birth* 和 *idx_country* 两个索引

### 7.增加列(初始化)

增加名为 balance 的列，默认值为 10000.00（若已有该列则所有记录的 balance 值全部重置为 10000.00）

原表结构：

![表结构1.jpg](https://i.loli.net/2019/12/20/OavlBNGKyIEVu8n.jpg)

新增列后：

![表结构2.jpg](https://i.loli.net/2019/12/20/279cp1U6SdhnEYT.jpg)

初始化前：

![初始化后.jpg](https://i.loli.net/2019/12/20/Umo1QMIvVrn6zbp.jpg)

初始化后：

![初始化前.jpg](https://i.loli.net/2019/12/20/xCd43vSmlHJ2pXu.jpg)

### 8.删除列

删除 balance 列

### 9.并发转账

多事务并发转账，保证一致性（人均余额并发转账前后不变）

转帐前：

![并发转账.jpg](https://i.loli.net/2019/12/20/IjAxBPwvSmLpnXz.jpg)

转账后：

![并发转账结果.jpg](https://i.loli.net/2019/12/20/qIuDd1hwAH2FYlK.jpg)

### 10.模拟秒杀

执行该操作后会首先初始化一张如下的商品表：

![商品表.jpg](https://i.loli.net/2019/12/20/I4MASYOV5nQcrsU.jpg)

按照提示继续执行，每个线程开启事务模拟抢购，由于 g_store 字段属性为 usigned：

![非负.jpg](https://i.loli.net/2019/12/20/UXP3aJ1R8vV4wcQ.jpg)

商品库存为 0 后剩余线程会失败，事务回滚

### q.退出

退出程序

## SQL效率统计

所有的并发操作最后会返回本次操作的 TPS、QPS和耗时等信息

![统计信息.jpg](https://i.loli.net/2019/12/20/iBOlvsjnRkLKUgN.jpg)



