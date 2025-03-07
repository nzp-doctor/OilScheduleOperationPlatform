package opt.jmetal.util.archive.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import opt.jmetal.problem.DoubleProblem;
import opt.jmetal.problem.impl.AbstractDoubleProblem;
import opt.jmetal.solution.DoubleSolution;
import opt.jmetal.solution.Solution;
import opt.jmetal.solution.impl.ArrayDoubleSolution;
import opt.jmetal.util.comparator.DominanceComparator;
import opt.jmetal.util.comparator.EqualSolutionsComparator;
import opt.jmetal.util.pseudorandom.JMetalRandom;
import opt.jmetal.util.archive.Archive;

/**
 * This class implements an archive containing non-dominated solutions
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @author Juan J. Durillo
 */
@SuppressWarnings("serial")
public class NonDominatedSolutionListArchive<S extends Solution<?>> implements Archive<S> {
    private List<S> solutionList;
    private Comparator<S> dominanceComparator;
    private Comparator<S> equalSolutions = new EqualSolutionsComparator<S>();

    /**
     * Constructor
     */
    public NonDominatedSolutionListArchive() {
        this(new DominanceComparator<S>());
    }

    /**
     * Constructor
     */
    public NonDominatedSolutionListArchive(DominanceComparator<S> comparator) {
        dominanceComparator = comparator;

        solutionList = new ArrayList<>();
    }

    /**
     * Inserts a solution in the list
     *
     * @param solution The solution to be inserted.
     * @return true if the operation success, and false if the solution is dominated
     * or if an identical individual exists. The decision variables can be
     * null if the solution is read from a file; in that case, the
     * domination tests are omitted
     */
    @Override
    public boolean add(S solution) {
        boolean solutionInserted = false;
        if (solutionList.size() == 0) {
            solutionList.add(solution);
            solutionInserted = true;
        } else {
            Iterator<S> iterator = solutionList.iterator();
            boolean isDominated = false;

            boolean isContained = false;
            while (((!isDominated) && (!isContained)) && (iterator.hasNext())) {
                S listIndividual = iterator.next();
                int flag = dominanceComparator.compare(solution, listIndividual);
                if (flag == -1) {
                    iterator.remove();
                } else if (flag == 1) {
                    isDominated = true; // dominated by one in the list
                } else if (flag == 0) {
                    int equalflag = equalSolutions.compare(solution, listIndividual);
                    if (equalflag == 0) // solutions are equals
                        isContained = true;
                }
            }

            if (!isDominated && !isContained) {
                solutionList.add(solution);
                solutionInserted = true;
            }

            return solutionInserted;
        }

        return solutionInserted;
    }

    public Archive<S> join(Archive<S> archive) {
        return this.addAll(archive.getSolutionList());
    }

    public Archive<S> addAll(List<S> list) {
        for (S solution : list) {
            this.add(solution);
        }

        return this;
    }

    @Override
    public List<S> getSolutionList() {
        return solutionList;
    }

    @Override
    public int size() {
        return solutionList.size();
    }

    @Override
    public S get(int index) {
        return solutionList.get(index);
    }

    public static void main(String args[]) {
        JMetalRandom.getInstance().setSeed(1L);
        Archive<DoubleSolution> archive = new NonDominatedSolutionListArchive<>();
        DoubleProblem problem = new MockedDoubleProblem1(100);
        long initTime = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            DoubleSolution solution = problem.createSolution();
            problem.evaluate(solution);
            archive.add(solution);
        }
        System.out.println("Time: " + (System.currentTimeMillis() - initTime));
    }

    private static class MockedDoubleProblem1 extends AbstractDoubleProblem {
        public MockedDoubleProblem1(int numberOfVariables) {
            setNumberOfVariables(numberOfVariables);
            setNumberOfObjectives(2);
            setNumberOfConstraints(0);

            List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables());
            List<Double> upperLimit = new ArrayList<>(getNumberOfVariables());

            for (int i = 0; i < getNumberOfVariables(); i++) {
                lowerLimit.add(0.0);
                upperLimit.add(1.0);
            }

            setLowerLimit(lowerLimit);
            setUpperLimit(upperLimit);
        }

        public void evaluate(DoubleSolution solution) {
            double[] f = new double[getNumberOfObjectives()];

            f[0] = solution.getVariableValue(0) + 0.0;
            double g = this.evalG(solution);
            double h = this.evalH(f[0], g);
            f[1] = h * g;

            solution.setObjective(0, f[0]);
            solution.setObjective(1, f[1]);
        }

        /**
         * Returns the value of the ZDT1 function G.
         *
         * @param solution Solution
         */
        private double evalG(DoubleSolution solution) {
            double g = 0.0;
            for (int i = 1; i < solution.getNumberOfVariables(); i++) {
                g += solution.getVariableValue(i);
            }
            double constant = 9.0 / (solution.getNumberOfVariables() - 1.0);
            g = constant * g;
            g = g + 1.0;
            return g;
        }

        /**
         * Returns the value of the ZDT1 function H.
         *
         * @param f First argument of the function H.
         * @param g Second argument of the function H.
         */
        public double evalH(double f, double g) {
            double h;
            h = 1.0 - Math.sqrt(f / g);
            return h;
        }

        @Override
        public DoubleSolution createSolution() {
            return new ArrayDoubleSolution(this);
        }
    }
}
