yuicompressor-server
====================

## What ?

[YUICompressor](http://yui.github.io/yuicompressor/) is an effective way to minify javascripts and CSS files. However, due to the fact it's running inside a JVM, its startup and warm time can be a huge cost when generating several minified assets in parallel.

yuicompress-server embeds a working YUICompressor inside a Jetty HTTP server to provide a simple but efficient Compressor as a Service. Once warm, the compressor will perform almost twice as fast as when cold-running.

## Use case ?

Instead of minifiying your files in batch (thanks to the `-o -minifiable.js:.js` option of YUICompressor) or using paralell runs of YUICompressor to maximize the CPU usage, just fire several threads that will run HTTP requests to the service. The performance bonus is instantly available... 

For example on our data set (running on a VM with 4 CPUS and 4GB of free memory):

- First run using yuicompressor-server (called with 5 threads): 9s
- Second run using yuicompressor-server (called with 5 threads): __7s__
- Run using `-o -minifiable.js:.js`: __57s__

So, who's your daddy now ?

## Run it ...

Provided you have a working Java environment, just type:

```
./sbt run
[....]
[info] Loading project definition from /yuicompressor-server/project
[info] Set current project to yui-compressor-server (in build file:yuicompressor-server/)
[info] Running com.fotopedia.yui.Server
19:07:20.010 [run-main] INFO  org.eclipse.jetty.server.Server - jetty-7.6.10.v20130312
19:07:20.058 [run-main] INFO  o.e.j.server.handler.ContextHandler - started o.e.j.s.ServletContextHandler{/,null}
19:07:20.090 [run-main] INFO  o.e.jetty.server.AbstractConnector - Started SelectChannelConnector@0.0.0.0:2013
Embedded server running on port 2013. Press any key to stop.
```

## ... or build a fat jar.

```
./sbt assembly
```

## Usage

- Post your file to the / endpoint
- Do not omit the content-type (`text/css` or `text/javascript`) header along with the charset

```
cat mysourcefile.css | curl -X POST --data-binary @- -H "content-type: text/css" -v http://localhost:2013/
```

## What else ?

- Written by kali@fotopedia following some experimentation by oct@fotopedia
- Fork, branch, commit, pull request to improve.
