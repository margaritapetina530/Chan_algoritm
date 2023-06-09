import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Chan {
    public static int iterate = 0;
    static int power(int base, int exp)
    {
        int i=0;
        int result=1;
        for(i=0;i<exp;i++)
        {
            result=result*base;
            iterate += 1;
        }
        return result;
    }

    /* Checks the output returned by Jarvis March algorithm and checks which list of points i.e which sub-Convex hull
     * has the extreme point and returns the index of the List and the Point in that list.
     * The latter will always be 0 because of the property of Jarvis March Algorithm. i.e. It returns the points in
     * Counter Clock-wise order starting from the left-bottom most (extreme point). */
    static List<Integer> findExtreme(List<List<Point>> points) {
        List<Integer> extreme_index = new ArrayList<Integer>();
        Point p = points.get(0).get(0);
        int index=0;
        for(int i=1;i<points.size();i++) {
            double x = points.get(i).get(0).getX();
            double y = points.get(i).get(0).getY();
            if(x < p.getX() || (x == p.getX() && y<p.getY())) {
                p.setX(x);
                p.setY(y);
                index=i;
            }
            iterate += 1;
        }
        extreme_index.add(index);
        extreme_index.add(0);
        return extreme_index;
    }

    /* Performs binary search on a sub-Convex hull to check which half contains the correct tangent. The necessary condition for this
     * is that the points in the sub-Convex hull must be in Counter Clockwise order. */
    static int findTangent(List<Point> points, Point p) {
        int left=0;
        int mid=0;
        int right=points.size();
        int prev_turn=0;
        int next_turn=0;
        int mid_prev_turn=0;
        int mid_next_turn=0;
        int mid_side_turn=0;
        int sz = points.size();
        if(sz-1 >= 0) {
            prev_turn = Helper.orientation(p, points.get(0), points.get(sz-1));
        }
        next_turn = Helper.orientation(p, points.get(0), points.get((left+1)%right));
        while(left<right) {
            mid=(left+right)/2;
            if(((mid-1) % sz) >= 0)
                mid_prev_turn = Helper.orientation(p, points.get(mid), points.get((mid - 1) % sz));
            else
                mid_prev_turn = Helper.orientation(p, points.get(mid), points.get(sz-1));
            mid_next_turn = Helper.orientation(p, points.get(mid), points.get((mid+1)% sz));
            mid_side_turn = Helper.orientation(p, points.get(left), points.get(mid));
            if(mid_prev_turn != -1 && mid_next_turn != -1) {
                return mid;
            }
            else if(mid_side_turn == 1 && (next_turn == -1 || prev_turn == next_turn) || mid_side_turn == -1 && mid_prev_turn == -1) {
                right=mid;
            }
            else {
                left = mid+1;
                prev_turn = -mid_next_turn;
                if(left<sz)
                    next_turn = Helper.orientation(p, points.get(left), points.get((left+1) % sz));
                else
                    return -1;
            }
            iterate += 1;
        }
        return left;
    }

    /* Finds the next Point to be in the final Convex hull by finding tangents from the previous point in
     * the Convex hull (initially the extreme point) to all other sub-Convex hulls and choosing the correct one */
    static List<Integer> nextPoint(List<List<Point>> points, List<Integer> list) {
        Point p = points.get(list.get(0)).get(list.get(1));
        List<Integer> next = new ArrayList<Integer>();
        next.add(list.get(0));
        int temp = ((list.get(1)+1) % points.get(list.get(0)).size());
        int j=0;
        Point q;
        Point r;
        next.add(temp);
        for(int i=0;i<points.size();i++) {
            if(i != list.get(0)) {
                j=findTangent(points.get(i), p);
                if(j == -1)
                    continue;
                q = points.get(next.get(0)).get(next.get(1));
                r = points.get(i).get(j);
                int turn = Helper.orientation(p, q, r);
                double dist = Helper.compare(Helper.dist(p,r),Helper.dist(p,q));
                if(turn == -1 || turn == 0 && dist == 1) {
                    next.set(0, i);
                    next.set(1, j);
                }
            }
            iterate += 1;
        }
        return next;
    }

    /* Given a set of points, computes the convex hull */
    public static List<Point> convexHull(List<Point> points) {
        JarvisMarch is = new JarvisMarch();
        List<Point> sub = new ArrayList<Point>();
        int m=1,t=0;
        List<Point> result = new ArrayList<Point>();
        List<Point> res = new ArrayList<Point>();
        List<List<Point>> input = new ArrayList<List<Point>>();
        while((points.size()/m)>m)
        {
            t++;
            m = power(2,power(2,t));
            iterate+=1;
        }
        int noOfGroups = (points.size())/m;
        if(((points.size()) % m) != 0)
            noOfGroups +=1;
        for(;m<points.size();) {
            for(int i=0,k=0;i<noOfGroups;i++,k=k+m) {
                if(k <= points.size()-m) {
                    sub = points.subList(k, k+m);
                }
                else {
                    sub = points.subList(k, points.size());
                }
                res=is.convexHull(sub);
                input.add(res);
                iterate += is.iterate + 1;
            }
            List<Integer> list = new ArrayList<Integer>();
            list = findExtreme(input);
            result.add(input.get(list.get(0)).get(list.get(1)));
            for(int x=0;x<m;x++) {
                list = nextPoint(input, list);
                if(result.get(0) == input.get(list.get(0)).get(list.get(1))) {
                    return result;
                }
                result.add(input.get(list.get(0)).get(list.get(1)));
                iterate += 1;
            }
            t++;
            m = power(2,power(2,t));
        }
        return result;
    }


}
