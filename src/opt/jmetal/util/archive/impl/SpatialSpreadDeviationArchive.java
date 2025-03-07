//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package opt.jmetal.util.archive.impl;

import opt.jmetal.solution.Solution;
import opt.jmetal.util.comparator.SpatialSpreadDeviationComparator;
import opt.jmetal.util.solutionattribute.DensityEstimator;
import opt.jmetal.util.solutionattribute.impl.SpatialSpreadDeviation;
import opt.jmetal.util.SolutionListUtils;

import java.util.Comparator;

/**
 * @author Alejandro Santiago <aurelio.santiago@upalt.edu.mx>
 */
public class SpatialSpreadDeviationArchive<S extends Solution<?>> extends AbstractBoundedArchive<S> {
    private Comparator<S> crowdingDistanceComparator;
    private DensityEstimator<S> crowdingDistance;

    public SpatialSpreadDeviationArchive(int maxSize) {
        super(maxSize);
        crowdingDistanceComparator = new SpatialSpreadDeviationComparator<S>();
        crowdingDistance = new SpatialSpreadDeviation<S>();
    }

    @Override
    public void prune() {
        if (getSolutionList().size() > getMaxSize()) {
            computeDensityEstimator();
            S worst = new SolutionListUtils().findWorstSolution(getSolutionList(), crowdingDistanceComparator);
            getSolutionList().remove(worst);
        }
    }

    @Override
    public void sortByDensityEstimator() {
        getSolutionList().sort(getComparator());
    }

    @Override
    public Comparator<S> getComparator() {
        return crowdingDistanceComparator;
    }

    @Override
    public void computeDensityEstimator() {
        crowdingDistance.computeDensityEstimator(getSolutionList());
    }
}
