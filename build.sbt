import AssemblyKeys._

organization := "com.fotopedia"

name := "yui-compressor-server"

libraryDependencies ++= Seq(
    "com.yahoo.platform.yui" % "yuicompressor" % "2.4.7",
    "org.eclipse.jetty"         %   "jetty-server"              % "7.6.10.v20130312",
    "org.eclipse.jetty"         %   "jetty-http"                % "7.6.10.v20130312"
)

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
