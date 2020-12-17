# log-analysis-all
 分布式系统监控（日志分析）中间件
## 系统模块说明
### log-analysis-client
客户端jar包，客户端引入后可截获控制台输出日志，定时上报到该系统
### log-analysis-common
共通包，一些共通实体类和共通方法。
### log-analysis-master-node
master节点工程
### log-analysis-slave-node
slave节点工程
### log-analysis-sample
使用示例

## 构架
### 日志获取
客户端中通过截获控制输出的日志，而非读取log文件的方式，以达到最小入侵。
### 日志存储
* 根据接入项目的projectId来分文件存储，每次写入文件是顺序写，虽然是写文件，但是顺序写具有很高的性能，还达到了持久化的效果
* 一条日志写入成功的标准为超过半数的slave也写入成功，超过半数写入的日志称为commited
* 日志存储中有两个index，一个是最新消息的index，一个是commited日志的index
* 对外可见的只有commited的日志
#### 通信
* 底层采用netty实现客户端与系统的通信，系统间主从节点的通信。
* netty中采用protobuf传输数据，protobuf是一种平台无关、语言无关、可扩展且轻便高效的序列化数据结构的协议
### 高可用
#### 主从架构
* 该系统采用一主多从的架构，日志的写入和读取都通过master节点，slave节点仅作为备份
* 写入日志时采用raft协议保证节点间的数据一致性
* 具体过程为，master节点收到客户的写请求时，先写入自己本地文件，并将状态设为uncommited
* 然后发送uncommited请求给所有slave节点，slave节点收到请求后在自己本地写入文件后，返回ack给master节点
* master节点收到ack后将自己本地的状态改为commited，并发送commited通知给slave节点
* slave节点再将本地状态改为commited。

#### 选举
* 选举算法采用raft算法
* 具体过程为如果slave检测到master宕机，则会尝试发起选举
* 首先产生一个150-300ms的随机数，然后休眠后向其它slave发起投票
* 其它slave收到投票请求后，比较term和已提交的日志index来判断是否投票，比如自己的term或已提交日志index比较新，则不投票。
* 当某个slave收到半数难以上投票后则成为新的master，并向其它slave广播
* 其它slave收到广播消息后将master修改为最新地址，并检查是否需要同步日志，如需求则发起日志同步请求。
* 新master收到日志同步请求后，将slave缺失的数据以增量方式发送回去
* slave接收到数据后写入本地文件，完成同步

#### 日志同步
* 具体同步过程为，由slave发起同步，并带上自己本地存储的所有项目工程的已提交日志index，比如project01对应的commitedIndex为5，project02对应的commitedIndex为8。
* master收到请求后，判断每个工程的已提交日志index和自己本地存储的index，然后把中间的差量以字节方式读取，放入一个字节数组中，并用一个map记录每个工程对应的字节数
* slave收到日志数据后，根据map来判断每个工程需要读取的字节数，从字节数组中读取字节，写入本地文件，完成同步。
* 由于每个slave的最新日志index可能不一样，根据raft协议，会以master的已提交日志index为准，所以需要把超过的部分删除掉。

#### 容灾
* master宕机后，自动发起选举
* 选举成功后自动同步数据
* 老master重连后，自动变为slave并同步最新数据