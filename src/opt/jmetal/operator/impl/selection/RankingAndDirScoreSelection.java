package opt.jmetal.operator.impl.selection;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import opt.jmetal.solution.Solution;
import opt.jmetal.util.JMetalException;
import opt.jmetal.util.comparator.DirScoreComparator;
import opt.jmetal.util.solutionattribute.Ranking;
import opt.jmetal.util.solutionattribute.impl.DirScore;
import opt.jmetal.util.solutionattribute.impl.DominanceRanking;

/**
 * created at 11:47 am, 2019/1/29 Used for DIR-enhanced NSGA-II (D-NSGA-II) to
 * select the joint solutions for next iteration this code implemented according
 * to "Cai X, Sun H, Fan Z. A diversity indicator based on reference vectors for
 * many-objective optimization[J]. Information Sciences, 2018, 430-431:467-486."
 *
 * @author sunhaoran <nuaa_sunhr@yeah.net>
 */
@SuppressWarnings("serial")
public class RankingAndDirScoreSelection<S extends Solution<?>> extends RankingAndCrowdingSelection<S> {

    private int solutionsToSelect;
    private Comparator<S> dominanceComparator;
    private double[][] referenceVectors;

    public RankingAndDirScoreSelection(int solutionsToSelect, Comparator<S> dominanceComparator,
                                       double[][] referenceVectors) {
        super(solutionsToSelect, dominanceComparator);
        this.solutionsToSelect = solutionsToSelect;
        this.dominanceComparator = dominanceComparator;
        this.referenceVectors = referenceVectors;
    }

    @Override
    public List<S> execute(List<S> solutionSet) {
        if (referenceVectors == null || referenceVectors.length == 0) {
            throw new JMetalException("reference vectors can not be null.");
        }
        if (solutionSet.isEmpty()) {
            throw new JMetalException("solution set can not be null");
        }
        Ranking<S> ranking = new DominanceRanking<>(dominanceComparator);
        ranking.computeRanking(solutionSet);
        return dirScoreSelection(ranking);
    }

    private List<S> dirScoreSelection(Ranking<S> ranking) {
        DirScore<S> dirScore = new DirScore<>(referenceVectors);
        List<S> population = new ArrayList<>(solutionsToSelect);
        int rankingIndex = 0;
        while (population.size() < solutionsToSelect) {
            if (subfrontFillsIntoThePopulation(ranking, rankingIndex, population)) {
                dirScore.computeDensityEstimator(ranking.getSubfront(rankingIndex));
                addRankedSolutionsToPopulation(ranking, rankingIndex, population);
                rankingIndex++;
            } else {
                dirScore.computeDensityEstimator(ranking.getSubfront(rankingIndex));
                addLastRankedSolutionsToPopulation(ranking, rankingIndex, population);
            }
        }
        return population;
    }

    @Override
    protected void addLastRankedSolutionsToPopulation(Ranking<S> ranking, int rank, List<S> population) {
        List<S> currentRankedFront = ranking.getSubfront(rank);

        currentRankedFront.sort(new DirScoreComparator<>());

        int i = 0;
        while (population.size() < solutionsToSelect) {
            population.add(currentRankedFront.get(i));
            i++;
        }
    }
}
