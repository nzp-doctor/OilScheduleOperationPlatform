/*
 * SBXCrossover.java
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

public class SBXCrossoverOffspring extends Offspring {

  private double crossoverProbability_ = 0.9;
  private double distributionIndexForCrossover_ = 20;
  private Operator crossover_;
  private Operator selection_;

  public SBXCrossoverOffspring(double crossoverProbability,
      double distributionIndexForCrossover) throws JMException {
    HashMap parameters ;
    crossoverProbability_ = crossoverProbability;
    distributionIndexForCrossover_ = distributionIndexForCrossover;

    // Crossover operator
    parameters = new HashMap() ;
    parameters.put("probability", crossoverProbability_) ;
    parameters.put("distributionIndex", distributionIndexForCrossover_) ;

    crossover_ = CrossoverFactory.getCrossoverOperator("SBXCrossover", parameters);

    selection_ = SelectionFactory.getSelectionOperator("BinaryTournament", null);

    id_ = "SBXCrossover";
  }

  public Solution getOffspring(SolutionSet solutionSet) {
    Solution[] parents = new Solution[2];
    Solution offSpring = null;

    try {
      parents[0] = (Solution) selection_.execute(solutionSet);
      parents[1] = (Solution) selection_.execute(solutionSet);

      Solution[] children = (Solution[]) crossover_.execute(parents);
      offSpring = children[0];
      //mutation_.execute(offSpring);
      //Create a new solution, using DE
    } catch (JMException ex) {
      Logger.getLogger(SBXCrossoverOffspring.class.getName()).log(Level.SEVERE, null, ex);
    }
    return offSpring;

  } // getOffpring

  /**
   * 
   */
  public Solution getOffspring(Solution[] parentSolutions) {
    Solution[] parents = new Solution[2];
    Solution offSpring = null;

    try {
      parents[0] = parentSolutions[0] ;
      parents[1] = parentSolutions[1] ;

      Solution[] children = (Solution[]) crossover_.execute(parents);
      offSpring = children[0];
      //mutation_.execute(offSpring);
      //Create a new solution, using DE
    } catch (JMException ex) {
      Logger.getLogger(SBXCrossoverOffspring.class.getName()).log(Level.SEVERE, null, ex);
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
      //mutation_.execute(offSpring);
      //Create a new solution, using DE
    } catch (JMException ex) {
      Logger.getLogger(SBXCrossoverOffspring.class.getName()).log(Level.SEVERE, null, ex);
    }
    return offSpring;

  } // getOffpring

  public String configuration() {
    String result = "-----\n" ;
    result += "Operator: " + id_ + "\n" ;
    result += "Probability: " + crossoverProbability_ + "\n" ;
    result += "DistributionIndex: " + distributionIndexForCrossover_ ;

    return result ;
  }
} // DifferentialEvolutionOffspring

