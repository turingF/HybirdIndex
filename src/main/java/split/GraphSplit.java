package split;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 图拆分程序，根据数据图的顶点，边文件与划分个数将图划分为多个子图。
 *  input：数据图点文件，边文件。
 * output：划分后包含核心点，扩展点，核心边，扩展边四个文件。
 *
 * @author kg-hb
 *
 */
public class GraphSplit {

	/**
	 * 图节点文件
	 */
	//private static final String SRC_NODE_FILE = "/home/kg-hb/workspace/原始图/nodes.txt";
	//private static final String SRC_NODE_FILE = "C:/Users/MrArche/Desktop/假期工作/项目测试数据/图-性能测试数据/原始数据/nodesTranslated.txt";
	private static final String SRC_NODE_FILE = "./File/myNode.json";

	/**
	 * 图边文件
	 */
	//private static final String SRC_EDGE_FILE = "/home/kg-hb/workspace/原始图/edges.txt";
	private static final String SRC_EDGE_FILE = "./File/edges.txt";
	//private static final String SRC_EDGE_FILE = "C:/Users/MrArche/Desktop/图-性能测试数据/原始数据/edgesTest.txt";
	/**
	 * 拆分后的核心节点文件前缀
	 */
//	private static final String DST_CORE_NODE_FILE_PREFIX = "/home/kg-hb/workspace/原始图/拆分结果/core_node_";
	private static final String DST_CORE_NODE_FILE_PREFIX = "./File/core_node_";

	/**
	 * 拆分后的扩展节点文件前缀
	 */
//	private static final String DST_EXTEND_NODE_FILE_PREFIX = "/home/kg-hb/workspace/原始图/拆分结果/extend_node_";
	private static final String DST_EXTEND_NODE_FILE_PREFIX = "./File/extend_node_";

	/**
	 * 拆分后的核心边文件前缀
	 */
//	private static final String DST_CORE_EDGE_FILE_PREFIX = "/home/kg-hb/workspace/原始图/拆分结果/core_edge_";
	private static final String DST_CORE_EDGE_FILE_PREFIX = "./File/core_edge_";

	/**
	 * 拆分后的扩展边文件前缀
	 */
//	private static final String DST_EXTEND_EDGE_FILE_PREFIX = "/home/kg-hb/workspace/原始图/拆分结果/extend_edge_";
	private static final String DST_EXTEND_EDGE_FILE_PREFIX = "./File/extend_edge_";

	/**
	 * 拆分节点数
	 */
	private static final int MACHINE_NUM = 3;

	/**
	 * 节点缓存信息（点划分时加载，边划分时使用）
	 */
	private static final Map<String, String> NODE_CACHE = new HashMap<>();


	//TODO：重新编写hashcode方法，优化分割策略
	public static int nodeToMachine(String nodeId) {
		return Math.abs(nodeId.hashCode() % MACHINE_NUM) + 1;
	}


	public static void nodeSplit() {
		try {
			BufferedReader nodeReader = new BufferedReader(new InputStreamReader(new FileInputStream(SRC_NODE_FILE), "utf-8"));
			BufferedWriter[] coreNodeWrites = new BufferedWriter[MACHINE_NUM];
			String line = null;
			int lineNum = 0;

			for (int i = 0; i < MACHINE_NUM; i++) {
				coreNodeWrites[i] = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(DST_CORE_NODE_FILE_PREFIX + i), "utf-8"));
			}

			while ((line = nodeReader.readLine()) != null) {
				line = line.trim();
				lineNum++;
				//前五条数据不读取？注释缘故？（--------------为保证程序能适应当前数据，此处先注释-----------）
				//if (lineNum < 5)
				//	continue;


				String[] lineInfo = line.split(": ");
				String nodeId = lineInfo[0];   //将第一个id分离出来
				NODE_CACHE.put(nodeId, line);  //把id-content放入hashmap中
				int machine = nodeToMachine(nodeId);
				coreNodeWrites[machine - 1].write(line);
				coreNodeWrites[machine - 1].newLine();

				if (lineNum % 10000 == 0)
					System.out.println("has read nodes line: " + lineNum);
			}
			System.out.println("finished! nodes lines: " + lineNum);

