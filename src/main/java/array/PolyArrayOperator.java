package array;

import math.bits.BitsOperator;
import polyfit.Fitting;
import polyfit.PolyfitOperator;
import proto.Array;
import util.Four;
import util.Pair;

import java.util.ArrayList;
import java.util.List;

public class PolyArrayOperator {

    public static final int maxEltWidth = 16;
    public static final int maxBitIndex = 1 << 16;
    public static final int maxSegmentSize = maxBitIndex / maxEltWidth;

    // allows max eltWidth=64 thus "start" is under 2^16
    public static final int segmentSize = 1024;
    public static final int segWidth = 10;//uint
    public static final int segMask = 1024 - 1;

    public static final int polyDegree = 2;
    public static final int polyCoefCnt = polyDegree + 1;

    // NewPolyArray creates a "PolyArray" array from a slice of int32.
    // A "PolyArray" array uses several polynomial curves to compress data.
    //
    // It is very efficient to store a serias integers with a overall trend, such as
    // a sorted array.
    //
    // Since 0.5.2
    Array.PolyArray newPolyArray(List<Integer> nums) throws Exception {

        Array.PolyArray.Builder polyArrayBuilder = Array.PolyArray.newBuilder();
        polyArrayBuilder.setN(nums.size());
        while (true) {
            if (nums.size() > segmentSize) {
                addSeg(polyArrayBuilder, nums.subList(0, segmentSize));
                nums = nums.subList(segmentSize, nums.size() - 1);
            } else {
                addSeg(polyArrayBuilder, nums);
                break;
            }
        }

        List<Array.Segment> segs = new ArrayList<Array.Segment>(polyArrayBuilder.getSegmentsCount());
        segs.addAll(polyArrayBuilder.getSegmentsList());
        polyArrayBuilder.clearSegments().addAllSegments(segs);

        return polyArrayBuilder.build();
    }

    public void addSeg(Array.PolyArray.Builder polyArrayBuilder, List<Integer> nums) throws Exception {

        int n = nums.size();
        double[] xs = new double[n];
        double[] ys = new double[n];

        for (int i = 0; i < nums.size(); i++) {
            xs[i] = (double) i;
            ys[i] = (double) nums.get(i);
        }

        // min polyspan
        int polyspan = 16;
        List<Fitting> fts = initFittings(xs, ys, polyspan);

        Four<Integer, List<Double>, int[], List<Fitting>> result = findMinFittings(xs, ys, fts, polyspan);
        polyspan = result.first;
        List<Double> polys = result.second;
        int[] widths = result.third;
        fts = result.fourth;
        List<Integer> infos = new ArrayList<Integer>();//uint32
        List<Long> words = new ArrayList<Long>(n);// max size

        // where the first elt of a polynomial in words
        int start = 0;

        int s = 0;
        int e = 0;
        for (int i = 0; i < fts.size(); i++) {
            s = e;
            e += fts.get(i).N;

            List<Double> poly = polys.subList(i * polyCoefCnt, i * polyCoefCnt + polyCoefCnt);
            int eltWidth = widths[i];
            int margin = 1 << eltWidth - 1;
            if (eltWidth > 0) {
                start = start + eltWidth - 1;
                start -= start % eltWidth;
            }

            if (start >= 65536) {
                throw new Exception(String.format("wordStart is too large:%d", start));
            }

            infos.add(packInfo(start, eltWidth));

            for (int j = s; j < e; j++) {

                double v = evalpoly(poly, xs[j]);

                long d = (long) nums.get(j) - (long) v;
                if (d > (long) margin || d < 0) {
                    throw new Exception(String.format("d=%d must smaller than %d and > 0", d, margin));
                }
                int iWord = start >> 6;
                words.set(iWord, words.get(iWord) | (d << start & 63));
                start += eltWidth;
            }
        }

        // last start is for len(nums)
        infos.add(packInfo(start, 0));

        int nWords = (start + 63) >> 6;

        Array.Segment seg = Array.Segment.newBuilder().build();
        Array.Segment.Builder segBuilder = seg.toBuilder();
        segBuilder.setPolySpan(polyspan);
        segBuilder.setPolySpanWidth(log2u64((long)polyspan));

        segBuilder.clearPolynomials().addAllPolynomials(polys);
        segBuilder.clearWords().addAllWords(words);
        segBuilder.clearInfo().addAllInfo(infos);

        polyArrayBuilder.addSegments(seg);
    }

    //uint32
    public int packInfo(int start, int width) {
        return (start << 8) + width;
    }

    //uint32
    int log2u64(long i) {

        if (i == 0 ){
            return 0;
        }

        return 63 - BitsOperator.leadingZeros64(i);
    }

