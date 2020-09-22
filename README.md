# 时空数据混合索引

## 概述
用作快速的时空数据查询所见的索引。其结构参考HR树和MV3DR树的原理，上层使用时间的B树做时间索引，下层使用R*树用作空间索引，
因为是模拟数据数据较离散，所以未实现子树重叠的功能。其核心类在Opeation的CreateIndex和Query。其数据规模在1000W条左右
(GB级)，要求在内存消耗控制在2G以内。

## 工作重点

- 使用hash作为两层索引的链接，减少了中间数据处理带来的开销。

- 测试了底层B树和顶层R树的反转结构，其查询性能和消耗有明显提升。

## 现有问题

- 索引内存消耗过大，其消耗率在数据量的150%左右。（只进行ID索引消耗率在110%左右）

- 混合索引的序列化中由于双层索引结构复杂导致反序列化失败

- 只支持基本范围索引，对移动物体轨迹不支持

## 后续工作

- 对时间的B树的hascode进行优化

- 索引序列化

- 树重叠的判断依据

- 反转结构中时间树无法重复利用，需要有个调节思路

- 参考论文改善模型分裂策略

## 参考文章
[HBase与时空索引技术](http://www.nosqlnotes.com/technotes/hbase/hbase-spatial-index/) 

[空间数据索引RTree（R树）完全解析及Java实现](https://blog.csdn.net/weixin_34343308/article/details/94752051?depth_1-utm_source=distribute.pc_relevant.none-task&utm_source=distribute.pc_relevant.none-task)

[基于Hash、B+、3DR和B*混合索引研究](http://gb.oversea.cnki.net/KCMS/detail/detailall.aspx?filename=1017136194.nh&dbcode=CMFD&dbname=CMFDREF)

[海战场时空查询及索引机制研究](http://www.docin.com/p-439295436.html?docfrom=rrela)

[针对存量时空数据的索引的几种分类方法](https://blog.csdn.net/qiaojialin/article/details/77722960)

以及各类英文文献。
