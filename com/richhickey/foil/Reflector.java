/*
 * Created on Dec 11, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.richhickey.foil;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.*;
import java.util.*;
import java.beans.*;
/**
 * @author Rich
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class Reflector implements IReflector
    {
    IBaseMarshaller baseMarshaller;
    public Reflector(IBaseMarshaller baseMarshaller)
        {
        this.baseMarshaller = baseMarshaller;
        }
    /**
     * @author Rich
     *
     * TODO To change the template for this generated type comment go to
     * Window - Preferences - Java - Code Style - Code Templates
     */
    public class CallableField implements ICallable
        {
        Field field;
        public CallableField(Field field)
            {
            this.field = field;
            }
        
        /* (non-Javadoc)
         * @see com.richhickey.foil.ICallable#invoke(java.lang.Object, java.util.List)
         */
        public Object invoke(Object target, List args)
                throws InvocationTargetException
            {
            try{
                if(args.size() == 0) //get
                    {
                    return field.get(target);
                    }
                else if(args.size() == 1) //set
                    {
                    field.set(target,boxArg(field.getType(),args.get(0)));
                    return null;
                    }
                else 
                    throw new Exception("invalid number of args passed to field");
            	}
            catch(Exception ex)
            	{	
            	throw new InvocationTargetException(ex);
            	}
            }

        }
    
    public class CallableMethod implements ICallable
        {
        List methods;
        public CallableMethod(List methods)
            {
        	this.methods = methods;
            }
        
        public Object invoke(Object target, List args) throws InvocationTargetException
            {
//            Object target = args.get(0);
//            args = args.subList(1,args.size());
            for(Iterator i = methods.iterator();i.hasNext();)
                {
                Method m = (Method)i.next();
                
                Class[] params = m.getParameterTypes();
                if(isCongruent(params,args))
                    {
                    Object[] boxedArgs = boxArgs(params,args);
                    try{
                        return m.invoke(target,boxedArgs);
                    	}
                    catch(Exception ex)
                    	{	
                    	throw new InvocationTargetException(ex);
                    	}
                    }
                }
            throw new InvocationTargetException(new Exception("no matching function found"));
            }
        }

    static Object[] boxArgs(Class[] params,List args)
        {
        if(params.length == 0)
            return null;
        Object[] ret = new Object[params.length];
        for(int i=0;i<params.length;i++)
            {
            Object arg = args.get(i);
            Class paramType = params[i]; 
            ret[i] = boxArg(paramType,arg);
            }
        return ret;
        }
    
    static Object boxArg(Class paramType,Object arg)
        {
        if(paramType == boolean.class && arg == null)
                return Boolean.FALSE;
        else
            return arg;
        }
    
    static boolean isCongruent(Class[] params,List args)
        {
        boolean ret = false;
        if((params.length == 0 && args == null)
                || params.length == args.size())
            {
            ret = true;
            for(int i=0;ret && i<params.length;i++)
                {
                Object arg = args.get(i);
                Class argType = (arg == null)?null:arg.getClass();
                Class paramType = params[i]; 
                if(paramType == boolean.class)
                    {
                    ret = arg == null || argType == Boolean.class;
                    }
                else if(paramType.isPrimitive())
                    {
                    if(arg == null)
                        ret = false;
                    else if(paramType == int.class)
                        ret = argType == Integer.class;
                    else if(paramType == double.class)
                        ret = argType == Double.class;
                    else if(paramType == long.class)
                        ret = argType == Long.class;
                    else if(paramType == char.class)
                        ret = argType == Character.class;
                    else if(paramType == short.class)
                        ret = argType == Short.class;
                    else if(paramType == byte.class)
                        ret = argType == Byte.class;
                    }
                else
                    {
                    ret = arg == null 
                    	|| argType == paramType 
                    	|| paramType.isAssignableFrom(argType);
                    }
                }
            }
        return ret;
        }
    
    public ICallable getCallable(int memberType, Class c, String memberName)
            throws Exception
        {
        switch(memberType)
        	{
        	case ICallable.METHOD:
        	    return getMethod(c,memberName);
        	case ICallable.FIELD:
        	    return getField(c,memberName);
        	case ICallable.PROPERTY_GET:
        	    return getPropertyGetter(c,memberName);
        	case ICallable.PROPERTY_SET:
        	    return getPropertySetter(c,memberName);
        	default:
        	    throw new Exception("unsupported member type");
        	}
        }
    
    
    ICallable getMethod(Class c,String method)
    	throws Exception
        {
        Method[] allmethods = c.getMethods();
        ArrayList methods = new ArrayList();
        for(int i=0;i<allmethods.length;i++)
            {
            if(method.equals(allmethods[i].getName()))
                methods.add(allmethods[i]);
            }
        if(methods.size() == 0)
            throw new Exception("no methods found");
        return new CallableMethod(methods);
        }

    ICallable getField(Class c,String field)
	throws Exception
	    {
	    Field[] allfields = c.getDeclaredFields();
	    for(int i=0;i<allfields.length;i++)
	        {
	        if(field.equals(allfields[i].getName()))
			    return new CallableField(allfields[i]);
	        }
        throw new Exception("no field found");
	    }

    ICallable getPropertyGetter(Class c,String property)
	throws Exception
        {
        PropertyDescriptor[] props = Introspector.getBeanInfo(c).getPropertyDescriptors();
        ArrayList methods = new ArrayList();
        for(int i=0;i<props.length;i++)
            {
            if(property.equals(props[i].getName()))
                methods.add(props[i].getReadMethod());
            }
        if(methods.size() == 0)
            throw new Exception("no properties found");
        return new CallableMethod(methods);
        }

    ICallable getPropertySetter(Class c,String property)
	throws Exception
        {
        PropertyDescriptor[] props = Introspector.getBeanInfo(c).getPropertyDescriptors();
        ArrayList methods = new ArrayList();
        for(int i=0;i<props.length;i++)
            {
            if(property.equals(props[i].getName()))
                methods.add(props[i].getWriteMethod());
            }
        if(methods.size() == 0)
            throw new Exception("no properties found");
        return new CallableMethod(methods);
        }

    /* (non-Javadoc)
     * @see com.richhickey.foil.IReflector#createNew(java.lang.Class, java.util.List)
     */
    public Object createNew(Class c, List args) throws Exception
        {
        Constructor[] ctors = c.getConstructors();
        for(int i=0;i<ctors.length;i++)
            {
            Constructor ctor = ctors[i];
            Class[] params = ctor.getParameterTypes();
            if(isCongruent(params,args))
                {
                Object[] boxedArgs = boxArgs(params,args);
                try{
                    return ctor.newInstance(boxedArgs);
                	}
                catch(Exception ex)
                	{	
                	throw new InvocationTargetException(ex);
                	}
                }
            }
        throw new InvocationTargetException(new Exception("no matching ctor found"));
        }

    /* (non-Javadoc)
     * @see com.richhickey.foil.IReflector#reflect(java.lang.Class, java.io.Writer)
     */
    public void reflect(Class c, Writer w) throws Exception
        {
        w.write(" (");
        
        w.write("(:class");
        baseMarshaller.marshallAtom(c,w,IBaseMarshaller.MARSHALL_ID,0);
        w.write(')');

        ArrayList supers = new ArrayList();
        if(c.isInterface())
            supers.add(Object.class);
        else
            supers.add(c.getSuperclass());
        Class[] interfaces = c.getInterfaces();
        for(int i=0;i<interfaces.length;i++)
            {
            Class inter = interfaces[i];
            boolean placed = false;
            for(int p=0;!placed && p<supers.size();p++)
                {
                Class s = (Class)supers.get(p);
                if(s.isAssignableFrom(inter))
                    {
                    supers.add(p,inter);
                    placed =true;
                    }
                }
            if(!placed)
                supers.add(inter);
                
            }
        w.write("(:bases");
        for(int p=0;p<supers.size();p++)
            baseMarshaller.marshallAtom(supers.get(p),w,IBaseMarshaller.MARSHALL_ID,1);
        w.write(')');
        
        Constructor[] ctors = c.getConstructors(); 
        if(ctors.length > 0)
            {
            w.write("(:ctors ");
            for(int i=0;i<ctors.length;i++)
                {
                Constructor ctor = ctors[i];
                Class[] params = ctor.getParameterTypes();
                w.write("(:args");
                for(int p=0;p<params.length;p++)
                    baseMarshaller.marshallAtom(params[p],w,IBaseMarshaller.MARSHALL_ID,1);
                
                w.write(')');
                }
            w.write(')');
            }
        
        w.write(')');
        }
    }
