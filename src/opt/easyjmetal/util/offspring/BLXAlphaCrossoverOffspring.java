/*
 * BLXAlphaCrossoverOffspring.java
 *
 * @author Antonio J. Nebro
 * @version 1.0
 *
 * This class returns a solution after applying SBX and Polynomial mutation
 */
package opt.easyjmetal.util.offspring;

import opt.easyjmetal.core.Operator;
import opt.easyjmetal.core.Solution;
import opt.easyjmetal.core.SolutionSet;
import opt.easyjmetal.operator.crossover.CrossoverFactory;
import opt.easyjmetal.operator.selection.SelectionFactory;
import opt.easyjmetal.util.JMException;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BLXAlphaCrossoverOffspring extends Offspring {

  private double crossoverProbability_ = 0.9;
  private double alpha_ = 0.5;
  private Operator crossover_;
  Operator mutation_;
  private Operator selection_;

  private BLXAlphaCrossoverOffspring(
          double crossoverProbability,
          double alpha) throws JMException {
  	HashMap parameters ;
    crossoverProbability_ = crossoverProbability;
    alpha_ = alpha;

    // Crossover operator
    parameters = new HashMap() ;
    parameters.put("probability", crossoverProbability_) ;
    parameters.put("alpha", alpha_) ;

    crossover_ = CrossoverFactory.getCrossoverOperator("BLXAlphaCrossover", parameters);

    selection_ = SelectionFactory.getSelectionOperator("BinaryTournament", null);
    
    id_ = "BLXAlphaCrossover";
  }

  public Solution getOffspring(SolutionSet solutionSet) {
    Solution[] parents = new Solution[2];
    Solution offSpring = null;

    try {
      parents[0] = (Solution) selection_.execute(solutionSet);
      parents[1] = (Solution) selection_.execute(solutionSet);

      Solution[] children = (Solution[]) crossover_.execute(parents);
      offSpring = children[0];
      //Create a new solution, using DE
    } catch (JMException ex) {
      Logger.getLogger(BLXAlphaCrossoverOffspring.class.getName()).log(Level.SEVERE, null, ex);
    }
    return offSpring;

  } // getOffpring

    public Solution getOffspring(SolutionSet solutionSet, SolutionSet archive) {
    Solution[] parents = new Solution[2];
    Solution offSpring = null;

    try {
      parents[0] = (Solution) selection_.execute(solutionSet);

      if (archive.size() > 0) {
          parents[1] = (Solution)selection_.execute(archive);
      } else {
          parents[1] = (Solution)selection_.execute(solutionSet);
      }

      Solution[] children = (Solution[]) crossover_.execute(parents);
      offSpring = children[0];
      //Create a new solution, using DE
    } catch (JMException ex) {
      Logger.getLogger(BLXAlphaCrossoverOffspring.class.getName()).log(Level.SEVERE, null, ex);
    }
    return offSpring;

  } // getOffpring
    
    public String configuration() {
    	String result = "-----\n" ;
    	result += "Operator: " + id_ + "\n" ;
    	result += "Probability: " + crossoverProbability_ + "\n" ;
    	result += "Alpha: " + alpha_ ;
    	
    	return result ;
    }
} // BLXAlphaCrossoverOffspring

