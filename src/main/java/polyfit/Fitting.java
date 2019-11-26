package polyfit;

import java.util.ArrayList;
import java.util.List;

public class Fitting {
    public int N;
    public int Degree;

    // cache Xᵀ X
    double[] xtx;
    // cache Xᵀ Y
    double[] xty;

    Fitting(int N, int Degree, double[] xtx, double[] xty) {
        this.N = N;
        this.Degree = Degree;
        this.xtx = xtx;
        this.xty = xty;
    }

    // Add a point(x, y) into this fitting.
    //
    // Since 0.5.4
    public void Add(double x, double y) {

        int m = Degree + 1;
        double[] xpows = new double[m];
        double v = 1f;
        for (int i = 0; i < m; i++) {
            xpows[i] = v;
            v *= x;
        }

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < m; j++) {
                xtx[i * m + j] += xpows[i] * xpows[j];
            }
        }

        for (int i = 0; i < m; i++) {
            xty[i] += xpows[i] * y;
        }

        N++;
    }

    // Solve the equation and returns coefficients of result polynomial.
    // The number of coefficients is f.Degree + 1.
    //
    // Since 0.5.4 yl_todo
    public List<Double> Solve(boolean minimizeDegree) {

        /*int m = Degree + 1;

        coef:=mat.NewDense(m, m, xtx);
        right:=mat.NewDense(m, 1, xty);

        if (minimizeDegree && Degree + 1 > N) {

            m = N;
            coef = coef.Slice(0, m, 0, m). ( * mat.Dense);
            right = right.Slice(0, m, 0, 1). ( * mat.Dense);
        }

        mat.Dense beta;
        beta.Solve(coef, right);*/

        List<Double> rst = new ArrayList<Double>(Degree + 1);
        /*for (int i = 0; i < m; i++) {
            rst.set(i, beta.At(i, 0));
        }

        for (int i = m; i < Degree + 1; i++) {
            rst.set(i, 0D);
        }*/

        return rst;
    }

    // Merge Combines two sets of sample data.
    //
    // This can be done because:
    //    |X₁|ᵀ × |X₁| = X₁ᵀ × X₁ + X₂ᵀ × X₂
    //    |X₂|    |X₂|
    //
    // Since 0.5.4
    public void merge(Fitting b) throws Exception {

        if (Degree != b.Degree) {
            throw new Exception(String.format("different degree: %d %d", Degree, b.Degree));
        }

        N += b.N;

        int m = Degree + 1;

        for (int i = 0; i < m; i++) {
            xty[i] += b.xty[i];
            for (int j = 0; j < m; j++) {
                xtx[i * m + j] += b.xtx[i * m + j];
            }
        }
    }
}
