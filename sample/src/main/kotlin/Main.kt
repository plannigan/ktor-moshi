import kotlin.system.exitProcess


// server: start server
// client: use client to interact with server
// client check: use client to interact with server and assert responses
fun main(args: Array<String>) {
    exitProcess(run(args))
}

fun run(args: Array<String>) : Int {
  if (args.isEmpty()) {
    println("Command not specified")
    return ExitCodes.BAD_ARG
  }
  return when(args[0]) {
    "server" -> runServer()
    "client" -> runClient(args.clientCheckArg())
    else -> {
      println("""Command must be "server" or "client""")
      return ExitCodes.BAD_ARG
    }
  }
}

fun Array<String>.clientCheckArg() = when(size) {
  1 -> false
  else -> this[1] == "check"
}
