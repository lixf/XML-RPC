/*! Xiaofan Li
 *  Summer 2014
 *  application specific stub obj
 */

import java.util.ArrayList;
import java.lang.Class;
import java.lang.reflect.*;



public class ConServerStub {
    //copy of the actual object
    private Con so;
    private ArrayList<Object> params;
    private String s1;
    private String s2;
    private boolean b;


    public ConServerStub (){
        this.so = new Con();
    }

    public void putArgs(ArrayList<Object> params) throws Exception {
        if (params.size() != 3) throw new Exception("wrong num of args");
        else {
            this.params = params;
            this.s1 = (String)params.get(0);
            this.s2 = (String)params.get(1);
            this.b = Boolean.valueOf((String)params.get(2));
            System.out.println("parsed one arguments: " + this.s1 +this.s2);
        }
    }


    public ArrayList<Object> execute (String methodName){
        Method method = null;
        ArrayList<Object> result = new ArrayList<Object>();
        try {
            method = this.so.getClass().getDeclaredMethod(methodName,String.class,String.class,boolean.class);
        } catch (SecurityException e) {
          System.out.println(e);
        } catch (NoSuchMethodException e) {
          System.out.println(e);
        }

        try {
            result.add(method.invoke(this.so,this.s1,this.s2,this.b));
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
        types.add("string");
        return types;
    }
}
