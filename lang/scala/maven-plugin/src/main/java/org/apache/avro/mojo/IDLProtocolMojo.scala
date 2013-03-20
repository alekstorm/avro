/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.avro.mojo

import java.io.File
import java.io.IOException
import java.net.URL
import java.net.URLClassLoader
import java.util.List
import org.apache.avro.Protocol
import org.apache.avro.compiler.idl.Idl
import org.apache.avro.compiler.idl.ParseException
import org.apache.avro.compiler.specific.SpecificCompiler
import org.apache.avro.generic.GenericData
import org.apache.maven.artifact.DependencyResolutionRequiredException

/**
 * Generate Java classes and interfaces from AvroIDL files (.avdl)
 *
 * @goal idl-protocol
 * @requiresDependencyResolution runtime
 * @phase generate-sources
 */
class IDLProtocolMojo extends AbstractAvroMojo {
  protected def doCompile(filename: String, sourceDirectory: File, outputDirectory: File) {
    try {
      @SuppressWarnings(Array("rawtypes")) val runtimeClasspathElements: List[_] = project.getRuntimeClasspathElements
      val runtimeUrls: Array[URL] = new Array[URL](runtimeClasspathElements.size)
      {
        var i: Int = 0
        while (i < runtimeClasspathElements.size) {
          {
            val element: String = runtimeClasspathElements.get(i).asInstanceOf[String]
            runtimeUrls(i) = new File(element).toURI.toURL
          }
          ({
            i += 1; i - 1
          })
        }
      }
      val projPathLoader: URLClassLoader = new URLClassLoader(runtimeUrls, Thread.currentThread.getContextClassLoader)
      val parser: Idl = new Idl(new File(sourceDirectory, filename), projPathLoader)
      val p: Protocol = parser.CompilationUnit
      val json: String = p.toString(true)
      val protocol: Protocol = Protocol.parse(json)
      val compiler: SpecificCompiler = new SpecificCompiler(protocol)
      compiler.setStringType(GenericData.StringType.valueOf(stringType))
      compiler.setTemplateDir(templateDirectory)
      compiler.compileToDestination(null, outputDirectory)
    }
    catch {
      case e: ParseException => {
        throw new IOException(e)
      }
      case drre: Nothing => {
        throw new IOException(drre)
      }
    }
  }

  protected def getIncludes: Array[String] = {
    return includes
  }

  protected def getTestIncludes: Array[String] = {
    return testIncludes
  }

  /**
   * A set of Ant-like inclusion patterns used to select files from the source
   * directory for processing. By default, the pattern
   * <code>**&#47;*.avdl</code> is used to select IDL files.
   *
   * @parameter
   */
  private var includes: Array[String] = Array[String]("**/*.avdl")
  /**
   * A set of Ant-like inclusion patterns used to select files from the source
   * directory for processing. By default, the pattern
   * <code>**&#47;*.avdl</code> is used to select IDL files.
   *
   * @parameter
   */
  private var testIncludes: Array[String] = Array[String]("**/*.avdl")
}