package org.apache.avro.scala

import java.io.{File, FilenameFilter}

import org.apache.commons.io.FileUtils

import org.apache.avro.{Protocol => JProtocol, Schema => JSchema}
import org.apache.avro.compiler.idl.Idl

object CompilerApp extends App {
  abstract class InputType(val extension: String)
  case object SchemaInput extends InputType("avsc")
  case object ProtocolInput extends InputType("avpr")
  case object IdlInput extends InputType("avdl")

  if (args.size < 3) {
    printHelp()
    sys.exit(1)
  }

  val inputType = args(0) match {
    case "schema" => SchemaInput
    case "protocol" => ProtocolInput
    case "idl" => IdlInput
    case _ => {
      println("Must specify a valid file format")
      printHelp()
      sys.exit(1)
    }
  }

  def checkExists(file: File) {
    if (!file.exists) {
      println(s"${file.getPath}: No such file or directory")
      sys.exit(2)
    }
  }

  val outDir = new File(args(1))
  val inObjs = args.drop(2).map(new File(_))
  (outDir +: inObjs).foreach(checkExists)
  inObjs.filter(_.isFile).foreach(file => compileAndWrite(outDir, file, inputType))
  inObjs.filter(_.isDirectory).foreach(dir => compileAndWriteDir(outDir, dir, inputType))

  def compileAndWrite(outDir: File, inFile: File, inputType: InputType) {
    val output = inputType match {
      case SchemaInput => compiler.compileSchema(Schema.fromJava(new JSchema.Parser().parse(inFile)))
      case ProtocolInput => compiler.compileProtocol(Protocol.fromJava(JProtocol.parse(inFile)))
      case IdlInput => compiler.compileProtocol(Protocol.fromJava(new Idl(inFile).CompilationUnit))
    }
    // TODO(alek): handle collisions from files with same input name (but different directories) being written to same output directory
    val name = ("\\." + inputType.extension + "$").r.replaceAllIn(inFile.getName, ".scala")
    FileUtils.writeStringToFile(new File(outDir, name), output, "UTF-8")
  }

  def compileAndWriteDir(outDir: File, inDir: File, inputType: InputType) {
    val inFiles = inDir.listFiles(new FilenameFilter {
      override def accept(dir: File, name: String) = name.endsWith("." + inputType.extension)
    })
    inFiles.foreach(file => compileAndWrite(outDir, file, inputType))
  }

  def printHelp() {
    println("Usage: CompilerApp <schema|protocol|idl> OUTDIR PATH...")
  }
}
