package opt.jmetal.algorithm.multiobjective.mombi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import opt.jmetal.algorithm.multiobjective.mombi.util.AbstractUtilityFunctionsSet;
import opt.jmetal.algorithm.multiobjective.mombi.util.R2Ranking;
import opt.jmetal.algorithm.multiobjective.mombi.util.R2SolutionData;
import opt.jmetal.algorithm.multiobjective.mombi.util.TchebycheffUtilityFunctionsSet;
import opt.jmetal.algorithm.multiobjective.mombi.util.R2RankingAttribute;
import opt.jmetal.operator.CrossoverOperator;
import opt.jmetal.operator.MutationOperator;
import opt.jmetal.operator.SelectionOperator;
import opt.jmetal.problem.Problem;
import opt.jmetal.solution.Solution;
import opt.jmetal.util.evaluator.SolutionListEvaluator;

@SuppressWarnings("serial")
public class MOMBI<S extends Solution<?>> extends AbstractMOMBI<S> {

    protected final AbstractUtilityFunctionsSet<S> utilityFunctions;

    public MOMBI(Problem<S> problem, int maxIterations, CrossoverOperator<S> crossover, MutationOperator<S> mutation,
                 SelectionOperator<List<S>, S> selection, SolutionListEvaluator<S> evaluator, String pathWeights) {
        super(problem, maxIterations, crossover, mutation, selection, evaluator);
        utilityFunctions = this.createUtilityFunction(pathWeights);
    }

    public AbstractUtilityFunctionsSet<S> createUtilityFunction(String pathWeights) {
        return new TchebycheffUtilityFunctionsSet<>(pathWeights, this.getReferencePoint());
    }

    public int getMaxPopulationSize() {
        return this.utilityFunctions.getSize();
    }

    @Override
    public void specificMOEAComputations() {
        updateNadirPoint(this.getPopulation());
        updateReferencePoint(this.getPopulation());
    }

    @Override
    protected List<S> replacement(List<S> population, List<S> offspringPopulation) {
        List<S> jointPopulation = new ArrayList<>();
        jointPopulation.addAll(population);
        jointPopulation.addAll(offspringPopulation);

        R2Ranking<S> ranking = computeRanking(jointPopulation);
        return selectBest(ranking);
    }

    protected R2Ranking<S> computeRanking(List<S> solutionList) {
        R2Ranking<S> ranking = new R2Ranking<>(this.utilityFunctions);
        ranking.computeRanking(solutionList);

        return ranking;
    }

    protected void addRankedSolutionsToPopulation(R2Ranking<S> ranking, int index, List<S> population) {
        for (S solution : ranking.getSubfront(index))
            population.add(solution);
    }

    protected void addLastRankedSolutionsToPopulation(R2Ranking<S> ranking, int index, List<S> population) {
        List<S> front = ranking.getSubfront(index);
        Collections.sort(front, new Comparator<S>() {
            @Override
            public int compare(S arg0, S arg1) {
                R2RankingAttribute<S> attribute = new R2RankingAttribute<>();
                R2SolutionData dataFirst = attribute.getAttribute(arg0);
                R2SolutionData dataSecond = attribute.getAttribute(arg1);
                if (dataFirst.utility > dataSecond.utility)
                    return -1;
                else if (dataFirst.utility < dataSecond.utility)
                    return 1;
                else
                    return 0;
            }

        });
        int remain = this.getMaxPopulationSize() - population.size();
        for (S solution : front.subList(0, remain))
            population.add(solution);
    }

    protected List<S> selectBest(R2Ranking<S> ranking) {
        List<S> population = new ArrayList<>(this.getMaxPopulationSize());
        int rankingIndex = 0;

        while (populationIsNotFull(population)) {
            if (subfrontFillsIntoThePopulation(ranking, rankingIndex, population)) {
                addRankedSolutionsToPopulation(ranking, rankingIndex, population);
                rankingIndex++;
            } else {
                addLastRankedSolutionsToPopulation(ranking, rankingIndex, population);
            }
        }
        return population;
    }

    private boolean subfrontFillsIntoThePopulation(R2Ranking<S> ranking, int index, List<S> population) {
        return (population.size() + ranking.getSubfront(index).size() < this.getMaxPopulationSize());
    }

    protected AbstractUtilityFunctionsSet<S> getUtilityFunctions() {
        return this.utilityFunctions;
    }

    @Override
    public String getName() {
        return "MOMBI";
    }

    @Override
    public String getDescription() {
        return "Many-Objective Metaheuristic Based on the R2 Indicator";
    }

    @Override
    public List<Double[]> getSolutions() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void clearSolutions() {
        // TODO Auto-generated method stub

    }
}
