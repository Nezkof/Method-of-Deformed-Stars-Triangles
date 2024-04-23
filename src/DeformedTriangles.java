import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class DeformedTriangles {
    private final ArrayList<Triangle> P_population;
    private ArrayList<Triangle> Pz_population;
    private ArrayList<Triangle> Ps_population;
    private ArrayList<Triangle> Pw_population;

    int populationSize;
    int compressionCoefficient;

    private final int itersNumber;

    private final Function function;
    private final double[] bounds;

    private long duration;
    private double bestResult;


    public DeformedTriangles(int populationSize, int compressionCoefficient, int itersNumber, double[] bounds, Function function){
        this.itersNumber = itersNumber;
        this.populationSize = populationSize;
        this.compressionCoefficient = compressionCoefficient;
        this.bounds = bounds;
        this.function = function;

        this.P_population = createPopulation();
        this.Pz_population = new ArrayList<>();
        this.Ps_population = new ArrayList<>();
        this.Pw_population = new ArrayList<>();
    }

    public void startOptimization() {
        long startTime = System.currentTimeMillis();

        if (itersNumber != 0) {
            for (int i = 0; i < itersNumber; ++i) {
                createParallelTransferPopulation();
                createRotationPopulation();
                createCenterRotatePopulation();
                selectBestPopulation();
            }
        }

        duration = System.currentTimeMillis() - startTime;
    }

    private ArrayList<Triangle> createPopulation() {
        ArrayList<Triangle> population = new ArrayList<>();

        for (int i = 0; i < populationSize; ++i) {
            population.add(new Triangle(bounds[0], bounds[1], function));
        }
        return population;
    }

    private void createParallelTransferPopulation() {
        Pz_population.clear();
        for (Triangle triangle : P_population) {
            Pz_population.add((Triangle) triangle.clone());
        }

        for (int i = 0; i < P_population.size(); ++i) {
            Pz_population.get(i).shiftPoints(bounds[0], bounds[1], compressionCoefficient);
        }
    }

    private void createRotationPopulation() {
        Ps_population.clear();
        for (Triangle triangle : P_population) {
            Ps_population.add((Triangle) triangle.clone());
        }

        for (int i = 0; i < P_population.size(); ++i) {
            Ps_population.get(i).rotatePoints(bounds[0], bounds[1]);
        }
    }

    private void createCenterRotatePopulation() {
        Pw_population.clear();
        for (Triangle triangle : P_population) {
            Pw_population.add((Triangle) triangle.clone());
        }

        for (int i = 0; i < P_population.size(); ++i) {
            Pw_population.get(i).rotateCenterPoints(bounds[0], bounds[1]);
        }
    }

    private void selectBestPopulation() {
        ArrayList<Triangle> combinedPopulation = new ArrayList<>();
        combinedPopulation.addAll(P_population);
        combinedPopulation.addAll(Pz_population);
        combinedPopulation.addAll(Ps_population);
        combinedPopulation.addAll(Pw_population);

        combinedPopulation.sort((triangle1, triangle2) -> Double.compare(function.calculate(triangle1.points.get(0)[0], triangle1.points.get(0)[1]), function.calculate(triangle2.points.get(0)[0], triangle2.points.get(0)[1])));

        P_population.clear();
        P_population.addAll(combinedPopulation.subList(0, populationSize));
        ArrayList<Double> populationResults = new ArrayList<>();

        for (int i = 0; i < P_population.size(); ++i) {
            populationResults.add(function.calculate(P_population.get(0).getBestPoint()[0], P_population.get(0).getBestPoint()[1]));
        }

        bestResult = function.calculate(P_population.get(0).getBestPoint()[0], P_population.get(0).getBestPoint()[1]);
    }

    public long getDuration(){
        return duration;
    }

    public double getBestResult() {return bestResult;}
}
