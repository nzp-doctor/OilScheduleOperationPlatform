//  RankingAndCrowdingSelection.java
//
//  Author:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
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

package opt.easyjmetal.operator.selection;

import opt.easyjmetal.core.Problem;
import opt.easyjmetal.core.SolutionSet;
import opt.easyjmetal.util.Configuration;
import opt.easyjmetal.util.JMException;
import opt.easyjmetal.util.comparators.line.CrowdingDistanceComparator;
import opt.easyjmetal.util.distance.Distance;
import opt.easyjmetal.util.ranking.impl.RankingByCDP;

import java.util.Comparator;
import java.util.HashMap;

/**
 * This class implements a selection for selecting a number of solutions from
 * a solutionSet. The solutions are taken by mean of its ranking and
 * crowding ditance values.
 * NOTE: if you use the default constructor, the problem has to be passed as
 * a parameter before invoking the execute() method -- see lines 67 - 74
 */
public class RankingAndCrowdingSelection extends Selection {

  /**
   * stores the problem to solve
   */
  private Problem problem_ = null ;

  /**
   * stores a <code>Comparator</code> for crowding comparator checking.
   */
  private static final Comparator crowdingComparator_ = new CrowdingDistanceComparator();


  /**
   * stores a <code>Distance</code> object for distance utilities.
   */
  private static final Distance distance_ = new Distance();

  /**
   * Constructor
   */
  public RankingAndCrowdingSelection(HashMap<String, Object> parameters) {
    super(parameters) ;

  	if (parameters.get("problem") != null)
  		problem_ = (Problem) parameters.get("problem") ;

    if (problem_ == null) {
      Configuration.logger_.severe("RankingAndCrowdingSelection.execute: " +
          "problem not specified") ;
      Class cls = String.class;
      String name = cls.getName();
    } // if

  } // RankingAndCrowdingSelection

  /**
   * Constructor
   * @param problem Problem to be solved
   */
  //public RankingAndCrowdingSelection(Problem problem) {
  //  problem_ = problem;
  //} // RankingAndCrowdingSelection

  /**
  * Performs the operation
  * @param object Object representing a SolutionSet.
  * @return an object representing a <code>SolutionSet<code> with the selected parents
   * @throws JMException
  */
  public Object execute (Object object) throws JMException {
    SolutionSet population = (SolutionSet)object;
    int populationSize     = (Integer)parameters_.get("populationSize");
    SolutionSet result     = new SolutionSet(populationSize);

    //->RankingByCDP the union
    RankingByCDP ranking = new RankingByCDP(population);

    int remain = populationSize;
    int index  = 0;
    SolutionSet front = null;
    population.clear();

    //-> Obtain the next front
    front = ranking.getSubfront(index);

    while ((remain > 0) && (remain >= front.size())){
      //Asign crowding distance to individuals
      distance_.crowdingDistanceAssignment(front,problem_.getNumberOfObjectives());
      //Add the individuals of this front
      for (int k = 0; k < front.size(); k++ ) {
        result.add(front.get(k));
      } // for

      //Decrement remaint
      remain = remain - front.size();

      //Obtain the next front
      index++;
      if (remain > 0) {
        front = ranking.getSubfront(index);
      } // if
    } // while

    //-> remain is less than front(index).size, insert only the best one
    if (remain > 0) {  // front containt individuals to insert
      distance_.crowdingDistanceAssignment(front,problem_.getNumberOfObjectives());
      front.sort(crowdingComparator_);
      for (int k = 0; k < remain; k++) {
        result.add(front.get(k));
      } // for

      remain = 0;
    } // if

    return result;
  } // execute
} // RankingAndCrowding
