package opt.jmetal.problem.multiobjective.glt;

import opt.jmetal.solution.DoubleSolution;
import opt.jmetal.problem.impl.AbstractDoubleProblem;

import java.util.ArrayList;
import java.util.List;

/**
 * Problem GLT2. Defined in
 * F. Gu, H.-L. Liu, and K. C. Tan, “A multiobjective evolutionary
 * algorithm using dynamic weight design method,” International Journal
 * of Innovative Computing, Information and Control, vol. 8, no. 5B, pp.
 * 3677–3688, 2012.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class GLT2 extends AbstractDoubleProblem {

    /**
     * Default constructor
     */
    public GLT2() {
        this(10);
    }

    /**
     * Constructor
     *
     * @param numberOfVariables
     */
    public GLT2(int numberOfVariables) {
        setNumberOfVariables(numberOfVariables);
        setNumberOfObjectives(2);
        setName("GLT2");

        List<Double> lowerLimit = new ArrayList<>(getNumberOfVariables());
        List<Double> upperLimit = new ArrayList<>(getNumberOfVariables());

        lowerLimit.add(0.0);
        upperLimit.add(1.0);
        for (int i = 1; i < getNumberOfVariables(); i++) {
            lowerLimit.add(-1.0);
            upperLimit.add(1.0);
        }

        setLowerLimit(lowerLimit);
        setUpperLimit(upperLimit);
    }

    @Override
    public void evaluate(DoubleSolution solution) {
        solution.setObjective(0, (1.0 + g(solution)) * (1.0 - Math.cos(Math.PI * solution.getVariableValue(0) / 2.0)));
        solution.setObjective(1, (1.0 + g(solution)) * (10.0 - 10.0 * Math.sin(solution.getVariableValue(0) * Math.PI / 2.0)));
    }

    private double g(DoubleSolution solution) {
        double result = 0.0;

        for (int i = 1; i < solution.getNumberOfVariables(); i++) {
            double value = solution.getVariableValue(i)
                    - Math.sin(2 * Math.PI * solution.getVariableValue(0) + i * Math.PI / solution.getNumberOfVariables());

            result += value * value;
        }

        return result;
    }
}