			nodeReader.close();
			for (int i = 0; i < MACHINE_NUM; i++) {
				coreNodeWrites[i].close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void edgeSplit() {
		try {
			BufferedReader edgeReader = new BufferedReader(new InputStreamReader(new FileInputStream(SRC_EDGE_FILE), "utf-8"));
			BufferedWriter[] extendNodeWriters = new BufferedWriter[MACHINE_NUM];
			BufferedWriter[] coreEdgeWriters = new BufferedWriter[MACHINE_NUM];
			BufferedWriter[] extendEdgeWriters = new BufferedWriter[MACHINE_NUM];

//			Jedis jedis = new Jedis("localhost");
//			jedis.select(1);

			Set<String>[] machieOwnNodes = new Set[MACHINE_NUM];
			String line = null;
			int lineNum = 0;

			for (int i = 0; i < MACHINE_NUM; i++) {
				extendNodeWriters[i] = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(DST_EXTEND_NODE_FILE_PREFIX + i), "utf-8"));
				coreEdgeWriters[i] = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(DST_CORE_EDGE_FILE_PREFIX + i), "utf-8"));
				extendEdgeWriters[i] = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(DST_EXTEND_EDGE_FILE_PREFIX + i), "utf-8"));
				machieOwnNodes[i] = new HashSet<>();
			}

			while ((line = edgeReader.readLine()) != null) {
				line = line.trim();
				lineNum++;
				//前五条数据不读取？注释缘故？（--------------为保证程序能适应当前数据，此处先注释--------------）
				//if (lineNum < 5)
				//	continue;

				String[] lineInfo = line.split(": ");
				String[] startAndEnd = lineInfo[1].trim().split(" ");
				String startId = startAndEnd[0];
				String endId = startAndEnd[1];
				int startMachine = nodeToMachine(startId);
				int endMachine = nodeToMachine(endId);
				if (startMachine == endMachine) {
					coreEdgeWriters[startMachine - 1].write(line);
					coreEdgeWriters[startMachine - 1].newLine();
				} else {
					if (!machieOwnNodes[startMachine - 1].contains(endId)) {
					//if (!jedis.sismember(String.valueOf(startMachine-1), endId)) {
						String endNodeInfo = NODE_CACHE.get(endId);
						machieOwnNodes[startMachine - 1].add(endId);
						//jedis.sadd(String.valueOf(startMachine-1), endId);
						extendNodeWriters[startMachine - 1].write(endNodeInfo);
						extendNodeWriters[startMachine - 1].newLine();
						;
					}
					extendEdgeWriters[startMachine - 1].write(line);
					extendEdgeWriters[startMachine - 1].newLine();

					if (!machieOwnNodes[endMachine - 1].contains(startId)) {
					//if (!jedis.sismember(String.valueOf(endMachine-1), startId)) {
						String startNodeInfo = NODE_CACHE.get(startId);
						machieOwnNodes[endMachine - 1].add(startId);
						//jedis.sadd(String.valueOf(endMachine-1), startId);
						extendNodeWriters[endMachine - 1].write(startNodeInfo);
						extendNodeWriters[endMachine - 1].newLine();
					}
					extendEdgeWriters[endMachine - 1].write(line);
					extendEdgeWriters[endMachine - 1].newLine();
				}

				if (lineNum % 10000 == 0)
					System.out.println("has read edges line: " + lineNum);
			}
			System.out.println("finished! edges lines: " + lineNum);

			edgeReader.close();
			for (int i = 0; i < MACHINE_NUM; i++) {
				extendNodeWriters[i].close();
				coreEdgeWriters[i].close();
				extendEdgeWriters[i].close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		System.out.println("node split start...");
		GraphSplit.nodeSplit();
		System.out.println("node split finish...");

		System.out.println("================");

		System.out.println("edge split start...");
		GraphSplit.edgeSplit();
		System.out.println("edge split finish...");
	}

}
