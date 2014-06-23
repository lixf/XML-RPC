/*! Xiaofan Li
 *  Summer 2014
 *  application specific stub obj
 */

import java.util.ArrayList;
import java.lang.Class;
import java.lang.reflect.*;



public class SumServerStub {
    //copy of the actual object
    private SumObj so;
    private ArrayList<Object> params;
    private int x;
    private int y;


    public SumServerStub (){
        this.so = new SumObj();
    }

    public void putArgs(ArrayList<Object> params) throws Exception {
        if (params.size() != 2) throw new Exception("wrong num of args");
        else {
            this.params = params;
            this.x = (Integer)params.get(0);
            this.y = (Integer)params.get(1);
        }
    }


    public ArrayList<Object> execute (String methodName){
        Method method = null;
        ArrayList<Object> result = new ArrayList<Object>();
        try {
            method = this.so.getClass().getMethod(methodName, int.class, int.class);
        } catch (SecurityException e) {
        } catch (NoSuchMethodException e) {
        }

        try {
            result.add((Object)method.invoke(this.so,this.x, this.y));
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
        }
        return result;
    }

    public ArrayList<String> getTypes (){
        ArrayList<String> types = new ArrayList<String>();
        types.add("int");
        types.add("int");
        return types;
    }
}
