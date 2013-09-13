package com.fotopedia.yui

import unfiltered.request._
import unfiltered.response._
import unfiltered.jetty._
import unfiltered.filter.Intent

import org.slf4j.LoggerFactory

import org.eclipse.jetty.server.{Server => JettyServer, Connector, Handler}
import org.eclipse.jetty.server.nio.SelectChannelConnector
import org.mozilla.javascript.ErrorReporter
import org.mozilla.javascript.EvaluatorException

import com.yahoo.platform.yui.compressor._

case class HttpSelect(port: Int, host: String) extends Server {
  type ServerBuilder = HttpSelect
  val url = "http://%s:%d/" format (host, port)
  val conn = new SelectChannelConnector()
  conn.setPort(port)
  conn.setHost(host)
  underlying.addConnector(conn)
}

object Server {
  val logger = LoggerFactory.getLogger(Server.getClass)
  def main(args: Array[String]) {
    val http = HttpSelect(2013, "0.0.0.0")
    http.filter(new App).run()
  }
}

class MyErrorReporter extends ErrorReporter {
    val errorBuffer = scala.collection.mutable.Buffer[String]()

    def error(message:String, sourceName:String, line: Int,lineSource:String, lineOffset:Int): Unit = {
        errorBuffer += "$message line $line:\n$lineSource\n\n"
    }
    def runtimeError(message: String,x$2: String,x$3: Int,x$4: String,x$5: Int): org.mozilla.javascript.EvaluatorException =
        return new EvaluatorException(message)
    def warning(x$1: String,x$2: String,x$3: Int,x$4: String,x$5: Int): Unit = {
    }
}

class App extends unfiltered.filter.Plan {
    def intent = Intent {
        case req@POST(Path("/") & RequestContentType(typ)) => {
            val reader = req.reader
            val out = new java.io.StringWriter()
            val reporter = new MyErrorReporter
            try {
                if(typ.startsWith("text/javascript")) {
                    new JavaScriptCompressor(reader, reporter).compress(out, -1, true, true, false, false)
                    Ok ~> ResponseString(out.toString())
                } else if(typ.startsWith("text/css")) {
                    new CssCompressor(reader).compress(out, -1)
                    Ok ~> ResponseString(out.toString())
                } else
                    BadRequest ~> ResponseString("invalid content type\n")
            } catch {
                case _:Throwable => BadRequest ~> ResponseString(reporter.errorBuffer.mkString)
            }
        }
    }
}
