/*! Xiaofan Li
 *  Summer 2014
 *  application specific stub obj
 */

import java.util.ArrayList;
import java.lang.Class;
import java.lang.reflect.*;



public class MultServerStub {
    //copy of the actual object
    private Mult so;
    private ArrayList<Object> params;
    private int x;
    private int y;


    public MultServerStub (){
        this.so = new Mult();
    }

    public void putArgs(ArrayList<Object> params) throws Exception {
        if (params.size() != 2) throw new Exception("wrong num of args");
        else {
            this.params = params;
            this.x = Integer.parseInt((String)params.get(0));
            this.y = Integer.parseInt((String)params.get(1));
            System.out.println("parsed two arguments: " + this.x);
        }
    }


    public ArrayList<Object> execute (String methodName){
        Method method = null;
        ArrayList<Object> result = new ArrayList<Object>();
        try {
            method = this.so.getClass().getDeclaredMethod(methodName, int.class, int.class);
        } catch (SecurityException e) {
          System.out.println(e);
        } catch (NoSuchMethodException e) {
          System.out.println(e);
        }

        try {
            result.add(method.invoke(this.so,this.x,this.y));
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
