/*
 *
 *
 *
 */


public class Fib {
    public Fib(){}
    public int fib(int x){
      if (x == 0) {
        return 0;
      }
      else if (x == 1){
        return 1;
      }
      else {
        int last = fib (x-1);
        int before = fib (x-2);
        return (last + before);
      }
    }
}
