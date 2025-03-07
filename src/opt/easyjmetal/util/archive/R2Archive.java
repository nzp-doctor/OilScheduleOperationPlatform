//  R2Archive.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <juanjo.durillo@gmail.com>
//
//  Copyright (c) 2013 Antonio J. Nebro, Juan J. Durillo
//
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

package opt.easyjmetal.util.archive;

import opt.easyjmetal.core.Solution;
import opt.easyjmetal.qualityindicator.R2;
import opt.easyjmetal.util.comparators.line.ConstraintDominanceComparator;
import opt.easyjmetal.util.permutation.PseudoRandom;
import opt.easyjmetal.util.comparators.line.CrowdingDistanceComparator;
import opt.easyjmetal.util.comparators.line.EqualSolutionsComparator;

import java.util.Comparator;

/**
 * This class implements a bounded archive based on crowding distances (as
 * defined in NSGA-II).
 */
public class R2Archive extends Archive {

    private int maxSize_;
    private int objectives_;
    private Comparator dominance_;
    private Comparator equals_;
    private Comparator crowdingDistance_;
    private R2 r2Indicator_;

    /**
     * Constructor. Creates an R2Archive for a problem of 2 objectives
     *
     * @param maxSize The maximum size of the archive.
     */
    public R2Archive(int maxSize) {
        super(maxSize);
        maxSize_ = maxSize;
        objectives_ = 2;        // hardcoded
        dominance_ = new ConstraintDominanceComparator();
        equals_ = new EqualSolutionsComparator();
        crowdingDistance_ = new CrowdingDistanceComparator();
        r2Indicator_ = new R2();
    }

    /**
     * Constructor.
     *
     * @param maxSize            The maximum size of the archive.
     * @param numberOfObjectives The number of objectives.
     */
    public R2Archive(int maxSize, int numberOfObjectives, String file) {
        super(maxSize);
        maxSize_ = maxSize;
        objectives_ = numberOfObjectives;
        dominance_ = new ConstraintDominanceComparator();
        equals_ = new EqualSolutionsComparator();
        crowdingDistance_ = new CrowdingDistanceComparator();
        r2Indicator_ = new R2(numberOfObjectives, file);
    }


    /**
     * Adds a <code>Solution</code> to the archive. If the <code>Solution</code>
     * is dominated by any member of the archive, then it is discarded. If the
     * <code>Solution</code> dominates some members of the archive, these are
     * removed. If the archive is full and the <code>Solution</code> has to be
     * inserted, the solutions are sorted by crowding distance and the one having
     * the minimum crowding distance value.
     *
     * @param solution The <code>Solution</code>
     * @return true if the <code>Solution</code> has been inserted, false
     * otherwise.
     */
    @Override
    public boolean add(Solution solution) {
        int flag = 0;
        int i = 0;
        Solution aux;

        while (i < solutionsList_.size()) {
            aux = solutionsList_.get(i);

            flag = dominance_.compare(solution, aux);
            if (flag == 1) {               // The solution to add is dominated
                return false;                // Discard the new solution
            } else if (flag == -1) {       // A solution in the archive is dominated
                solutionsList_.remove(i);    // Remove it from the population
            } else {
                if (equals_.compare(aux, solution) == 0) { // There is an equal solution
                    // in the population
                    return false; // Discard the new solution
                }
                i++;
            }
        }
        // Insert the solution into the archive
        solutionsList_.add(solution);
        if (size() > maxSize_) {
            int indexWorst = this.r2Indicator_.getWorst(this);
            remove(indexWorst);
        }
        return true;
    }

    /**
     * Returns a solution from the archive based on their contribution to the R2
     * indicator. The solution is chosen using a binary tournament.
     */
    public Solution
    getSolution() {
        int index1, index2;
        index1 = PseudoRandom.randInt(0, size() - 1);
        index2 = PseudoRandom.randInt(0, size() - 1);
        double aux1 = this.r2Indicator_.R2Without(this, index1);
        double aux2 = this.r2Indicator_.R2Without(this, index2);

        if (aux1 > aux2) {
            // means that index1 contributed less than index2
            return this.get(index1);
        } else {
            return this.get(index2);
        }
    }
}