    List<Fitting> initFittings(double[] xs, double[] ys, int polysize) {

        List<Fitting> fts = new ArrayList<Fitting>();
        int n = xs.length;

        for (int i = 0; i < n; i += polysize) {
            int s = i;
            int e = s + polysize;
            if (e > n) {
                e = n;
            }

            double[] xx = cloneArray(xs, s, e);
            double[] yy = cloneArray(ys, s, e);
            PolyfitOperator polyfitOperator = new PolyfitOperator();
            Fitting ft = polyfitOperator.newFitting(xx, yy, polyDegree);
            fts.add(ft);
        }
        return fts;
    }

    // return (int32, []float64, []uint32, []*polyfit.Fitting)
    public Four<Integer, List<Double>, int[], List<Fitting>> findMinFittings(
            double[] xs, double[] ys, List<Fitting> fts, int polysize) throws Exception {
        int minMem = 1 << 30;

        List<Double> minPolys = new ArrayList<Double>();
        List<Integer> minWidths = new ArrayList<Integer>(); //uint32
        List<Fitting> minFts = new ArrayList<Fitting>();//        minFts := []*polyfit.Fitting(nil)
        int minPolySize = polysize;
        while (true) {
            List<Double> polys = new ArrayList<Double>();
            List<Integer> widths = new ArrayList<Integer>();//uint32

            int mem = 0;

            int s =0;
            int e = 0;
            for (Fitting ft : fts) {
                s = e;
                e += ft.N;

                List<Double> poly = ft.Solve(true);
                Pair<Double, Double> pair = maxminResiduals(poly, cloneArray(xs, s, e), cloneArray(ys, s, e));
                double max = pair.left;
                double min = pair.right;
                int margin = (int) (Math.ceil(max - min));
                poly.set(0, poly.get(0) + min);

                int eltWidth = marginWidth(margin);
                mem += memCost(poly, eltWidth, ft.N);

                polys.addAll(poly);
                widths.add(eltWidth);
            }

            if (minMem > mem) {
                minMem = mem;
                minPolys = new ArrayList<Double>(polys);
                minWidths = new ArrayList<Integer>(widths);
                minFts = fts;
                minPolySize = polysize;

                fts = mergeFittings(fts);
                polysize *= 2;

            } else {
                return new Four(minPolySize, minPolys, minWidths, minFts);
            }
        }

    }

    List<Fitting> mergeFittings(List<Fitting> fts) throws Exception{

        List<Fitting> newFts = new ArrayList<Fitting>();
        for(int i = 0; i < fts.size()/2; i++ ){
            double[] xs = new double[0];
            double[] ys = new double[0];
            PolyfitOperator polyfitOperator = new PolyfitOperator();
            Fitting f = polyfitOperator.newFitting(xs, ys, fts.get(i*2).Degree);
            f.merge(fts.get(i*2));
            f.merge(fts.get(i*2+1));
            newFts.add(f);
        }
        if (fts.size()%2 == 1) {
            int i = fts.size() - 1;
            PolyfitOperator polyfitOperator = new PolyfitOperator();
            Fitting f = polyfitOperator.newFitting(new double[0], new double[0], fts.get(i).Degree);
            f.merge(fts.get(i));
            newFts.add(f);
        }

        return newFts;
    }

    public double[] cloneArray(double[] array, int s, int e) {
        int size = e - s;
        double[] result = new double[size];
        for (int i = 0; i < size; i++) {
            result[i] = array[i + s];
        }
        return result;
    }

    // maxminResiduals finds max and min residuals along a curve.
    //
    // Since 0.5.2
    Pair<Double, Double> maxminResiduals(List<Double> poly, double[] xs, double[] ys) {

        double max = 0f;
        double min = 0f;

        for (int i = 0; i < xs.length; i++) {
            double v = evalpoly(poly, xs[i]);
            double diff = ys[i] - v;
            if (diff > max) {
                max = diff;
            }
            if (diff < min) {
                min = diff;
            }
        }

        return new Pair<Double, Double>(max, min);
    }

    // evalpoly is a quick path of eval.
    //
    // Since 0.5.2
    public double evalpoly(List<Double> poly, double x) {
        // return poly[0] + poly[1]*x + poly[2]*x*x + poly[3]*x*x*x
        return poly.get(0) + poly.get(1) * x + poly.get(2) * x * x;
    }

    // return uint32
    public int marginWidth(int margin) throws Exception {
        int[] array = new int[]{0, 1, 2, 4, 8, 16};
        for (int width : array) {
            if (1 << width > margin) {
                return width;
            }
        }

        throw new Exception(String.format("margin is too large: %d >= 2^16", margin));
    }

    public int memCost(List<Double> poly, int eltWidth, int n) {
        int mm = 0;
        mm += 64;                             // PolySpan and PolySpanWidth
        mm += 64 * poly.size();                 // Polynomials
        mm += 32 * (poly.size() / polyCoefCnt); // Info
        mm += eltWidth * n;         // Words
        return mm;
    }
}
