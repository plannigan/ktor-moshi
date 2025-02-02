import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int
import kotlin.system.exitProcess


fun main(args: Array<String>) = SampleCli().subcommands(ClientCli(), ServerCli()).main(args)

class SampleCli : CliktCommand() {
  override fun run() {
  }
}

class ClientCli : CliktCommand(name="client") {
  private val host : String by option(help = "Host to connect to", metavar = "HOSTNAME").default("localhost")
  private val port : Int by option(help = "Port to connect to", metavar = "PORT").int().default(PORT)
  private val check : Boolean by option(help="Should the responses be validated").flag("--no-check")

  override fun run() {
    exitProcess(runClient(check, host, port))
  }

  override fun help(context: Context): String {
    return "Use the client to connect to the server"
  }
}

class ServerCli : CliktCommand(name="server") {
  private val port : Int by option(help = "Port to connect to", metavar = "PORT").int().default(PORT)

  override fun run() {
    exitProcess(runServer(port))
  }

  override fun help(context: Context): String {
    return "Start the server"
  }
}

