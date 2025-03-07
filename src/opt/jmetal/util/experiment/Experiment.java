package opt.jmetal.util.experiment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import opt.jmetal.qualityindicator.impl.GenericIndicator;
import opt.jmetal.solution.Solution;
import opt.jmetal.util.experiment.util.ExperimentAlgorithm;
import opt.jmetal.util.experiment.util.ExperimentProblem;

/**
 * Class for describing the configuration of a jMetal experiment.
 * <p>
 * Created by Antonio J. Nebro on 17/07/14.
 */
public class Experiment<S extends Solution<?>, Result extends List<S>> {
    private String experimentName;
    private List<ExperimentAlgorithm<S, Result>> algorithmList;
    private List<ExperimentProblem<S>> problemList;
    private String experimentBaseDirectory;

    private String outputParetoFrontFileName;
    private String outputParetoSetFileName;
    private int independentRuns;

    private String referenceFrontDirectory;

    private List<GenericIndicator<S>> indicatorList;

    private int numberOfCores;

    private int evaluation;

    private int populationsize;

    /**
     * Constructor
     */
    public Experiment(ExperimentBuilder<S, Result> builder) {
        this.experimentName = builder.getExperimentName();
        this.experimentBaseDirectory = builder.getExperimentBaseDirectory();
        this.algorithmList = builder.getAlgorithmList();
        this.problemList = builder.getProblemList();
        this.independentRuns = builder.getIndependentRuns();
        this.outputParetoFrontFileName = builder.getOutputParetoFrontFileName();
        this.outputParetoSetFileName = builder.getOutputParetoSetFileName();
        this.numberOfCores = builder.getNumberOfCores();
        this.referenceFrontDirectory = builder.getReferenceFrontDirectory();
        this.indicatorList = builder.getIndicatorList();
        this.evaluation = builder.getEvaluation();
        this.populationsize = builder.getPopulationsize();
    }

    /* Getters */
    public String getExperimentName() {
        return experimentName;
    }

    public int getEvaluation() {
        return evaluation;
    }

    public int getPopulationsize() {
        return populationsize;
    }

    public List<ExperimentAlgorithm<S, Result>> getAlgorithmList() {
        return algorithmList;
    }

    public List<ExperimentProblem<S>> getProblemList() {
        return problemList;
    }

    public String getExperimentBaseDirectory() {
        return experimentBaseDirectory;
    }

    public String getOutputParetoFrontFileName() {
        return outputParetoFrontFileName;
    }

    public String getOutputParetoSetFileName() {
        return outputParetoSetFileName;
    }

    public int getIndependentRuns() {
        return independentRuns;
    }

    public int getNumberOfCores() {
        return numberOfCores;
    }

    public String getReferenceFrontDirectory() {
        return referenceFrontDirectory;
    }

    public List<GenericIndicator<S>> getIndicatorList() {
        return indicatorList;
    }

    /* Setters */
    public void setReferenceFrontDirectory(String referenceFrontDirectory) {
        this.referenceFrontDirectory = referenceFrontDirectory;
    }

    public void setAlgorithmList(List<ExperimentAlgorithm<S, Result>> algorithmList) {
        this.algorithmList = algorithmList;
    }

    /**
     * The list of algorithms contain an algorithm instance per problem. This is not
     * convenient for calculating statistical data, because a same algorithm will
     * appear many times. This method remove duplicated algorithms and leave only an
     * instance of each one.
     *
     * @Deprecated 方法已过期
     */
    @Deprecated
    public void removeDuplicatedAlgorithms() {
        List<ExperimentAlgorithm<S, Result>> algorithmList = new ArrayList<>();
        HashSet<String> algorithmTagList = new HashSet<>();

        getAlgorithmList().removeIf(alg -> !algorithmTagList.add(alg.getAlgorithmTag()));
    }
}
