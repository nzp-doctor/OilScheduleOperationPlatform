package opt.easyjmetal.algorithm.cmoeas.entrence;

import opt.easyjmetal.algorithm.AlgorithmFactory;
import opt.easyjmetal.core.Algorithm;
import opt.easyjmetal.core.Operator;
import opt.easyjmetal.core.Problem;
import opt.easyjmetal.operator.crossover.CrossoverFactory;
import opt.easyjmetal.operator.mutation.MutationFactory;
import opt.easyjmetal.operator.selection.SelectionFactory;
import opt.easyjmetal.problem.ProblemFactory;
import opt.easyjmetal.algorithm.common.MatlabUtilityFunctionsWrapper;

import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * 该函数只是测试相关的约束多目标优化算法，可以随意修改
 */
public class CMOEAs_append {

    public static void main(String[] args) throws Exception {
        // 0 represents for DE, 1 represents for SBX
        int crossoverMethod = 1;
        MatlabUtilityFunctionsWrapper.setup();
        batchRun(Arrays.asList("C_MOEAD"), crossoverMethod);
    }

    private static void batchRun(List<String> algorithmSet, int crossMethod) throws Exception {
        int algorithmNo = algorithmSet.size();

        // 输出运行时间
        String basePath = "result/easyjmetal/two/";
        File dir = new File(basePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 每次新建文件true = append file
        FileWriter fileWritter = new FileWriter(basePath + "runtimes.txt", false);
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < algorithmNo; i++) {
            System.out.println("The tested algorithm: " + algorithmSet.get(i));
            System.out.println("The process: " + String.format("%.2f", (100.0 * i / algorithmNo)) + "%");
            stringBuilder.append(singleRun(algorithmSet.get(i), crossMethod, basePath));
        }

        fileWritter.write(stringBuilder.toString());
        fileWritter.flush();
        fileWritter.close();
    }

    private static String singleRun(String algorithmName, int crossMethod, String basePath) throws Exception {
        StringBuilder stringBuilder = new StringBuilder();

        Operator crossover;
        Operator mutation;
        Operator selection;
        HashMap parameters;

        int popSize = 100;
        int maxFES = 500;
        int updateNumber = 2;
        int neighborSize = (int) (0.1 * popSize);
        double deDelta = 0.9;
        double DeCrossRate = 1.0;
        double DeFactor = 0.5;
        double tao = 0.1;
        double alpha = 0.9;
        float infeasibleRatio = 0.1f;
        String AlgorithmName = algorithmName;
        double threshold = 1e-3;
        // 权重文件路径
        String weightPath = "resources/MOEAD_Weights/";
        // 迭代次数
        int runtime = 10;
        // 是否显示详细调度
        Boolean isDisplay = false;
        // 0: population; 1: external archive
        int plotFlag = 0;
        // MOEAD_SR parameters
        double srFactor = 0.05;

        // 算法需要传入两个参数，一个是编码方式，另一个是xml配置文件所在的路径
        Object[] params = {"Real", "data/configs/config1.xml"};
        String[] problemStrings = {"EDFPS", "EDFTSS"};

        for (int i = 0; i < problemStrings.length; i++) {
            // 问题
            Problem problem = ProblemFactory.getProblem(problemStrings[i], params);

            // 算法
            Object[] algorithmParams = {problem};
            Algorithm algorithm = AlgorithmFactory.getAlgorithm(AlgorithmName, algorithmParams);

            // pareto文件路径
            String paretoPath = basePath + problem.getName() + ".pf";

            // Algorithm parameters
            algorithm.setInputParameter("AlgorithmName", AlgorithmName);
            algorithm.setInputParameter("populationSize", popSize);
            algorithm.setInputParameter("maxEvaluations", maxFES);
            // 实验数据存放的路径
            algorithm.setInputParameter("dataDirectory", basePath + problem.getName());
            // 权重文件存放的路径
            algorithm.setInputParameter("weightDirectory", weightPath);
            algorithm.setInputParameter("T", neighborSize);
            algorithm.setInputParameter("delta", deDelta);
            algorithm.setInputParameter("nr", updateNumber);
            algorithm.setInputParameter("isDisplay", isDisplay);
            algorithm.setInputParameter("plotFlag", plotFlag);
            algorithm.setInputParameter("paretoPath", paretoPath);
            algorithm.setInputParameter("srFactor", srFactor);
            algorithm.setInputParameter("tao", tao);
            algorithm.setInputParameter("alpha", alpha);
            algorithm.setInputParameter("threshold_change", threshold);
            algorithm.setInputParameter("infeasibleRatio", infeasibleRatio);

            // 交叉算子
            if (crossMethod == 0) {
                parameters = new HashMap();
                parameters.put("CR", DeCrossRate);
                parameters.put("F", DeFactor);
                crossover = CrossoverFactory.getCrossoverOperator("DifferentialEvolutionCrossover", parameters);
                algorithm.addOperator("crossover", crossover);
            } else if (crossMethod == 1) {
                parameters = new HashMap();
                parameters.put("probability", 1.0);
                parameters.put("distributionIndex", 20.0);
                crossover = CrossoverFactory.getCrossoverOperator("SBXCrossover", parameters);
                algorithm.addOperator("crossover", crossover);
            }

            // 变异算子
            parameters = new HashMap();
            parameters.put("probability", 1.0 / problem.getNumberOfVariables());
            parameters.put("distributionIndex", 20.0);
            mutation = MutationFactory.getMutationOperator("PolynomialMutation", parameters);
            algorithm.addOperator("mutation", mutation);

            // 选择算子
            parameters = null;
            selection = SelectionFactory.getSelectionOperator("BinaryTournament2", parameters);
            algorithm.addOperator("selection", selection);

            // 独立运行若干次
            for (int j = 0; j < runtime; j++) {
                System.out.println("==================================================================");
                algorithm.setInputParameter("runningTime", j);
                // 运行算法，并记录运行时间
                System.out.println("The " + j + " run of " + algorithmName);
                long initTime = System.currentTimeMillis();
                algorithm.execute();
                long estimatedTime = System.currentTimeMillis() - initTime;
                System.out.println("Total execution time: " + estimatedTime + "ms");
                System.out.println("Problem:  " + problemStrings[i] + "  running time:  " + j);
                System.out.println("==================================================================");
                stringBuilder.append(algorithmName + "," + problemStrings[i] + "," + estimatedTime + "\n");
            }
        }
        return stringBuilder.toString();
    }
}
