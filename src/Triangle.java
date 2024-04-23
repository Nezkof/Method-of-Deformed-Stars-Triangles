import java.beans.beancontext.BeanContext;
import java.util.ArrayList;
import java.util.Random;

public class Triangle {
    ArrayList<double[]> points;
    double[] centralPoint;
    int bestPoint;
    Random random;
    Function function;

    @Override
    public Object clone() {
        Triangle clonedTriangle = new Triangle();
        clonedTriangle.bestPoint = this.bestPoint;
        clonedTriangle.random = new Random();
        clonedTriangle.points = new ArrayList<>(this.points.size());
        for (double[] point : this.points) {
            clonedTriangle.points.add(point.clone());
        }
        clonedTriangle.centralPoint = this.centralPoint.clone();
        clonedTriangle.function = this.function; // Виберіть, які поля копіювати або передавати посиланням
        return clonedTriangle;
    }


    public Triangle() {
        points = new ArrayList<>();
        centralPoint = new double[2];
    }

    public Triangle(double leftBound, double rightBound, Function function) {
        random = new Random();
        centralPoint = new double[2];
        this.function = function;
        createPoints(leftBound, rightBound);
        evaluateBestPoint();
    }

    private void createPoints(double leftBound, double rightBound){
        points = new ArrayList<>();
        for (int i = 0; i < 3; ++i) {
            points.add(new double[] {
                    random.nextDouble(rightBound - leftBound + 1) + leftBound,
                    random.nextDouble(rightBound - leftBound + 1) + leftBound
            });

            centralPoint[0] += points.get(i)[0];
            centralPoint[1] += points.get(i)[1];
        }

        centralPoint[0] /= 3;
        centralPoint[1] /= 3;
    }

    public void evaluateBestPoint(){
        bestPoint = 0;
        double bestResult = function.calculate(points.get(0)[0],points.get(0)[1]);

        for (int i = 1; i < points.size(); i++) {
            double[] currentPoint = points.get(i);
            double currentResult = function.calculate(currentPoint[0], currentPoint[1]);
            if (currentResult < bestResult) {
                bestResult = currentResult;
                bestPoint = i;
            }
        }
    }

    public void shiftPoints(double leftBound, double rightBound, double a) {
        evaluateBestPoint();
        //double a = random.nextDouble() * (rightBound - leftBound) + leftBound;
        points.get(bestPoint)[0] = (1+a)*points.get(bestPoint)[0] - a*centralPoint[0];
        points.get(bestPoint)[1] = (1+a)*points.get(bestPoint)[1] - a*centralPoint[1];

        points.set(bestPoint, validatePoint(points.get(bestPoint), leftBound, rightBound));

        for (int i = 0; i < points.size(); ++i){
            if (i == bestPoint)
                continue;
            points.get(i)[0] = (points.get(bestPoint)[0] + a*points.get(i)[0])/(1+a);
            points.get(i)[1] = (points.get(bestPoint)[1] + a*points.get(i)[1])/(1+a);
            points.set(i, validatePoint(points.get(i), leftBound, rightBound));
        }
    }

    public void rotatePoints(double leftBound, double rightBound) {
        evaluateBestPoint();
        for (int i = 0; i < points.size(); ++i){
            if (i == bestPoint)
                continue;

            double b = random.nextDouble() * (rightBound - leftBound) + leftBound;
            points.get(i)[0] = points.get(i)[0] + (points.get(bestPoint)[0])*Math.cos(b) - (points.get(i)[1] - points.get(bestPoint)[1])*Math.sin(b);
            points.get(i)[1] = points.get(i)[1] + (points.get(bestPoint)[1])*Math.sin(b) - (points.get(i)[1] - points.get(bestPoint)[1])*Math.cos(b);
            points.set(i, validatePoint(points.get(i), leftBound, rightBound));
        }
    }

    public void rotateCenterPoints(double leftBound, double rightBound) {
        evaluateBestPoint();
        for (int i = 0; i < points.size(); ++i){
            if (i == bestPoint)
                continue;

            double alpha = random.nextDouble() * (rightBound - leftBound) + leftBound;
            points.get(i)[0] = (points.get(i)[0] - centralPoint[0])*Math.cos(alpha) - (points.get(i)[1] - centralPoint[1]*Math.sin(alpha) + centralPoint[0]);
            points.get(i)[1] = (points.get(i)[0] - centralPoint[0])*Math.cos(alpha) - (points.get(i)[1] - centralPoint[1]*Math.sin(alpha) + centralPoint[1]);
            points.set(i, validatePoint(points.get(i), leftBound, rightBound));
        }
    }

    public double[] validatePoint(double[] point, double leftBound, double rightBound) {
        if (point[0] < leftBound && point[1] < leftBound) {
            point[0] -= (leftBound - rightBound);
            point[1] -= (leftBound - rightBound);
        }

        if (point[0] < leftBound && point[1] > rightBound) {
            point[0] -= (leftBound - rightBound);
            point[1] += (leftBound - rightBound);
        }

        if (point[0] > rightBound && point[1] > rightBound) {
            point[0] += (leftBound - rightBound);
            point[1] += (leftBound - rightBound);
        }

        if (point[0] > rightBound && point[1] < leftBound) {
            point[0] += (leftBound - rightBound);
            point[1] -= (leftBound - rightBound);
        }

        if(point[0] >= leftBound && point[0] <= rightBound) {
            if (point[1] > rightBound)
                point[1] += leftBound - rightBound;
            if (point[1] < leftBound)
                point[1] -= leftBound - rightBound;
        }

        if (point[1] >= leftBound && point[1] <= rightBound) {
            if (point[0] > rightBound)
                point[0] += leftBound - rightBound;
            if (point[0] < leftBound)
                point[0] -= leftBound - rightBound;
        }

        return point;
    }

    public double[] getBestPoint(){
        evaluateBestPoint();
        return points.get(bestPoint);
    }


}
