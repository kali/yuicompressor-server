package com.fotopedia.yui

import javax.servlet.http._

import org.eclipse.jetty.server.{Server, Handler, Request}
import org.eclipse.jetty.server.handler.AbstractHandler
import org.eclipse.jetty.http.HttpHeaders

import org.mozilla.javascript.ErrorReporter
import org.mozilla.javascript.EvaluatorException

import com.yahoo.platform.yui.compressor._

class MyErrorReporter extends ErrorReporter {
    val errorBuffer = scala.collection.mutable.Buffer[String]()

    def error(message:String, sourceName:String, line: Int,lineSource:String, lineOffset:Int): Unit = {
        errorBuffer += s"$message line $line:\n$lineSource\n\n"
    }
    def runtimeError(message: String,x$2: String,x$3: Int,x$4: String,x$5: Int): org.mozilla.javascript.EvaluatorException =
        return new EvaluatorException(message)
    def warning(x$1: String,x$2: String,x$3: Int,x$4: String,x$5: Int): Unit = {
    }
}

object App extends AbstractHandler {

    def main(args: Array[String]) {
        val server = new Server(2013);
        server.setHandler(this)
        server.start();
        server.join();
    }

    override def handle(target:String,baseRequest:Request,req:HttpServletRequest,resp:HttpServletResponse) {
        val reader = req.getReader
        val reporter = new MyErrorReporter
        val typ:String = req.getHeader(HttpHeaders.CONTENT_TYPE)
        baseRequest.setHandled(true)
        try {
            if(typ.startsWith("text/javascript")) {
                resp.setStatus(HttpServletResponse.SC_OK)
                resp.setHeader(HttpHeaders.CONTENT_TYPE, "text/javascript")
                resp.setCharacterEncoding(req.getCharacterEncoding)
                new JavaScriptCompressor(reader, reporter).compress(resp.getWriter, -1, true, true, false, false)
            } else if(typ.startsWith("text/css")) {
                resp.setStatus(HttpServletResponse.SC_OK)
                resp.setHeader(HttpHeaders.CONTENT_TYPE, "text/css")
                resp.setCharacterEncoding(req.getCharacterEncoding)
                new CssCompressor(reader).compress(resp.getWriter, -1)
            } else
                resp.setStatus(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE)
        } catch {
            case _:Throwable =>
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST)
                resp.getWriter.print(reporter.errorBuffer.mkString)
        }
    }
}
