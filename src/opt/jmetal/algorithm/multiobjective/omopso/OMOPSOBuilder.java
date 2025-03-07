package opt.jmetal.algorithm.multiobjective.omopso;

import opt.jmetal.operator.MutationOperator;
import opt.jmetal.operator.impl.mutation.NonUniformMutation;
import opt.jmetal.operator.impl.mutation.UniformMutation;
import opt.jmetal.problem.DoubleProblem;
import opt.jmetal.solution.DoubleSolution;
import opt.jmetal.util.AlgorithmBuilder;
import opt.jmetal.util.evaluator.SolutionListEvaluator;

/**
 * Class implementing the OMOPSO algorithm
 */
public class OMOPSOBuilder implements AlgorithmBuilder<OMOPSO> {
    protected DoubleProblem problem;
    protected SolutionListEvaluator<DoubleSolution> evaluator;

    private int swarmSize = 100;
    private int archiveSize = 100;
    private int maxIterations = 25000;

    private UniformMutation uniformMutation;
    private NonUniformMutation nonUniformMutation;

    public OMOPSOBuilder(DoubleProblem problem, SolutionListEvaluator<DoubleSolution> evaluator) {
        this.evaluator = evaluator;
        this.problem = problem;
    }

    public OMOPSOBuilder setSwarmSize(int swarmSize) {
        this.swarmSize = swarmSize;

        return this;
    }

    public OMOPSOBuilder setArchiveSize(int archiveSize) {
        this.archiveSize = archiveSize;

        return this;
    }

    public OMOPSOBuilder setMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations;

        return this;
    }

    public OMOPSOBuilder setUniformMutation(MutationOperator<DoubleSolution> uniformMutation) {
        this.uniformMutation = (UniformMutation) uniformMutation;

        return this;
    }

    public OMOPSOBuilder setNonUniformMutation(MutationOperator<DoubleSolution> nonUniformMutation) {
        this.nonUniformMutation = (NonUniformMutation) nonUniformMutation;

        return this;
    }

    /* Getters */
    public int getArchiveSize() {
        return archiveSize;
    }

    public int getSwarmSize() {
        return swarmSize;
    }

    public int getMaxIterations() {
        return maxIterations;
    }

    public UniformMutation getUniformMutation() {
        return uniformMutation;
    }

    public NonUniformMutation getNonUniformMutation() {
        return nonUniformMutation;
    }

    public OMOPSO build() {
        return new OMOPSO(problem, evaluator, swarmSize, maxIterations, archiveSize, uniformMutation,
                nonUniformMutation);
    }
}
