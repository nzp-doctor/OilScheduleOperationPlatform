package opt.jmetal.qualityindicator.impl.hypervolume.util;

import opt.jmetal.solution.Solution;
import opt.jmetal.util.JMetalException;
import opt.jmetal.util.front.imp.ArrayFront;
import opt.jmetal.util.point.Point;

import java.util.List;

/**
 * Created by ajnebro on 3/2/15.
 */
@SuppressWarnings("serial")
public class WfgHypervolumeFront extends ArrayFront {

    public WfgHypervolumeFront() {
        super();
    }

    public WfgHypervolumeFront(List<? extends Solution<?>> solutionList) {
        super(solutionList);
    }

    public WfgHypervolumeFront(int numberOfPoints, int dimensions) {
        super(numberOfPoints, dimensions);
    }

    public void setNumberOfPoints(int numberOfPoints) {
        this.numberOfPoints = numberOfPoints;
    }

    @Override
    public int getNumberOfPoints() {
        return numberOfPoints;
    }

    @Override
    public Point getPoint(int index) {
        if (index < 0) {
            throw new JMetalException("The index value is negative");
        }

        return points[index];
    }

    @Override
    public void setPoint(int index, Point point) {
        if (index < 0) {
            throw new JMetalException("The index value is negative");
        } else if (point == null) {
            throw new JMetalException("The point is null");
        }
        points[index] = point;
    }
}
