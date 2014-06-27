/*! Xiaofan Li
 *  Summer 2014
 *  application specific stub obj
 */

import java.util.ArrayList;
import java.lang.Class;
import java.lang.reflect.*;



public class FibServerStub {
    //copy of the actual object
    private Fib so;
    private ArrayList<Object> params;
    private int x;


    public FibServerStub (){
        this.so = new Fib();
    }

    public void putArgs(ArrayList<Object> params) throws Exception {
        if (params.size() != 1) throw new Exception("wrong num of args");
        else {
            this.params = params;
            this.x = Integer.parseInt((String)params.get(0));
            System.out.println("parsed one arguments: " + this.x);
        }
    }


    public ArrayList<Object> execute (String methodName){
        Method method = null;
        ArrayList<Object> result = new ArrayList<Object>();
        try {
            method = this.so.getClass().getDeclaredMethod(methodName,int.class);
        } catch (SecurityException e) {
          System.out.println(e);
        } catch (NoSuchMethodException e) {
          System.out.println(e);
        }

        try {
            result.add(method.invoke(this.so,this.x));
        } catch (IllegalArgumentException e) {
          System.out.println(e);
        } catch (IllegalAccessException e) {
          System.out.println(e);
        } catch (InvocationTargetException e) {
          System.out.println(e);
        }
        return result;
    }

    public ArrayList<String> getTypes (){
        ArrayList<String> types = new ArrayList<String>();
        types.add("int");
        return types;
    }
}
