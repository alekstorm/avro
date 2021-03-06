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
package org.apache.avro.scala

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite

import org.apache.avro.scala.test.generated.scala.{Contained, Container, RecordWithAllTypes}

@RunWith(classOf[JUnitRunner])
class TestToMutable
  extends FunSuite {

  test("toMutable") {
    val rImm = Fixtures.recordWithAllTypes("a")
    val rMut = rImm.toMutable
    assert(rImm === rMut)

    // ensure that altering mutable record's fields does not alter the
    // immutable record's fields
    rMut.stringField = "b"
    assert(rMut.stringField === "b")
    assert(rImm.stringField === "a")
  }

  test("modify nested mutable record built from immutable record") {
    val rImm = new Container(contained = new Contained(1))
    val rMut = rImm.toMutable
    assert(rImm === rMut)

    rMut.contained.data = 2

    assert(rImm.contained.data === 1)
    assert(rMut.contained.data === 2)
    assert(rImm != rMut)

    assert(rMut.build.contained.data === 2)
  }

}
