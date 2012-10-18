/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.avro.scala;

import java.io.*;
import java.net.*;
import java.util.*;

public class ScalaNamespaceSuffixSchemaClassLoader extends ClassLoader {

  public ScalaNamespaceSuffixSchemaClassLoader() { }

  protected Class findClass(String name) throws ClassNotFoundException {
    Class c = null;
    int lastDot = name.lastIndexOf('.');
    if (lastDot != -1) {
      String prefix = "Mutable";
      String newName = name.substring(0, lastDot) + ".scala." + prefix + name.substring(lastDot + 1);
      c = Thread.currentThread().getContextClassLoader().loadClass(newName);
    }
    return c;
  }
}
