package opt.jmetal.util.front.imp;

import opt.jmetal.problem.oil.sim.oil.cop.OilScheduleConstrainedOptimizationProblem;
import opt.jmetal.solution.DoubleSolution;
import opt.jmetal.solution.Solution;
import opt.jmetal.solution.impl.DefaultDoubleSolution;
import opt.jmetal.util.fileinput.VectorFileUtils;
import opt.jmetal.util.point.Point;
import opt.jmetal.util.point.impl.ArrayPoint;
import opt.jmetal.util.JMetalException;
import opt.jmetal.util.front.Front;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.*;

/**
 * This class implements the {@link Front} interface by using an array of
 * {@link Point} objects
 *
 * @author Antonio J. Nebro
 */
@SuppressWarnings("serial")
public class ArrayFront implements Front {
    protected Point[] points;
    protected int numberOfPoints;
    private int pointDimensions;

    /**
     * Constructor
     */
    public ArrayFront() {
        points = null;
        numberOfPoints = 0;
        pointDimensions = 0;
    }

    /**
     * Constructor
     */
    public ArrayFront(List<? extends Solution<?>> solutionList) {
        if (solutionList == null) {
            throw new JMetalException("The list of solutions is null");
        } else if (solutionList.size() == 0) {
            throw new JMetalException("The list of solutions is empty");
        }

        numberOfPoints = solutionList.size();
        pointDimensions = solutionList.get(0).getNumberOfObjectives();
        points = new Point[numberOfPoints];

        points = new Point[numberOfPoints];
        for (int i = 0; i < numberOfPoints; i++) {
            Point point = new ArrayPoint(pointDimensions);
            for (int j = 0; j < pointDimensions; j++) {
                point.setValue(j, solutionList.get(i).getObjective(j));
            }
            points[i] = point;
        }
    }

    /**
     * Copy Constructor
     */
    public ArrayFront(Front front) {
        if (front == null) {
            throw new JMetalException("The front is null");
        } else if (front.getNumberOfPoints() == 0) {
            throw new JMetalException("The front is empty");
        }
        numberOfPoints = front.getNumberOfPoints();
        pointDimensions = front.getPoint(0).getDimension();
        points = new Point[numberOfPoints];

        points = new Point[numberOfPoints];
        for (int i = 0; i < numberOfPoints; i++) {
            points[i] = new ArrayPoint(front.getPoint(i));
        }
    }

    /**
     * Constructor
     */
    public ArrayFront(int numberOfPoints, int dimensions) {
        this.numberOfPoints = numberOfPoints;
        pointDimensions = dimensions;
        points = new Point[this.numberOfPoints];

        for (int i = 0; i < this.numberOfPoints; i++) {
            Point point = new ArrayPoint(pointDimensions);
            for (int j = 0; j < pointDimensions; j++) {
                point.setValue(j, 0.0);
            }
            points[i] = point;
        }
    }

