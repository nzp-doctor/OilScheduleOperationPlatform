package opt.jmetal.problem.multiobjective.mop;

import opt.jmetal.solution.DoubleSolution;
import opt.jmetal.problem.impl.AbstractDoubleProblem;

import java.util.ArrayList;
import java.util.List;

/**
 * Problem MOP5. Defined in
 * H. L. Liu, F. Gu and Q. Zhang, "Decomposition of a Multiobjective
 * Optimization Problem Into a Number of Simple Multiobjective Subproblems,"
 * in IEEE Transactions on Evolutionary Computation, vol. 18, no. 3, pp.
 * 450-455, June 2014.
 *
 * @author Mastermay <javismay@gmail.com>
 */
@SuppressWarnings("serial")
public class MOP5 extends AbstractDoubleProblem {

    /**
     * Constructor. Creates default instance of problem MOP5 (10 decision variables)
     */
    public MOP5() {
        this(10);
    }

    /**
     * Creates a new instance of problem MOP5.
     *
     * @param numberOfVariables Number of variables.
     */
    public MOP5(Integer numberOfVariables) {
        setNumberOfVariables(numberOfVariables);
        setNumberOfObjectives(2);
        setName("MOP5");

        List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables());
        List<Double> upperLimit = new ArrayList<>(getNumberOfVariables());

        for (int i = 0; i < getNumberOfVariables(); i++) {
            lowerLimit.add(0.0);
            upperLimit.add(1.0);
        }

        setLowerLimit(lowerLimit);
        setUpperLimit(upperLimit);
    }

    /**
     * Evaluate() method
     */
    public void evaluate(DoubleSolution solution) {
        double[] f = new double[getNumberOfObjectives()];

        double g = this.evalG(solution);
        f[0] = (1 + g) * solution.getVariableValue(0);
        f[1] = (1 + g) * (1 - Math.sqrt(solution.getVariableValue(0)));

        solution.setObjective(0, f[0]);
        solution.setObjective(1, f[1]);
    }

    /**
     * Returns the value of the MOP5 function G.
     *
     * @param solution Solution
     */
    private double evalG(DoubleSolution solution) {
        double g = 0.0;
        for (int i = 1; i < solution.getNumberOfVariables(); i++) {
            double t = solution.getVariableValue(i) - Math.sin(0.5 * Math.PI * solution.getVariableValue(0));
            g += -0.9 * t * t + Math.pow(Math.abs(t), 0.6);
        }
        g = 2 * Math.abs(Math.cos(Math.PI * solution.getVariableValue(0))) * g;
        return g;
    }

}
