/*
 * Created on Dec 8, 2004
 *
 *   Copyright (c) Rich Hickey. All rights reserved.
 *   The use and distribution terms for this software are covered by the
 *   Common Public License 1.0 (http://opensource.org/licenses/cpl.php)
 *   which can be found in the file CPL.TXT at the root of this distribution.
 *   By using this software in any fashion, you are agreeing to be bound by
 * 	 the terms of this license.
 *   You must not remove this notice, or any other, from this software.
 */
package com.richhickey.foil;
import java.util.*;
import java.io.*;
/**
 * @author Rich
 *
 */
public interface IReflector
    {
    /**
     * @author Rich
     *
     */
    ICallable getCallable(int memberType, Class c, String memberName) throws Exception;
    Object createNew(Class c, List args) throws Exception;
    void reflect(Class c,Writer w) throws Exception;
    Object createVector(Class c,int length,List inits) throws Exception;
    Object vectorGet(Object v,int index) throws Exception;
    void vectorSet(Object v,int index,Object val) throws Exception;
    Object vectorLength(Object v) throws Exception;
	void setProps(Object o, List nameValuePairs) throws Exception;

    }
