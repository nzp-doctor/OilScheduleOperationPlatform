package opt.jmetal.algorithm.multiobjective.nsgaii;

import opt.jmetal.operator.CrossoverOperator;
import opt.jmetal.operator.MutationOperator;
import opt.jmetal.operator.SelectionOperator;
import opt.jmetal.problem.Problem;
import opt.jmetal.solution.Solution;
import opt.jmetal.util.evaluator.SolutionListEvaluator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
@Deprecated
public class SteadyStateNSGAII<S extends Solution<?>> extends NSGAII<S> {
    /**
     * Constructor
     */
    public SteadyStateNSGAII(Problem<S> problem, int maxEvaluations, int populationSize,
                             CrossoverOperator<S> crossoverOperator, MutationOperator<S> mutationOperator,
                             SelectionOperator<List<S>, S> selectionOperator, Comparator<S> dominanceComparator, SolutionListEvaluator<S> evaluator) {
        super(problem, maxEvaluations, populationSize, 100, 100, crossoverOperator, mutationOperator,
                selectionOperator, dominanceComparator, evaluator);
    }

    @Override
    protected void updateProgress() {
        evaluations++;
    }

    @Override
    protected List<S> selection(List<S> population) {
        List<S> matingPopulation = new ArrayList<>(2);

        matingPopulation.add(selectionOperator.execute(population));
        matingPopulation.add(selectionOperator.execute(population));

        return matingPopulation;
    }

    @Override
    protected List<S> reproduction(List<S> population) {
        List<S> offspringPopulation = new ArrayList<>(1);

        List<S> parents = new ArrayList<>(2);
        parents.add(population.get(0));
        parents.add(population.get(1));

        List<S> offspring = crossoverOperator.execute(parents);

        mutationOperator.execute(offspring.get(0));

        offspringPopulation.add(offspring.get(0));
        return offspringPopulation;
    }

    @Override
    public String getName() {
        return "ssNSGAII";
    }

    @Override
    public String getDescription() {
        return "Nondominated Sorting Genetic Algorithm version II. Steady-state version";
    }
}
