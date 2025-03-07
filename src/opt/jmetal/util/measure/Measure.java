package opt.jmetal.util.measure;

import opt.jmetal.algorithm.Algorithm;
import opt.jmetal.util.naming.DescribedEntity;

import java.io.Serializable;

/**
 * A {@link Measure} aims at providing the {@link Value} of a specific property,
 * typically of an {@link Algorithm}. In order to facilitate external uses, it
 * implements the methods of {@link DescribedEntity}.
 *
 * @param <Value> the type of value the {@link Measure} can provide
 * @author Created by Antonio J. Nebro on 21/10/14 based on the ideas of
 * Matthieu Vergne <matthieu.vergne@gmail.com>
 */
public interface Measure<Value> extends DescribedEntity, Serializable {
}
