package opt.jmetal.util.pseudorandom;

import java.io.Serializable;

import opt.jmetal.util.pseudorandom.impl.JavaRandomGenerator;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class JMetalRandom implements Serializable {
    private static final long serialVersionUID = 1L;
    private static JMetalRandom instance;
    private PseudoRandomGenerator randomGenerator;

    private JMetalRandom() {
        randomGenerator = new JavaRandomGenerator();
    }

    public static JMetalRandom getInstance() {
        if (instance == null) {
            instance = new JMetalRandom();
        }
        return instance;
    }

    public void setRandomGenerator(PseudoRandomGenerator randomGenerator) {
        this.randomGenerator = randomGenerator;
    }

    public PseudoRandomGenerator getRandomGenerator() {
        return randomGenerator;
    }

    public int nextInt(int lowerBound, int upperBound) {
        return randomGenerator.nextInt(lowerBound, upperBound);
    }

    public double nextDouble() {
        return randomGenerator.nextDouble();
    }

    public double nextDouble(double lowerBound, double upperBound) {
        return randomGenerator.nextDouble(lowerBound, upperBound);
    }

    public void setSeed(long seed) {
        randomGenerator.setSeed(seed);
    }

    public long getSeed() {
        return randomGenerator.getSeed();
    }

    public String getGeneratorName() {
        return randomGenerator.getName();
    }
}
