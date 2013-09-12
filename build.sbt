import AssemblyKeys._

organization := "com.fotopedia"

name := "yui-compressor-server"

libraryDependencies += "com.yahoo.platform.yui" % "yuicompressor" % "2.4.7"

libraryDependencies += "net.databinder" %% "unfiltered-filter" % "0.6.7"

libraryDependencies += "net.databinder"            %%  "unfiltered-jetty"          % "0.6.7" exclude("org.eclipse.jetty", "jetty-webapp")

libraryDependencies += "org.eclipse.jetty"         %   "jetty-webapp"              % "7.6.10.v20130312"

libraryDependencies += "ch.qos.logback"            %   "logback-classic"           % "0.9.29"

seq(Revolver.settings: _*)

assemblySettings

mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) =>
  {
    case PathList("javax", "servlet", xs @ _*)         => MergeStrategy.first
    case PathList("org", "mozilla", xs @ _*)           => MergeStrategy.first
    case PathList(ps @ _*) if ps.last endsWith ".html" => MergeStrategy.first
    case x => old(x)
  }
}
