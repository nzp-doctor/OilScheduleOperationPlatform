package opt.jmetal.util.artificialdecisionmaker.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import opt.jmetal.algorithm.InteractiveAlgorithm;
import opt.jmetal.problem.BoundedProblem;
import opt.jmetal.problem.Problem;
import opt.jmetal.solution.Solution;
import opt.jmetal.util.artificialdecisionmaker.ArtificialDecisionMaker;
import opt.jmetal.util.artificialdecisionmaker.DecisionTreeEstimator;
import opt.jmetal.util.comparator.ObjectiveComparator;
import opt.jmetal.util.distance.impl.EuclideanDistanceBetweenSolutionsInObjectiveSpace;
import opt.jmetal.util.pseudorandom.JMetalRandom;

/**
 * Class implementing the Towards automatic testing of reference point based
 * interactive methods described in: Ojalehto, V., Podkopaev, D., & Miettinen,
 * K. (2016, September). Towards automatic testing of reference point based
 * interactive methods. In International Conference on Parallel Problem Solving
 * from Nature (pp. 483-492). Springer, Cham.
 *
 * @author Cristobal Barba <cbarba@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class ArtificialDecisionMakerDecisionTree<S extends Solution<?>> extends ArtificialDecisionMaker<S, List<S>> {

    protected List<Double> idealOjectiveVector = null;
    protected List<Double> nadirObjectiveVector = null;
    protected List<Double> rankingCoeficient = null;
    protected List<Double> asp = null;
    protected double tolerance;
    // protected int numberReferencePoints;
    protected JMetalRandom random = null;
    protected double considerationProbability;
    protected int numberOfObjectives;
    protected double varyingProbability;
    protected int evaluations;
    protected int maxEvaluations;
    protected List<Double> allReferencePoints;
    protected List<Double> currentReferencePoint;
    protected List<Double> distances;
    private S solutionRun = null;

    public ArtificialDecisionMakerDecisionTree(Problem<S> problem, InteractiveAlgorithm<S, List<S>> algorithm,
                                               double considerationProbability, double tolerance, int maxEvaluations, List<Double> rankingCoeficient,
                                               List<Double> asp) {
        super(problem, algorithm);
        this.considerationProbability = considerationProbability;
        this.tolerance = tolerance;
        numberOfObjectives = problem.getNumberOfObjectives();
        this.random = JMetalRandom.getInstance();
        this.maxEvaluations = maxEvaluations;
        this.rankingCoeficient = rankingCoeficient;
        if (rankingCoeficient == null || rankingCoeficient.isEmpty()) {
            initialiceRankingCoeficient();
        }
        // this.numberReferencePoints = numberReferencePoints;
        this.allReferencePoints = new ArrayList<>();
        this.distances = new ArrayList<>();

        if (asp != null) {
            this.asp = new ArrayList<>(asp);
        }
    }

    private void initialiceRankingCoeficient() {
        rankingCoeficient = new ArrayList<>();
        for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
            rankingCoeficient.add(1.0 / problem.getNumberOfObjectives());
        }
    }

    private void updateObjectiveVector(List<S> solutionList) {
        for (int j = 0; j < numberOfObjectives; j++) {
            Collections.sort(solutionList, new ObjectiveComparator<>(j));
            double objetiveMinn = solutionList.get(0).getObjective(j);
            double objetiveMaxn = solutionList.get(solutionList.size() - 1).getObjective(j);
            idealOjectiveVector.add(objetiveMinn);
            nadirObjectiveVector.add(objetiveMaxn);
        }
        if (problem instanceof BoundedProblem) {
            BoundedProblem<?, ?> aux = (BoundedProblem<?, ?>) problem;
            for (int i = 0; i < numberOfObjectives; i++) {
                idealOjectiveVector.add(aux.getLowerBound(i).doubleValue());
                nadirObjectiveVector.add(aux.getUpperBound(i).doubleValue());
            }
        }
        if (asp == null)
            asp = idealOjectiveVector;
    }

    @Override
    protected List<Double> generatePreferenceInformation() {

        idealOjectiveVector = new ArrayList<>(numberOfObjectives);
        nadirObjectiveVector = new ArrayList<>(numberOfObjectives);

        List<S> solutions = new ArrayList<>();
        S sol = problem.createSolution();
        problem.evaluate(sol);
        solutions.add(sol);
        updateObjectiveVector(solutions);
        // solutionRun = solutions.get(0);
        List<Double> referencePoint = new ArrayList<>(numberOfObjectives);
        for (int j = 0; j < numberOfObjectives; j++) {
            double rand = random.nextDouble(0.0, 1.0);
            if (rand < considerationProbability * rankingCoeficient.get(j)) {
                referencePoint.add(asp.get(j));
            } else {
                referencePoint.add(nadirObjectiveVector.get(j));
            }
        }

        currentReferencePoint = referencePoint;
        allReferencePoints.addAll(referencePoint);
        return referencePoint;
    }

    @Override
    protected boolean isStoppingConditionReached() {
        boolean stop = evaluations > maxEvaluations;

        if (indexOfRelevantObjectiveFunctions != null) {
            stop = stop || indexOfRelevantObjectiveFunctions.size() == numberOfObjectives;
        }
        return stop;
    }

    @Override
    protected void initProgress() {
        evaluations = 0;
        varyingProbability = considerationProbability;
    }

    @Override
    protected void updateProgress() {
        evaluations++;
    }

    @Override
    protected List<Integer> relevantObjectiveFunctions(List<S> front) {
        List<Integer> order = new ArrayList<>();
        List<Integer> indexRelevantObjectivesFunctions = new ArrayList<>();
        SortedMap<Double, List<Integer>> map = new TreeMap<>(Collections.reverseOrder());
        for (int i = 0; i < rankingCoeficient.size(); i++) {
            List<Integer> aux = map.getOrDefault(rankingCoeficient.get(i), new ArrayList<>());
            aux.add(i);
            map.putIfAbsent(rankingCoeficient.get(i), aux);
        }
        Set<Double> keys = map.keySet();
        for (Double key : keys) {
            order.addAll(map.get(key));
        }
        S solution = getSolution(front, currentReferencePoint);
        for (Integer i : order) {
            double rand = random.nextDouble(0.0, 1.0);
            if ((asp.get(i) - solution.getObjective(i)) < tolerance && rand < considerationProbability) {
                indexRelevantObjectivesFunctions.add(i);
            } else if (rand < varyingProbability) {
                indexRelevantObjectivesFunctions.add(i);
            }
            varyingProbability -= (varyingProbability / i) * indexRelevantObjectivesFunctions.size();
        }
        return indexRelevantObjectivesFunctions;
    }

    @Override
    protected List<Double> calculateReferencePoints(List<Integer> indexOfRelevantObjectiveFunctions, List<S> front,
                                                    List<S> paretoOptimalSolutions) {
        List<Double> result = new ArrayList<>();
        List<S> temporal = new ArrayList<>(front);

        S solution = getSolution(temporal, currentReferencePoint);
        solutionRun = solution;
        temporal.remove(solution);

        for (int i = 0; i < numberOfObjectives; i++) {
            if (indexOfRelevantObjectiveFunctions.contains(i)) {
                result.add(asp.get(i) - (asp.get(i) - solution.getObjective(i)) / 2);
            } else {
                // predict the i position of reference point
                result.add(prediction(i, front, solution));
            }
        }
        calculateDistance(solutionRun, asp);

        currentReferencePoint = result;
        allReferencePoints.addAll(result);

        return result;
    }

    private void calculateDistance(S solution, List<Double> referencePoint) {
        EuclideanDistanceBetweenSolutionsInObjectiveSpace<S> euclidean = new EuclideanDistanceBetweenSolutionsInObjectiveSpace<>();

        double distance = euclidean.getDistance(solution, getSolutionFromRP(referencePoint));
        distances.add(distance);
    }

    private S getSolutionFromRP(List<Double> referencePoint) {
        S result = problem.createSolution();
        for (int i = 0; i < result.getNumberOfObjectives(); i++) {
            result.setObjective(i, referencePoint.get(i));
        }
        return result;
    }

    private double prediction(int index, List<S> paretoOptimalSolutions, S solution) {
        DecisionTreeEstimator<S> dte = new DecisionTreeEstimator<S>(paretoOptimalSolutions);

        double data = dte.doPrediction(index, solution);
        return data;
    }

    @Override
    protected void updateParetoOptimal(List<S> front, List<S> paretoOptimalSolutions) {
        // paretoOptimalSolutions.addAll(front);
        paretoOptimalSolutions = new ArrayList<>(front);
    }

    @Override
    public List<Double> getReferencePoints() {
        // allReferencePoints.remove(allReferencePoints.size()-1);
        return allReferencePoints;
    }

    @Override
    public List<Double> getDistances() {

        return distances;
    }

    private S getSolution(List<S> front, List<Double> referencePoint) {
        S result = front.get(0);
        EuclideanDistanceBetweenSolutionsInObjectiveSpace<S> euclidean = new EuclideanDistanceBetweenSolutionsInObjectiveSpace<>();
        SortedMap<Double, S> map = new TreeMap<>();
        S aux = getSolutionFromRP(referencePoint);
        for (S solution : front) {
            double distance = euclidean.getDistance(solution, aux);
            map.put(distance, solution);
        }
        result = map.get(map.firstKey());
        return result;
    }

    @Override
    public void clearSolutions() {
        // TODO Auto-generated method stub

    }

    @Override
    public List<Double[]> getSolutions() {
        // TODO Auto-generated method stub
        return null;
    }

}
