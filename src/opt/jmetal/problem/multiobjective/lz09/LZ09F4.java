package opt.jmetal.problem.multiobjective.lz09;

import opt.jmetal.solution.DoubleSolution;
import opt.jmetal.problem.impl.AbstractDoubleProblem;
import opt.jmetal.util.JMetalException;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing problem LZ09F4
 */
@SuppressWarnings("serial")
public class LZ09F4 extends AbstractDoubleProblem {

    private LZ09 lz09;

    /**
     * Creates a default LZ09F4 problem (30 variables and 2 objectives)
     **/
    public LZ09F4() {
        this(21, 1, 24);
    }

    /**
     * Creates a LZ09F4 problem instance
     */
    public LZ09F4(Integer ptype,
                  Integer dtype,
                  Integer ltype) throws JMetalException {
        setNumberOfVariables(30);
        setNumberOfObjectives(2);
        setNumberOfConstraints(0);
        setName("LZ09F4");

        lz09 = new LZ09(getNumberOfVariables(),
                getNumberOfObjectives(),
                ptype,
                dtype,
                ltype);

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
        List<Double> x = new ArrayList<Double>(getNumberOfVariables());
        List<Double> y = new ArrayList<Double>(getNumberOfObjectives());

        for (int i = 0; i < getNumberOfVariables(); i++) {
            x.add(solution.getVariableValue(i));
            y.add(0.0);
        }

        lz09.objective(x, y);

        for (int i = 0; i < getNumberOfObjectives(); i++) {
            solution.setObjective(i, y.get(i));
        }
    }
}


