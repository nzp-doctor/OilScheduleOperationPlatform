package opt.jmetal.operator.impl.selection;

import opt.jmetal.operator.SelectionOperator;
import opt.jmetal.util.JMetalException;
import opt.jmetal.util.SolutionListUtils;

import java.util.List;

/**
 * This class implements a random selection operator used for selecting randomly a solution from a list
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class RandomSelection<S> implements SelectionOperator<List<S>, S> {

    /**
     * Execute() method
     */
    public S execute(List<S> solutionList) {
        if (null == solutionList) {
            throw new JMetalException("The solution list is null");
        } else if (solutionList.isEmpty()) {
            throw new JMetalException("The solution list is empty");
        }

        List<S> list = SolutionListUtils.selectNRandomDifferentSolutions(1, solutionList);

        return list.get(0);
    }
}
