package org.apache.avro.scala

object CodeStringProcessor {
  implicit def codeHelper(ctx: StringContext) = new {
    def code(args: Any*): String = {
      val parts = ctx.parts.iterator
      val expressions = args.iterator
      val buf = new StringBuffer()
      def nextPart(): Int = {
        val part = parts.next()
        buf.append(part)
        part.slice(part.lastIndexOf("\n"), part.length).length
      }
      var lastIndent = nextPart()
      while (parts.hasNext) {
        val expression = expressions.next()
        buf.append(if (expression.isInstanceOf[String]) {
          val lines = expression.asInstanceOf[String].split("\n")
          (lines.take(1) ++ lines.drop(1).map(" " * lastIndent + _)).mkString("\n")
        }
        else
          expression)
        lastIndent = nextPart()
      }
      val interpolated = """(^\s*\n)|(\s*$)""".r.replaceAllIn(buf.toString, "")
      val indents = interpolated.split("\n").filter(!_.trim.isEmpty).map("^\\s*".r.findFirstIn(_).get.length)
      val offset = if (indents.size > 0) indents.min else 0
      interpolated.split("\n").map { line => line.slice(offset, line.length) }.mkString("\n")
    }
  }
}
