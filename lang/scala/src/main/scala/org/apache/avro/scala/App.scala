package org.apache.avro.scala

import java.io.{File, FilenameFilter}

import org.apache.commons.io.FileUtils

import org.apache.avro.{Protocol => JProtocol, Schema => JSchema}

import org.apache.avro.compiler.idl.Idl

object CompilerApp extends App {
  import CodeStringProcessor._

  if (args.size < 3) {
    printHelp()
    sys.exit(1)
  }

  abstract class InputType(val extension: String)
  case object SchemaInput extends InputType("avsc")
  case object ProtocolInput extends InputType("avpr")
  case object IdlInput extends InputType("avdl")
  val inputType = args(0) match {
    case "schema" => SchemaInput
    case "protocol" => ProtocolInput
    case "idl" => IdlInput
    case _ => {
      println("Must specify either 'schema' or 'protocol'")
      printHelp()
      sys.exit(1)
    }
  }

  val fileArgs = args.drop(1).map { path =>
    val f = new File(path)
    if (!f.exists) {
      println("CompilerApp: %s: No such file or directory".format(f.getPath))
      sys.exit(2)
    }
  }

  val outDir = new File(args(1))
  require(outDir.isDirectory && outDir.exists)
  val inPaths = args.drop(2)
  val inObjs = inPaths.map(new File(_))
  val inFiles = inObjs.filter(_.isFile)
  val inDirs = inObjs.filter(_.isDirectory)

  compileAndWrite(outDir, inFiles, inputType)
  for (inDir <- inDirs) {
    println(inDir + ":")
    compileAndWrite(outDir, inDir, inputType)
  }

  def compileAndWrite(outDir: File, inDir: File, inputType: InputType) {
    require(inDir.exists, inDir)
    object filter extends FilenameFilter {
      override def accept(dir: File, name: String): Boolean =
        name.endsWith(".%s" format inputType.extension)
    }
    val inFiles = inDir.listFiles(filter)
    compileAndWrite(outDir, inFiles, inputType)
  }

  def compileAndWrite(outDir: File, inFiles: Seq[File], inputType: InputType) {
    for (inFile <- inFiles) {
      val name = inFile.getName.stripSuffix(".%s".format(inputType.extension))
      val scalaFile = new File(outDir, "%s.scala".format(name)) // TODO(alek): handle collisions from files with same input name (but different directories) being written to same output directory
      val scalaSource = inputType match {
        case SchemaInput => Compiler.compileSchema(Schema.fromJava(new JSchema.Parser().parse(inFile)))
        case ProtocolInput => Compiler.compileProtocol(Protocol.fromJava(JProtocol.parse(inFile)))
        case IdlInput => Compiler.compileProtocol(Protocol.fromJava(new Idl(inFile).CompilationUnit))
      }
      FileUtils.writeStringToFile(scalaFile, scalaSource, "UTF-8")
    }
  }

  def printHelp() {
    println("Usage: CompilerApp <schema|protocol> OUTDIR PATH...")
  }
}
