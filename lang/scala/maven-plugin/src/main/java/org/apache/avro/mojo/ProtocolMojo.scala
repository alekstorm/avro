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

import org.apache.avro.generic.GenericData.StringType
import java.io.File
import java.io.IOException
import org.apache.avro.Protocol
import org.apache.avro.compiler.specific.SpecificCompiler

/**
 * Generate Java classes and interfaces from Avro protocol files (.avpr)
 *
 * @goal protocol
 * @phase generate-sources
 */
class ProtocolMojo extends AbstractAvroMojo {
  protected def doCompile(filename: String, sourceDirectory: File, outputDirectory: File) {
    val src: File = new File(sourceDirectory, filename)
    val protocol: Protocol = Protocol.parse(src)
    val compiler: SpecificCompiler = new SpecificCompiler(protocol)
    compiler.setTemplateDir(templateDirectory)
    compiler.setStringType(StringType.valueOf(stringType))
    compiler.compileToDestination(src, outputDirectory)
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
   * <code>**&#47;*.avpr</code> is used to select grammar files.
   *
   * @parameter
   */
  private var includes: Array[String] = Array[String]("**/*.avpr")
  /**
   * A set of Ant-like inclusion patterns used to select files from the source
   * directory for processing. By default, the pattern
   * <code>**&#47;*.avpr</code> is used to select grammar files.
   *
   * @parameter
   */
  private var testIncludes: Array[String] = Array[String]("**/*.avpr")
}