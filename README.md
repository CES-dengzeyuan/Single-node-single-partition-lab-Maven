# Single-node-single-partition-lab-Maven

基于CMU的BenchBase，抽取不同工作负载下的TPC-C、YCSB的loader和worker对应的SQL语句 Ref:https://github.com/CES-dengzeyuan/Benchbase_FetchSQL

自建模型，测试单副本单分区条件下，不同工作负载、不同Batch_size条件下，系统吞吐量及事务延迟的变化趋势。

Maven 中心仓库: https://mvnrepository.com/

AllInOne 为最上层架构，下层由FetchSQL、FixSQL、ExecuteSQL组成：
- FetchSQL提取BenchBase中的SQL语句，支持TPC-C和YCSB两个数据集，生成两个sql文件（Loader & Worker）文件以EOL切分事务内的操作，以EOLEOF切分Txn中的事务
- FixSQL处理抽取出的sql文件，返回两个对应的list/array
- ExecuteSQL获取上一步的list/array，并执行相应测试