    /**
     * Constructor
     *
     * @param fileName File containing the data. Each line of the file is a list of
     *                 objective values
     * @throws FileNotFoundException
     */
    public ArrayFront(String fileName) throws FileNotFoundException {
        this();
        InputStream inputStream = null;
        try {
            URL url = VectorFileUtils.class.getClassLoader().getResource(fileName);
            if (url != null) {
                String uri = Paths.get(url.toURI()).toString();
                inputStream = createInputStream(uri);
            } else {
                inputStream = createInputStream(fileName);
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        InputStreamReader isr = new InputStreamReader(inputStream);
        BufferedReader br = new BufferedReader(isr);

        List<Point> list = new ArrayList<>();
        int numberOfObjectives = 0;
        String aux;
        try {
            aux = br.readLine();

            while (aux != null) {
                StringTokenizer tokenizer = new StringTokenizer(aux, ",");
                int i = 0;
                if (numberOfObjectives == 0) {
                    numberOfObjectives = tokenizer.countTokens();
                } else if (numberOfObjectives != tokenizer.countTokens()) {
                    throw new JMetalException("Invalid number of points read. " + "Expected: " + numberOfObjectives
                            + ", received: " + tokenizer.countTokens());
                }

                Point point = new ArrayPoint(numberOfObjectives);
                while (tokenizer.hasMoreTokens()) {
                    double value = new Double(tokenizer.nextToken());
                    point.setValue(i, value);
                    i++;
                }
                list.add(point);
                aux = br.readLine();
            }
            br.close();
        } catch (IOException e) {
            throw new JMetalException("Error reading file", e);
        } catch (NumberFormatException e) {
            throw new JMetalException("Format number exception when reading file", e);
        }

        numberOfPoints = list.size();
        points = new Point[list.size()];
        points = list.toArray(points);
        if (numberOfPoints == 0) {
            pointDimensions = 0;
        } else {
            pointDimensions = points[0].getDimension();
        }
        for (int i = 0; i < numberOfPoints; i++) {
            points[i] = list.get(i);
        }
    }

    /**
     * 从double数组中加载解
     *
     * @param solutionList
     * @deprecated 该方法未被调用过，已废弃使用
     */
    @Deprecated
    public ArrayFront loadSolutions(List<Double[]> solutionList) {
        if (solutionList == null) {
            throw new JMetalException("The list of solutions is null");
        } else if (solutionList.size() == 0) {
            throw new JMetalException("The list of solutions is empty");
        }

        numberOfPoints = solutionList.size();
        pointDimensions = solutionList.get(0).length;
        points = new Point[numberOfPoints];

        points = new Point[numberOfPoints];
        for (int i = 0; i < numberOfPoints; i++) {
            Point point = new ArrayPoint(pointDimensions);
            for (int j = 0; j < pointDimensions; j++) {
                point.setValue(j, solutionList.get(i)[j]);
            }
            points[i] = point;
        }

        return this;
    }


    /**
     * 转换为doublesolutionlist
     *
     * @deprecated 该方法未被调用过，已废弃使用，请使用FrontUtils.convertFrontToSolutionList
     */
    @Deprecated
    public List<DoubleSolution> toDoubleSolutionList() {
        List<DoubleSolution> solutionList = new ArrayList<>();
        if (this.numberOfPoints == 0) {
            throw new JMetalException("The list of solutions is empty");
        }

        for (int i = 0; i < this.numberOfPoints; i++) {
            DoubleSolution solution = new DefaultDoubleSolution(new OilScheduleConstrainedOptimizationProblem(""));
            for (int j = 0; j < this.pointDimensions; j++) {// 忽略最后一个空格
                Double value = this.getPoint(i).getValue(j);
                solution.setVariableValue(j, value);
            }
            solutionList.add(solution);
        }
        return solutionList;
    }

    public InputStream createInputStream(String fileName) throws FileNotFoundException {
        InputStream inputStream = getClass().getResourceAsStream(fileName);
        if (inputStream == null) {
            inputStream = new FileInputStream(fileName);
        }

        return inputStream;
    }

    @Override
    public int getNumberOfPoints() {
        return numberOfPoints;
    }

    @Override
    public int getPointDimensions() {
        return pointDimensions;
    }

    @Override
    public Point getPoint(int index) {
        if (index < 0) {
            throw new JMetalException("The index value is negative");
        } else if (index >= numberOfPoints) {
            throw new JMetalException("The index value (" + index + ") is greater than the number of " + "points ("
                    + numberOfPoints + ")");
        }
        return points[index];
    }

    @Override
    public void setPoint(int index, Point point) {
        if (index < 0) {
            throw new JMetalException("The index value is negative");
        } else if (index >= numberOfPoints) {
            throw new JMetalException("The index value (" + index + ") is greater than the number of " + "points ("
                    + numberOfPoints + ")");
        } else if (point == null) {
            throw new JMetalException("The point is null");
        }
        points[index] = point;
    }

    @Override
    public void sort(Comparator<Point> comparator) {
        // Arrays.sort(points, comparator);
        Arrays.sort(points, 0, numberOfPoints, comparator);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ArrayFront that = (ArrayFront) o;

        if (numberOfPoints != that.numberOfPoints)
            return false;
        if (pointDimensions != that.pointDimensions)
            return false;
        if (!Arrays.equals(points, that.points))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(points);
        result = 31 * result + numberOfPoints;
        result = 31 * result + pointDimensions;
        return result;
    }

    @Override
    public String toString() {
        return Arrays.toString(points);
    }
}
