//  PermutationSolutionType.java
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

package opt.easyjmetal.encodings.solutiontype;

import opt.easyjmetal.encodings.solutiontype.variable.Permutation;
import opt.easyjmetal.core.Problem;
import opt.easyjmetal.core.SolutionType;
import opt.easyjmetal.core.Variable;

/** 
 * Class representing the solution type of solutions composed of Permutation
 * variables 
 */
public class PermutationSolutionType extends SolutionType {

	/**
	 * Constructor
	 * @param problem  Problem to solve
	 */
	public PermutationSolutionType(Problem problem) {
		super(problem) ;
  } // PermutationSolution
	
	/**
	 * Creates the variables of the solution
	 */
	public Variable[]  createVariables() {
		Variable [] variables = new Variable[problem_.getNumberOfVariables()];
		    
    for (int var = 0; var < problem_.getNumberOfVariables(); var++)
    	variables[var] = new Permutation(problem_.getLength(var)) ;
    
    return variables ;
	} // createVariables
} // PermutationSolutionType
