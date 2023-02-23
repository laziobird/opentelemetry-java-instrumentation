val stableVersion = "1.24.0-jiangzhiwei"
val alphaVersion = "1.24.0-alpha-jiangzhiwei"

allprojects {
  if (findProperty("otel.stable") != "true") {
    version = alphaVersion
  } else {
    version = stableVersion
  }
}
