package opt.easyjmetal.algorithm.cmoeas.entrence;

import opt.easyjmetal.algorithm.AlgorithmFactory;
import opt.easyjmetal.algorithm.common.MatlabUtilityFunctionsWrapper;
import opt.easyjmetal.core.Algorithm;
import opt.easyjmetal.core.Operator;
import opt.easyjmetal.core.Problem;
import opt.easyjmetal.operator.crossover.CrossoverFactory;
import opt.easyjmetal.operator.mutation.MutationFactory;
import opt.easyjmetal.operator.selection.SelectionFactory;
import opt.easyjmetal.problem.ProblemFactory;
import opt.rl4j.fly.BeepUtil;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;

public class CMOEAs_main {

    public static void main(String[] args) throws Exception {
        int crossoverMethod = 1;

        MatlabUtilityFunctionsWrapper.setup();
        batchRun(new String[]{
//                "NSGAII_CDP_Fitness_ISDEPlus",
//                "NSGAII_CDP_Fitness_Contribution",
//                "NSGAII_CDP_Fitness",
//                "NSGAII_CDP_Contribution",
//                "NSGAII_CDP_ISDEPlus",
//                "NSGAII_CDP_ManyAddOne",
                "NSGAII_CDP",
                "ISDEPLUS_CDP",
                "NSGAIII_CDP",
                "MOEAD_CDP",
                "MOEAD_IEpsilon",
                "MOEAD_Epsilon",
                "MOEAD_SR",
                "C_MOEAD",
                "PPS_MOEAD",
                "C_TAEA",
                "CCMO"
        }, crossoverMethod);
        MatlabUtilityFunctionsWrapper.stop();
        BeepUtil.playSound("sound/bombo.wav");
    }

    private static void batchRun(String[] methods, int crossMethod) throws Exception {
        String[] algorithmSet = methods;
        int algorithmNo = algorithmSet.length;

        // 输出运行时间
        String basePath = "result/easyjmetal/twopipeline/";
        File dir = new File(basePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 每次新建文件true = append file
        FileWriter fileWritter = new FileWriter(basePath + "runtimes.txt", false);
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < algorithmNo; i++) {
            System.out.println("The tested algorithm: " + algorithmSet[i]);
            System.out.println("The process: " + String.format("%.2f", (100.0 * i / algorithmNo)) + "%");
            stringBuilder.append(singleRun(algorithmSet[i], crossMethod, basePath));
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
        int maxFES = 30000;
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
        int runtime = 6;
        // 是否显示详细调度
        Boolean isDisplay = false;
        // 0: population; 1: external archive
        int plotFlag = 0;
        // MOEAD_SR parameters
        double srFactor = 0.05;

        // 算法需要传入两个参数，一个是编码方式，另一个是xml配置文件所在的路径
        Object[] params = {"Real", "data/configs/config1.xml"};
        String[] problemStrings = {"EDFPS"};//, "EDFTSS"

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
