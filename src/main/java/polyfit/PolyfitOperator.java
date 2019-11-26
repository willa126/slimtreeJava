package polyfit;

public class PolyfitOperator {
    // NewFitting creates a new polynomial fitting context.
    //
    // Since 0.5.4
    public Fitting newFitting(double[] xs, double[]ys, int degree) {

        int n = xs.length;

        int m= degree + 1;

        double[] xtx = new double[m*m];
        double[] xty = new double[m];
        Fitting f = new Fitting(0, degree, xtx, xty);

        for(int i = 0; i < m*m; i++ ){
            f.xtx[i] = 0;
        }

        for (int i = 0; i < m; i++ ){
            f.xty[i] = 0;
        }

        for (int i = 0; i < n; i++) {
            f.Add(xs[i], ys[i]);
        }

        return f;
    }
}
