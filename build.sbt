
lazy val root = (project in file(".")).
  aggregate(master, client1, client2, backend1, backend2)

lazy val master = project in file("master")

lazy val client1 = project in file("client1")

lazy val client2 = project in file("client2")

lazy val backend1 = project in file("backend1")

lazy val backend2 = project in file("backend2")


