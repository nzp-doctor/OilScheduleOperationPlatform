package opt.jmetal.algorithm.multiobjective.mocell;

import opt.jmetal.algorithm.impl.AbstractGeneticAlgorithm;
import opt.jmetal.operator.CrossoverOperator;
import opt.jmetal.operator.MutationOperator;
import opt.jmetal.operator.SelectionOperator;
import opt.jmetal.problem.Problem;
import opt.jmetal.problem.oil.sim.common.CloneUtils;
import opt.jmetal.solution.Solution;
import opt.jmetal.util.archive.BoundedArchive;
import opt.jmetal.util.comparator.DominanceComparator;
import opt.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import opt.jmetal.util.evaluator.SolutionListEvaluator;
import opt.jmetal.util.neighborhood.Neighborhood;
import opt.jmetal.util.solutionattribute.Ranking;
import opt.jmetal.util.solutionattribute.impl.CrowdingDistance;
import opt.jmetal.util.solutionattribute.impl.DominanceRanking;
import opt.jmetal.util.solutionattribute.impl.LocationAttribute;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;

/**
 * @param <S>
 * @author JuanJo Durillo
 */
@SuppressWarnings("serial")
public class MOCell<S extends Solution<?>> extends AbstractGeneticAlgorithm<S, List<S>> {
    protected int evaluations;
    protected int maxEvaluations;
    protected final SolutionListEvaluator<S> evaluator;

    protected Neighborhood<S> neighborhood;
    protected int currentIndividual;
    protected List<S> currentNeighbors;

    protected BoundedArchive<S> archive;

    protected Comparator<S> dominanceComparator;
    protected LocationAttribute<S> location;

    protected List<Double[]> solutions = new LinkedList<>();

    /**
     * Constructor
     *
     * @param problem
     * @param maxEvaluations
     * @param populationSize
     * @param neighborhood
     * @param crossoverOperator
     * @param mutationOperator
     * @param selectionOperator
     * @param evaluator
     */
    public MOCell(Problem<S> problem, int maxEvaluations, int populationSize, BoundedArchive<S> archive,
                  Neighborhood<S> neighborhood, CrossoverOperator<S> crossoverOperator, MutationOperator<S> mutationOperator,
                  SelectionOperator<List<S>, S> selectionOperator, SolutionListEvaluator<S> evaluator) {
        super(problem);
        this.maxEvaluations = maxEvaluations;
        setMaxPopulationSize(populationSize);
        this.archive = archive;
        this.neighborhood = neighborhood;
        this.crossoverOperator = crossoverOperator;
        this.mutationOperator = mutationOperator;
        this.selectionOperator = selectionOperator;
        this.dominanceComparator = new DominanceComparator<S>();

        this.evaluator = evaluator;
    }

    @Override
    protected void initProgress() {
        evaluations = population.size();
        currentIndividual = 0;
    }

    @Override
    protected void updateProgress() {
        evaluations++;
        currentIndividual = (currentIndividual + 1) % getMaxPopulationSize();
        // 更新进度条
        // MainMethod.frame.updateProcessBar(evaluations);
    }

    @Override
    protected boolean isStoppingConditionReached() {
        return (evaluations >= maxEvaluations);
    }

    @Override
    protected List<S> createInitialPopulation() {
        List<S> population = new ArrayList<>(getMaxPopulationSize());
        for (int i = 0; i < getMaxPopulationSize(); i++) {
            S newIndividual = getProblem().createSolution();
            population.add(newIndividual);
        }
        return population;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected List<S> evaluatePopulation(List<S> population) {
        // 1.评价种群中个体的适应度
        population = evaluator.evaluate(population, getProblem());
        for (S solution : population) {
            // 2.将深复制的解添加到储备集合中
            archive.add((S) solution.copy());
            // 3.保存到种群列表中
            solutions.add(CloneUtils.clone(ArrayUtils.toObject(solution.getObjectives())));
        }

        return population;
    }

    @Override
    protected List<S> selection(List<S> population) {
        List<S> parents = new ArrayList<>(2);
        currentNeighbors = neighborhood.getNeighbors(population, currentIndividual);
        currentNeighbors.add(population.get(currentIndividual));

        parents.add(selectionOperator.execute(currentNeighbors));
        if (archive.size() > 0) { // TODO. REVISAR EN EL CASO DE TAMAÑO 1
            parents.add(selectionOperator.execute(archive.getSolutionList()));
        } else {
            parents.add(selectionOperator.execute(currentNeighbors));
        }
        return parents;
    }

    @Override
    protected List<S> reproduction(List<S> population) {
        List<S> result = new ArrayList<>(1);
        List<S> offspring = crossoverOperator.execute(population);
        mutationOperator.execute(offspring.get(0));
        result.add(offspring.get(0));
        return result;
    }

    @Override
    protected List<S> replacement(List<S> population, List<S> offspringPopulation) {
        location = new LocationAttribute<>(population);

        int flag = dominanceComparator.compare(population.get(currentIndividual), offspringPopulation.get(0));

        if (flag == 1) { // The new individual dominates
            population = insertNewIndividualWhenDominates(population, offspringPopulation);
        } else if (flag == 0) { // The new individual is non-dominated
            population = insertNewIndividualWhenNonDominated(population, offspringPopulation);
        }
        return population;
    }

    @Override
    public List<S> getResult() {
        return archive.getSolutionList();
    }

    private List<S> insertNewIndividualWhenDominates(List<S> population, List<S> offspringPopulation) {
        location.setAttribute(offspringPopulation.get(0), location.getAttribute(population.get(currentIndividual)));
        List<S> result = new ArrayList<>(population);
        result.set(location.getAttribute(offspringPopulation.get(0)), offspringPopulation.get(0));
        archive.add(offspringPopulation.get(0));
        return result;
    }

    private List<S> insertNewIndividualWhenNonDominated(List<S> population, List<S> offspringPopulation) {
        currentNeighbors.add(offspringPopulation.get(0));
        location.setAttribute(offspringPopulation.get(0), -1);
        List<S> result = new ArrayList<>(population);
        Ranking<S> rank = new DominanceRanking<S>();
        rank.computeRanking(currentNeighbors);

        CrowdingDistance<S> crowdingDistance = new CrowdingDistance<S>();
        for (int j = 0; j < rank.getNumberOfSubfronts(); j++) {
            crowdingDistance.computeDensityEstimator(rank.getSubfront(j));
        }

        Collections.sort(this.currentNeighbors, new RankingAndCrowdingDistanceComparator<S>());
        S worst = this.currentNeighbors.get(this.currentNeighbors.size() - 1);

        if (location.getAttribute(worst) == -1) { // The worst is the offspring
            archive.add(offspringPopulation.get(0));
        } else {
            location.setAttribute(offspringPopulation.get(0), location.getAttribute(worst));
            result.set(location.getAttribute(offspringPopulation.get(0)), offspringPopulation.get(0));
            archive.add(offspringPopulation.get(0));

        }
        return result;
    }

    @Override
    public String getName() {
        return "MOCell";
    }

    @Override
    public String getDescription() {
        return "Multi-Objective Cellular evolutionry algorithm";
    }

    @Override
    public List<Double[]> getSolutions() {
        return solutions;
    }

    @Override
    public void clearSolutions() {
        solutions = null;
    }
}
