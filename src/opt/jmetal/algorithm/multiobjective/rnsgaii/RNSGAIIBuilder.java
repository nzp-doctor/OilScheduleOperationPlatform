package opt.jmetal.algorithm.multiobjective.rnsgaii;

import opt.jmetal.operator.CrossoverOperator;
import opt.jmetal.operator.MutationOperator;
import opt.jmetal.operator.SelectionOperator;
import opt.jmetal.operator.impl.selection.BinaryTournamentSelection;
import opt.jmetal.problem.Problem;
import opt.jmetal.solution.Solution;
import opt.jmetal.util.AlgorithmBuilder;
import opt.jmetal.util.JMetalException;
import opt.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import opt.jmetal.util.evaluator.SolutionListEvaluator;
import opt.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

import java.util.List;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class RNSGAIIBuilder<S extends Solution<?>> implements AlgorithmBuilder<RNSGAII<S>> {
    /**
     * NSGAIIBuilder class
     */
    private final Problem<S> problem;
    private int maxEvaluations;
    private int populationSize;
    protected int matingPoolSize;
    protected int offspringPopulationSize;

    private CrossoverOperator<S> crossoverOperator;
    private MutationOperator<S> mutationOperator;
    private SelectionOperator<List<S>, S> selectionOperator;
    private SolutionListEvaluator<S> evaluator;
    private List<Double> interestPoint;
    private double epsilon;

    /**
     * NSGAIIBuilder constructor
     */
    public RNSGAIIBuilder(Problem<S> problem, CrossoverOperator<S> crossoverOperator,
                          MutationOperator<S> mutationOperator, List<Double> interestPoint, double epsilon) {
        this.problem = problem;
        maxEvaluations = 25000;
        populationSize = 100;
        this.matingPoolSize = 100;
        this.offspringPopulationSize = 100;
        this.crossoverOperator = crossoverOperator;
        this.mutationOperator = mutationOperator;
        selectionOperator = new BinaryTournamentSelection<S>(new RankingAndCrowdingDistanceComparator<S>());
        evaluator = new SequentialSolutionListEvaluator<S>();
        this.epsilon = epsilon;
        this.interestPoint = interestPoint;
    }

    public RNSGAIIBuilder<S> setMaxEvaluations(int maxEvaluations) {
        if (maxEvaluations < 0) {
            throw new JMetalException("maxEvaluations is negative: " + maxEvaluations);
        }
        this.maxEvaluations = maxEvaluations;

        return this;
    }

    public RNSGAIIBuilder<S> setPopulationSize(int populationSize) {
        if (populationSize < 0) {
            throw new JMetalException("Population size is negative: " + populationSize);
        }

        this.populationSize = populationSize;

        return this;
    }

    public RNSGAIIBuilder<S> setSelectionOperator(SelectionOperator<List<S>, S> selectionOperator) {
        if (selectionOperator == null) {
            throw new JMetalException("selectionOperator is null");
        }
        this.selectionOperator = selectionOperator;

        return this;
    }

    public RNSGAIIBuilder<S> setSolutionListEvaluator(SolutionListEvaluator<S> evaluator) {
        if (evaluator == null) {
            throw new JMetalException("evaluator is null");
        }
        this.evaluator = evaluator;

        return this;
    }

    public RNSGAIIBuilder<S> setMatingPoolSize(int matingPoolSize) {
        if (matingPoolSize < 0) {
            throw new JMetalException("The mating pool size is negative: " + populationSize);
        }

        this.matingPoolSize = matingPoolSize;

        return this;
    }

    public RNSGAIIBuilder<S> setOffspringPopulationSize(int offspringPopulationSize) {
        if (offspringPopulationSize < 0) {
            throw new JMetalException("Offspring population size is negative: " + populationSize);
        }

        this.offspringPopulationSize = offspringPopulationSize;

        return this;
    }

    public RNSGAII<S> build() {
        RNSGAII<S> algorithm;

        algorithm = new RNSGAII<>(problem, maxEvaluations, populationSize, matingPoolSize, offspringPopulationSize,
                crossoverOperator, mutationOperator, selectionOperator, evaluator, interestPoint, epsilon);

        return algorithm;
    }

    /* Getters */
    public Problem<S> getProblem() {
        return problem;
    }

    public int getMaxIterations() {
        return maxEvaluations;
    }

    public int getPopulationSize() {
        return populationSize;
    }

    public CrossoverOperator<S> getCrossoverOperator() {
        return crossoverOperator;
    }

    public MutationOperator<S> getMutationOperator() {
        return mutationOperator;
    }

    public SelectionOperator<List<S>, S> getSelectionOperator() {
        return selectionOperator;
    }

    public SolutionListEvaluator<S> getSolutionListEvaluator() {
        return evaluator;
    }
}
